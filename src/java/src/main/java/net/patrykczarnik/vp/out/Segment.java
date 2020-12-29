package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;

public class Segment {
	private List<FFInput> inputs = new ArrayList<>();
	
	public void addInput(FFInput input) {
		inputs.add(input);
	}
	
	public List<FFInput> getInputs() {
		return Collections.unmodifiableList(inputs);
	}

	public List<FFFilterChain> getCombinedChains(int nseg, int ninp) {
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", inputs.size()),
				FFFilterOption.integer("v", 1),
				FFFilterOption.integer("a", 0));
		
		List<String> startLabels = IntStream.range(ninp, ninp + inputs.size())
				.mapToObj(i -> i + ":0")
				.collect(Collectors.toList());

		String endLabel = segmentLabel(nseg);
		
		FFFilterChain chain = FFFilterChain.withLabels(startLabels, List.of(endLabel), List.of(concatFilter));
		
		return List.of(chain);
	}
	
	public static String segmentLabel(int nseg) {
		return "seg" + nseg;
	}

}
