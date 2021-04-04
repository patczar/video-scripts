package net.patrykczarnik.vp.out;

import java.util.List;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.utils.Positioned;

public interface AVideoProcessor {

	List<Positioned<FFFilter>> makeAllFilters(Segment segment);

	List<FFFilterChain> getCombinedChains(Segment segment);

}