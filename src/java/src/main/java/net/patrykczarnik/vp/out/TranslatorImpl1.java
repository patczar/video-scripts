package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.commands.CommandScriptImpl;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterGraph;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;
import net.patrykczarnik.ffmpeg.FFMPEG;
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
	private CurrentOptions currentOptions;
	private CommandScript resultScript = null;
	
	public TranslatorImpl1(FiltersRegistry filtersRegistry) {
		this.filtersRegistry = filtersRegistry;
	}

	@Override
	public void begin() {
		segments = new ArrayList<>();
		currentOptions = new CurrentOptions();
	}

	@Override
	public void end() {
		endSegment();
		int nseg = 0;
		int ninp = 0;
		
		FFMPEG ffmpeg = new FFMPEG();
		ffmpeg.setFilterGraph(FFFilterGraph.empty());
		for(Segment segment : segments) {
			ffmpeg.addInputs(segment.getInputs());
			ffmpeg.getFilterGraph().addChains(segment.getCombinedChains(nseg, ninp));
			nseg += 1;
			ninp += segment.getInputs().size();
		}
		
		List<String> startLabels = IntStream.range(0, nseg)
				.mapToObj(Segment::segmentLabel)
				.collect(Collectors.toList());
		
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", segments.size()),
				FFFilterOption.integer("v", 1),
				FFFilterOption.integer("a", 0));
		
		FFFilterChain concatSegmentsChain = FFFilterChain.withLabels(startLabels, List.of("v"), List.of(concatFilter));
		ffmpeg.getFilterGraph().addChain(concatSegmentsChain);
		
		applyOutputOptions(ffmpeg);
		
		resultScript = CommandScriptImpl.of(ffmpeg);
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
	}

	@Override
	protected CommandScript getResultScript() {
		return resultScript;
	}

	private void beginSegment() {
		currentSegment = new Segment(filtersRegistry);
		currentSegment.remeberOptions(currentOptions);
	}

	private void endSegment() {
		segments.add(currentSegment);
		currentSegment = null;
	}

	private void applyOutputOptions(FFMPEG ffmpeg) {
		Map<String, VPScriptOption> outputOptions = currentOptions.getOutput();
		String outputFile;
		if(outputOptions.containsKey("out")) {
			outputFile = outputOptions.get("out").textValue();
		} else {
			outputFile = "out.mp4";
		}
		FFOutput ffOutput = FFOutput.forFile(outputFile);
		outputOptions.forEach((key, option) -> {			
			ffOutput.withOption(option.toFFOption());
		});
		ffmpeg.addOutputs(ffOutput);
	}

}
