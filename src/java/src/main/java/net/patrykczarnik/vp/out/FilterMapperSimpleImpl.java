package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.patrykczarnik.vp.in.VPScriptOption;

public class FilterMapperSimpleImpl extends FilterMapperAbstractOneFilterImpl {
	private final String vpName;
	private final List<String> params = new ArrayList<>();
	
	public FilterMapperSimpleImpl(String vpName, String ffName, int position) {
		super(ffName, position);
		this.vpName = vpName;
	}
	
	public void addParams(Collection<String> params) {
		this.params.addAll(params);
	}

	public void addParams(String... params) {
		this.params.addAll(List.of(params));
	}

	public List<String> getParams() {
		return Collections.unmodifiableList(params);
	}

	@Override
	public void acceptOption(VPScriptOption vpOption) {
		setManyFilterOptions(vpOption, params);
	}

	@Override
	public Set<String> observedParams() {
		return Set.of(vpName);
	}

}
