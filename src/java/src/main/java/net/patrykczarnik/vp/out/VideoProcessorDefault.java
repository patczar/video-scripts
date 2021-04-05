package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.CollectionUtils;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public class VideoProcessorDefault implements AVideoProcessor {
	private FiltersRegistry filtersRegistry;
	
	VideoProcessorDefault(FiltersRegistry filtersRegistry) {
		this.filtersRegistry = filtersRegistry;
	}

	@Override
	public List<Positioned<FFFilter>> makeAllFilters(Segment segment) {
		List<Positioned<FFFilter>> allFilters = new ArrayList<>();
		allFilters.addAll(makeSpeedFilters(segment));
		allFilters.addAll(makeVideoFilters(segment));
		allFilters.sort(null);
		return allFilters;
	}

	private List<Positioned<FFFilter>> makeSpeedFilters(Segment segment) {
		List<Positioned<FFFilter>> filters = new ArrayList<>();
		Integer framerate = null;
		String pts = "PTS";
		String tb = "TB";
		if(segment.getRemeberedOptions().getOutput().containsKey("framerate")) {
			framerate = segment.getRemeberedOptions().getOutput().get("framerate").intValue();
			pts = "N";
			tb = String.format("1/%d", framerate);
		}
		
		if(segment.getRemeberedOptions().getSpeed().containsKey("mod")) {
			int mod = segment.getRemeberedOptions().getSpeed().get("mod").intValue();
			FFFilter select = FFFilter.newFilter("select", FFFilterOption.citedText(String.format("not(mod(n,%d))", mod)));
			filters.add(Positioned.of(AFilterMapper.POSITION_VIDEO_SELECT_SPEED, select));
			if(framerate == null) {
				pts = String.format("%s/%d", pts, mod);
			}
		}
		if(!"TB".equals(tb)) {
			FFFilter tbFilter = FFFilter.newFilter("settb", FFFilterOption.text(tb));
			filters.add(Positioned.of(AFilterMapper.POSITION_JOIN_INPUTS_TO_SEGMENT+1, tbFilter));
		}
		if(!"PTS".equals(pts)) {
			FFFilter ptsFilter = FFFilter.newFilter("setpts", FFFilterOption.text(pts));
			filters.add(Positioned.of(AFilterMapper.POSITION_JOIN_INPUTS_TO_SEGMENT+1, ptsFilter));
		}
		return filters;
	}
	
	private List<Positioned<FFFilter>> makeVideoFilters(Segment segment) {
		List<Positioned<FFFilter>> filters = new ArrayList<>();
		if(segment.getRemeberedOptions().getGlobal().containsKey("pixel_format")) {
			String format = segment.getRemeberedOptions().getGlobal().get("pixel_format").textValue();
			FFFilter filter = FFFilter.newFilter("format", FFFilterOption.text(format));
			filters.add(Positioned.of(AFilterMapper.POSITION_VIDEO_FORMAT, filter));
		}
		if(segment.getRemeberedOptions().getOutput().containsKey("pixel_format")) {
			String format = segment.getRemeberedOptions().getOutput().get("pixel_format").textValue();
			FFFilter filter = FFFilter.newFilter("format", FFFilterOption.text(format));
			filters.add(Positioned.of(AFilterMapper.POSITION_VIDEO_FINAL_FORMAT, filter));
		}
		Set<AFilterMapper> mappers = new LinkedHashSet<>();
		for(VPScriptOption vpOption : segment.getRemeberedOptions().getVideo().values()) {
			Set<AFilterMapper> foundMappers = filtersRegistry.get("video", vpOption.getName());
			for(AFilterMapper mapper : foundMappers) {
				if(mappers.add(mapper)) {
					mapper.begin();
				}
				mapper.acceptOption(vpOption);
			}
		}
		for(AFilterMapper filterMapper : mappers) {
			filters.addAll(filterMapper.getCollectedFFFilters());
		}
		return filters;
	}
	
	@Override
	public List<FFFilterChain> getCombinedChains(Segment segment) {
		final int n = segment.getInputs().size();
		List<Positioned<FFFilter>> allFilters = makeAllFilters(segment);

		List<FFFilter> firstStageFilters = CollectionUtils.mapList(
				CollectionUtils.sublist(allFilters,
						(Positioned<FFFilter> elt) -> elt.posBetween(AFilterMapper.POSITION_START, AFilterMapper.POSITION_JOIN_INPUTS_TO_SEGMENT)),
				Positioned::getValue);

		List<FFFilterChain> firstStageChains = new ArrayList<>();
		List<String> labelsA = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			int currentInputNo = segment.getFirstInputNumber() + i;
			String inputLabel = currentInputNo + ":0";
			String labelA = "A"+currentInputNo;
			labelsA.add(labelA);
			firstStageChains.add(FFFilterChain.withLabels(List.of(inputLabel), List.of(labelA), firstStageFilters));
		}
		
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", n),
				FFFilterOption.integer("v", 1),
				FFFilterOption.integer("a", 0));

		String endLabel = TranslationCommons.segmentLabel(segment.getSegmentNumber());
		
		List<FFFilter> segmentFilters = CollectionUtils.mapList(
				CollectionUtils.sublist(allFilters,
						(Positioned<FFFilter> elt) -> elt.posBetween(AFilterMapper.POSITION_JOIN_INPUTS_TO_SEGMENT, AFilterMapper.POSITION_AUDIO_START)),
				Positioned::getValue);
		FFFilterChain segmentChain = FFFilterChain.withLabels(labelsA, List.of(endLabel), List.of(concatFilter));
		segmentChain.addFilters(segmentFilters);
		
		List<FFFilterChain> allChains = new ArrayList<>();
		allChains.addAll(firstStageChains);
		allChains.add(segmentChain);
		return allChains;
	}

}
