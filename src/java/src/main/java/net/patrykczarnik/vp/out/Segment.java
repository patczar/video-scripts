package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;
import net.patrykczarnik.utils.CollectionUtils;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public class Segment {
	private List<FFInput> inputs = new ArrayList<>();
	private CurrentOptions remeberedOptions;
	private FiltersRegistry filtersRegistry;
	
	public Segment(FiltersRegistry filtersRegistry) {
		this.filtersRegistry = filtersRegistry;
	}

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
		List<Positioned<FFFilter>> allFilters = new ArrayList<>();
		for(AFilterMapper mapper : filtersRegistry.getAll()) {
			mapper.begin();
		}
		Set<AFilterMapper> mappers = new LinkedHashSet<>();
		for(VPScriptOption vpOption : remeberedOptions.getVideo().values()) {
			AFilterMapper filterMapper = filtersRegistry.get("video", vpOption.getName());
			filterMapper.acceptOption(vpOption);
			mappers.add(filterMapper);
		}
		for(AFilterMapper filterMapper : mappers) {
			allFilters.addAll(filterMapper.getCollectedFFFilters());
		}
		allFilters.sort(null);
		chain.addFilters(CollectionUtils.mapList(allFilters, Positioned::getValue));
		
		return List.of(chain);
	}
	
	public static String segmentLabel(int nseg) {
		return "seg" + nseg;
	}

	public void remeberOptions(CurrentOptions currentOptions) {
		remeberedOptions = currentOptions.clone();
	}
	
	public CurrentOptions getRemeberedOptions() {
		return remeberedOptions;
	}

}
