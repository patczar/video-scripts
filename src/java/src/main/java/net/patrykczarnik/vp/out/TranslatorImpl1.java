package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.commands.CommandScriptImpl;
import net.patrykczarnik.commands.CommandScriptWithOptions;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterGraph;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;
import net.patrykczarnik.ffmpeg.FFMPEG;
import net.patrykczarnik.ffmpeg.FFMap;
import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.ffmpeg.FFOutput;
import net.patrykczarnik.vp.in.VPScriptEntryFile;
import net.patrykczarnik.vp.in.VPScriptEntrySetOptions;
import net.patrykczarnik.vp.in.VPScriptOption;

/**
 * @author Patryk Czarnik
 * 
 * Implementation based on stream concatenation.
 * 
 */
public class TranslatorImpl1 extends TranslatorAbstractImpl {
	private FiltersRegistry filtersRegistry;
	private List<Segment> segments;
	private Segment currentSegment;
	private int nextSegmentNumber, nextInputNumber;
	private CurrentOptions currentOptions;
	private CommandScriptImpl resultScript = null;
	private AVideoProcessor videoProcessor;
	private AAudioProcessor audioProcessor;
	private List<VPScriptEntryFile> allFiles;
	private boolean hasAudio = false;
	
	public TranslatorImpl1(FiltersRegistry filtersRegistry) {
		this.filtersRegistry = filtersRegistry;
	}

	@Override
	public void begin() {
		nextSegmentNumber = 0;
		nextInputNumber = 0;
		allFiles = new ArrayList<>();
		segments = new ArrayList<>();
		currentOptions = new CurrentOptions();
	}

	@Override
	public void end() {
		endSegment();
				
		if(currentOptions.getGlobal().containsKey("dir")) {
			String dir = currentOptions.getGlobal().get("dir").textValue();
			resultScript = CommandScriptImpl.empty().setWorkingDir(dir);
		}
		
		selectAudioProcessor();
		selectVideoProcessor();
		resultScript.add(createAudioCommands());
		
		FFMPEG finalFFMPEG = createFinalFFMPEG();
		resultScript.add(finalFFMPEG);

		resultScript.add(createLastCommands());
	}
	
	private FFMPEG createFinalFFMPEG() {
		int nseg = 0;
		int naudio = 0;
		
		FFMPEG ffmpeg = new FFMPEG();
		applyGlobalOptions(ffmpeg);
		ffmpeg.setFilterGraph(FFFilterGraph.empty());
		
		List<String> lastConcatInputLabels = new ArrayList<>();
		
		for(Segment segment : segments) {
			ffmpeg.addInputs(segment.getInputs());
			ffmpeg.getFilterGraph().addChains(videoProcessor.getCombinedChains(segment));
			lastConcatInputLabels.add(TranslationCommons.segmentLabel(nseg));
			Optional<FFFilterChain> segmentChain = audioProcessor.audioSegmentChain(segment);
			if(segmentChain.isPresent()) {
				ffmpeg.getFilterGraph().addChains(segmentChain.get());
				lastConcatInputLabels.addAll(segmentChain.get().getEndLabels());
				naudio++;
			}
			nseg += 1;
		}
		
		hasAudio = naudio > 0;
		int audioStreams = hasAudio ? 1 : 0;
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", segments.size()),
				FFFilterOption.integer("v", 1),
				FFFilterOption.integer("a", audioStreams));
		
		FFFilterChain concatSegmentsChain = FFFilterChain.withLabels(lastConcatInputLabels, List.of("final"), List.of(concatFilter));
		ffmpeg.getFilterGraph().addChain(concatSegmentsChain);
		
