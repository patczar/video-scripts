package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;
import net.patrykczarnik.vp.in.VPScriptValue;

public abstract class FilterMapperAbstractOneFilterImpl implements AParamOrientedFilterMapper {
	private final String ffName;
	private final int position;
	private final List<FFFilterOption> defaultParams = new ArrayList<>();
	private Map<String, FFFilterOption> collectedFilterOptions;
	
	protected FilterMapperAbstractOneFilterImpl(String ffName, int position) {
		this.ffName = ffName;
		this.position = position;
	}
	
	public String getFFName() {
		return ffName;
	}

	public int getPosition() {
		return position;
	}
	
	public void addDefaultParams(Collection<FFFilterOption> params) {
		this.defaultParams.addAll(params);
	}

	public void addDefaultParams(FFFilterOption... params) {
		this.defaultParams.addAll(List.of(params));
	}
	
	public List<FFFilterOption> getDefaultParams() {
		return Collections.unmodifiableList(defaultParams);
	}
	
	protected void setFilterOption(FFFilterOption opt) {
		collectedFilterOptions.put(opt.getName(), opt);
	}

	@Override
	public void begin() {
		collectedFilterOptions = new LinkedHashMap<String, FFFilterOption>();
	}

	@Override
	public List<Positioned<FFFilter>> getCollectedFFFilters() {
		FFFilter filter = FFFilter.newFilter(ffName);
		Set<String> were = new HashSet<>();
		for(FFFilterOption opt : collectedFilterOptions.values()) {
			filter.withOption(opt);
			were.add(opt.getName());
		}
		for(FFFilterOption dopt : defaultParams) {
			if(!were.contains(dopt.getName())) {
				filter.withOption(dopt);
			}
		}

		return List.of(Positioned.of(position, filter));
	}

	protected void setManyFilterOptions(VPScriptOption vpOption, List<String> ffParams) {
		int i = 0;
		for(VPScriptValue v : vpOption.getValue().asMany()) {
			String paramName = i < ffParams.size() ? ffParams.get(i) : null; // FIXME null
			FFFilterOption ffOption = v.toFFFilterOption(paramName);
			setFilterOption(ffOption);
			i++;
		}
	}

}
