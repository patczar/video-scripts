package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;
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
import net.patrykczarnik.vp.in.VPScriptEntryFile;
import net.patrykczarnik.vp.in.VPScriptEntrySetOptions;

/**
 * @author Patryk Czarnik
 * 
 * Implementation based on stream concatenation.
 * 
 */
public class TranslatorImpl1 extends TranslatorAbstractImpl {
	private List<Segment> segments;
	private Segment currentSegment;
	// private List<FFFilterChain> chunkChains;
	private CommandScript resultScript = null;

	@Override
	public void begin() {
		segments = new ArrayList<>();
		currentSegment = new Segment();
	}

	@Override
	public void end() {
		finishSegment();
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
		
		resultScript = CommandScriptImpl.of(ffmpeg);
	}

	@Override
	public void acceptSetOptions(VPScriptEntrySetOptions entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptFile(VPScriptEntryFile entry) {
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

	private void finishSegment() {
		segments.add(currentSegment);
		currentSegment = new Segment();
	}
}
