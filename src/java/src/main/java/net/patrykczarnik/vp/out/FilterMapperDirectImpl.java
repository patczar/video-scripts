package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public class FilterMapperDirectImpl implements AFilterMapper {
	private int position;
	private List<Positioned<FFFilter>> collectedFFFilters;
	
	public FilterMapperDirectImpl() {
		this(POSITION_DEFAULT);
	}
	
	public FilterMapperDirectImpl(int position) {
		this.position = position;
	}

	@Override
	public void begin() {
		collectedFFFilters = new ArrayList<>();
	}
	
	@Override
	public void acceptOption(VPScriptOption vpOption) {
		FFFilter filter = vpOption.toFFFilter();
		collectedFFFilters.add(Positioned.of(position, filter));
	}

	@Override
	public List<Positioned<FFFilter>> getCollectedFFFilters() {
		return collectedFFFilters;
	}

}