		applyOutputOptions(ffmpeg);
		return ffmpeg;
	}

	private void selectAudioProcessor() {
		String audioProcessorName = Optional.ofNullable(
				currentOptions.getAudio().get("impl"))
				.map(VPScriptOption::textValue).orElse("");
		audioProcessor = AAudioProcessor.getImpl(audioProcessorName, filtersRegistry);
	}

	private void selectVideoProcessor() {
		videoProcessor = new VideoProcessorDefault(filtersRegistry);
	}

	private List<Command> createAudioCommands() {
		List<Command> list = new ArrayList<>();
		if(audioProcessor != null) {
			list.addAll(audioProcessor.commandsBefore());
			for(VPScriptEntryFile file : allFiles) {
				list.addAll(audioProcessor.commandsForEachFile(file));
			}
			for(Segment segment : segments) {
				list.addAll(audioProcessor.commandsForEachSegment(segment));
			}
		}
		return list;
	}

	private List<Command> createLastCommands() {
		List<Command> list = new ArrayList<>();
		if(audioProcessor != null) {
			list.addAll(audioProcessor.commandsAfter());
		}
		return list;
	}

	@Override
	public void acceptSetOptions(VPScriptEntrySetOptions entry) {
		if(currentSegment != null) {
			endSegment();
		}
		currentOptions.update(entry);
	}

	@Override
	public void acceptFile(VPScriptEntryFile entry) {
		if(currentSegment == null) {
			beginSegment();
		}
		
		FFInput newInput = FFInput.forFile(entry.getPath());
		if(entry.getStart() != null) {
			newInput.withOption(FFOption.of("ss", String.valueOf(entry.getStart())));
		}
		if(entry.getEnd() != null) {
			newInput.withOption(FFOption.of("to", String.valueOf(entry.getEnd())));
		}
		currentSegment.addInput(newInput);
		
		nextInputNumber++;
		allFiles.add(entry);
	}

	@Override
	protected CommandScriptWithOptions getResultScript() {
		return resultScript;
	}

	private void beginSegment() {
		currentSegment = new Segment(nextSegmentNumber++, nextInputNumber);
		currentSegment.remeberOptions(currentOptions);
	}

	private void endSegment() {
		segments.add(currentSegment);
		currentSegment = null;
	}

	private void applyGlobalOptions(FFMPEG ffmpeg) {
		for(VPScriptOption vpOption : currentOptions.getGlobal().values()) {
			switch(vpOption.getName()) {
				case "overwrite":
					ffmpeg.addGlobalOptions(FFOption.of("y"));
					break;
			}
		}
		ffmpeg.addGlobalOptions(audioProcessor.ffGlobalOptions());
	}
	
	private void applyOutputOptions(FFMPEG ffmpeg) {
		Map<String, VPScriptOption> outputOptions = currentOptions.getOutput();
		String outputFile;
		if(outputOptions.containsKey("out")) {
			outputFile = outputOptions.get("out").textValue();
		} else {
			outputFile = "out.mp4";
		}
		if(outputOptions.containsKey("fps")) {
			ffmpeg.addGlobalOptions(FFOption.of("r", outputOptions.get("fps").textValue()));
		}
		String codec = "h264";
		if(outputOptions.containsKey("codec")) {
			codec = outputOptions.get("codec").textValue();
		}
		
		FFOutput ffOutput = FFOutput.forFile(outputFile);
		ffOutput.withOption(FFOption.of("c:v", codec));
		Map<String, String> optionsMapping = new TreeMap<>(Map.of(
				"profile", "profile:v",
				"preset", "preset:v",
				"crf", "crf",
				"bitrate", "b:v"
		));
		if(codec.contains("nvenc")) {
			optionsMapping.put("crf", "cq:v");
			ffOutput.withOption(FFOption.of("rc:v", "vbr_hq"));
			if(!outputOptions.containsKey("bitrate")) {
				ffOutput.withOption(FFOption.of("b:v", "40m"));
			}
			ffOutput.withOption(FFOption.of("maxrate:v", "50m"));
		}
		outputOptions.forEach((key, option) -> {
			if(optionsMapping.containsKey(key)) {
				ffOutput.withOption(option.toFFOption(optionsMapping.get(key)));
			}
		});
		ffOutput.withMap(FFMap.ofLabel("final"));
		ffmpeg.addOutputs(ffOutput);
	}

}
