package net.patrykczarnik.vp.out;

import java.util.List;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public class FilterMapperDirectImpl implements AFilterMapper {
	private int position;
	
	public FilterMapperDirectImpl() {
		this(POSITION_DEFAULT);
	}
	
	public FilterMapperDirectImpl(int position) {
		this.position = position;
	}

	@Override
	public List<Positioned<FFFilter>> getFFFilters(VPScriptOption vpOption) {
		FFFilter filter = vpOption.toFFFilter();
		return List.of(Positioned.of(position, filter));
	}

}
