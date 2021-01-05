package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public class FilterMapperMergingImpl implements AFilterMapper {
	private final String ffName;
	private final int position;
	private final Map<String, String> paramMapping = new HashMap<>();
	private final List<FFFilterOption> defaultParams = new ArrayList<>();
	private final Map<String, FFFilterOption> actualParams = new HashMap<>();
	
	public FilterMapperMergingImpl(String ffName, int position) {
		this.ffName = ffName;
		this.position = position;
	}
	
	public void addParamMapping(String vpName, String ffName) {
		this.paramMapping.put(vpName, ffName);
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

	public List<FFFilterOption> getDefaultParams() {
		return Collections.unmodifiableList(defaultParams);
	}

	@Override
	public List<Positioned<FFFilter>> getFFFilters(VPScriptOption vpOption) {
		String vpName = vpOption.getName();
		String ffName = paramMapping.get(vpName);
		if(ffName != null) {
			actualParams.put(ffName, vpOption.getValue().toFFFilterOption(ffName));
		}
		
		return List.of();
	}
	
	@Override
	public List<Positioned<FFFilter>> getPostponed() {
		FFFilter filter = FFFilter.newFilter(ffName);
		Set<String> were = new HashSet<>();
		for(FFFilterOption ffOption : actualParams.values()) {
			filter.withOption(ffOption);
			were.add(ffOption.getName());
		}
		for(FFFilterOption dopt : defaultParams) {
			if(!were.contains(dopt.getName())) {
				filter.withOption(dopt);
			}
		}
		
		actualParams.clear();
		return List.of(Positioned.of(position, filter));
	}

}
