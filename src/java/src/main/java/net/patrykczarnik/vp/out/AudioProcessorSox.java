package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterGraph;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;
import net.patrykczarnik.ffmpeg.FFMPEG;
import net.patrykczarnik.ffmpeg.FFMap;
import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.ffmpeg.FFOutput;
import net.patrykczarnik.sox.Sox;
import net.patrykczarnik.sox.SoxEffect;
import net.patrykczarnik.sox.SoxInputOutput;
import net.patrykczarnik.sox.SoxValue;
import net.patrykczarnik.utils.CollectionUtils;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptEntryFile;

public class AudioProcessorSox implements AAudioProcessor {
	private static final String AUDIO_LABEL = "audio";
	private final String SEGMENT_FILE_BEFORE_PATTERN = "aseg%04d.wav";
	private final String SEGMENT_FILE_AFTER_PATTERN = "bseg%04d.wav";

	@Override
	public List<Command> commandsBefore() {
		// TODO Auto-generated method stub
		return List.of();
	}

	@Override
	public List<Command> commandsForEachFile(VPScriptEntryFile fileSpec) {
		// TODO Auto-generated method stub
		return List.of();
	}

	@Override
	public List<Command> commandsForEachSegment(Segment segment) {
		final int segmentNumber = segment.getSegmentNumber();
		final String fileBefore = audioSegmentFileBefore(segmentNumber);
		final String fileAfter = audioSegmentFileAfter(segmentNumber);

		FFMPEG ffmpeg= new FFMPEG();
		ffmpeg.addInputs(segment.getInputs());
		ffmpeg.addGlobalOptions(FFOption.of("y"));
		ffmpeg.addGlobalOptions(FFOption.of("vn"));
		ffmpeg.addOutputs(FFOutput.forFile(fileBefore).withMap(FFMap.ofLabel(AUDIO_LABEL)));
		
		List<String> labelsA = new ArrayList<>();
		final int n = segment.getInputs().size();
		for(int i = 0; i < n; i++) {
			String inputLabel = i + ":1";
			labelsA.add(inputLabel);
		}
		
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", n),
				FFFilterOption.integer("v", 0),
				FFFilterOption.integer("a", 1));

		FFFilterChain segmentChain = FFFilterChain.withLabels(labelsA, List.of(AUDIO_LABEL), List.of(concatFilter));
		// segmentChain.addFilters(segmentFilters);
		FFFilterGraph ffFilters = FFFilterGraph.ofChains(segmentChain);
		ffmpeg.setFilterGraph(ffFilters);
		
		Sox sox = new Sox();
		sox.addInputs(SoxInputOutput.ofFile(fileBefore));
		sox.setOutput(SoxInputOutput.ofFile(fileAfter));
		
		sox.addEffects(SoxEffect.of("norm"));
		double speed = TranslationCommons.realSpeedChange(segment.getRemeberedOptions());
		if(speed != 1.0) {
			sox.addEffects(soxEffectsForSpeedChange(speed));
		}
		
		return List.of(ffmpeg, sox);
	}

	@Override
	public List<Positioned<FFFilter>> ffForEachFile(VPScriptEntryFile fileSpec) {
		// TODO Auto-generated method stub
		return List.of();
	}

	@Override
	public List<Positioned<FFFilter>> ffForEachSegment(Segment segment) {
		// TODO Auto-generated method stub
		return List.of();
	}
	
	@Override
	public Optional<FFFilterChain> audioSegmentChain(Segment segment) {
		FFFilterChain chain = FFFilterChain.noLabels(
				FFFilter.newFilter("amovie", FFFilterOption.text(audioSegmentFileAfter(segment.getSegmentNumber())))
		).addEndLabels(labelForSegmentChain(segment.getSegmentNumber()));
		return Optional.of(chain);
	}

	@Override
	public List<Command> commandsAfter() {
		return List.of();
	}

	@Override
	public List<FFOption> ffGlobalOptions() {
		return List.of();
	}

	private String audioSegmentFileBefore(int nr) {
		return String.format(SEGMENT_FILE_BEFORE_PATTERN, nr);
	}
	
	private String audioSegmentFileAfter(int nr) {
		return String.format(SEGMENT_FILE_AFTER_PATTERN, nr);
	}
	
	private List<SoxEffect> soxEffectsForSpeedChange(double speed) {
		return List.of(SoxEffect.withOptions("speed", SoxValue.f(speed)));
	}

	private String labelForSegmentChain(int segmentNumber) {
		return String.format("aseg%d", segmentNumber);
	}

}
