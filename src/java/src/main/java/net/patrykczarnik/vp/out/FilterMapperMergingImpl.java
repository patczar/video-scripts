package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.patrykczarnik.vp.in.VPScriptOption;

public class FilterMapperMergingImpl extends FilterMapperAbstractOneFilterImpl {
	private final Map<String, List<String>> paramMapping = new LinkedHashMap<>();
	
	public FilterMapperMergingImpl(String ffName, int position) {
		super(ffName, position);
	}
	
	public void addParamMapping(String vpName, List<String> ffParamNames) {
		this.paramMapping.put(vpName, new ArrayList<>(ffParamNames));
	}

	@Override
	public void acceptOption(VPScriptOption vpOption) {
		List<String> ffParams = paramMapping.get(vpOption.getName());
		setManyFilterOptions(vpOption, ffParams);

	}

	@Override
	public Set<String> observedParams() {
		return Collections.unmodifiableSet(paramMapping.keySet());
	}

}
