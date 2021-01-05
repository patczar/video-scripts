package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;
import net.patrykczarnik.vp.in.VPScriptValue;

public class FilterMapperSimpleImpl implements AFilterMapper {
	private final String ffName;
	private final int position;
	private final List<String> params = new ArrayList<>();
	private final List<FFFilterOption> defaultParams = new ArrayList<>();
	
	public FilterMapperSimpleImpl(String ffName, int position) {
		this.ffName = ffName;
		this.position = position;
	}
	
	public void addParams(Collection<String> params) {
		this.params.addAll(params);
	}

	public void addParams(String... params) {
		this.params.addAll(List.of(params));
	}

	public void addDefaultParams(Collection<FFFilterOption> params) {
		this.defaultParams.addAll(params);
	}

	public void addDefaultParams(FFFilterOption... params) {
		this.defaultParams.addAll(List.of(params));
	}
	
	public String getFfName() {
		return ffName;
	}

	public int getPosition() {
		return position;
	}

	public List<String> getParams() {
		return Collections.unmodifiableList(params);
	}

	public List<FFFilterOption> getDefaultParams() {
		return Collections.unmodifiableList(defaultParams);
	}

	@Override
	public List<Positioned<FFFilter>> getFFFilters(VPScriptOption vpOption) {
		FFFilter filter = FFFilter.newFilter(ffName);
		Set<String> were = new HashSet<>();
		int i = 0;
		for(VPScriptValue v : vpOption.getValue().asMany()) {
			String paramName = i < params.size() ? params.get(i) : null;
			FFFilterOption ffOption = v.toFFFilterOption(paramName);
			filter.withOption(ffOption);
			were.add(paramName);
		}
		for(FFFilterOption dopt : defaultParams) {
			if(!were.contains(dopt.getName())) {
				filter.withOption(dopt);
			}
		}
		
		return List.of(Positioned.of(position, filter));
	}

}
