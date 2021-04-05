package net.patrykczarnik.vp.out;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.patrykczarnik.sox.SoxEffect;
import net.patrykczarnik.sox.SoxValue;
import net.patrykczarnik.utils.CollectionUtils;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;
import net.patrykczarnik.vp.in.VPScriptValue;

public class FilterMapperSimpleSoxImpl implements AParamOrientedFilterMapper {
	private String vpParam;
	private String soxEffect;
	private int position;
	private SoxEffect resultEffect;

	public FilterMapperSimpleSoxImpl(String vpParam, String soxEffect, int position) {
		this.vpParam = vpParam;
		this.soxEffect = soxEffect;
		this.position = position;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getVpParam() {
		return vpParam;
	}
	
	public String getSoxEffect() {
		return soxEffect;
	}
	
	@Override
	public void begin() {
	}

	@Override
	public void acceptOption(VPScriptOption vpOption) {
		if(Objects.equals(vpOption.getName(), vpParam)) {
			List<SoxValue> params = soxValuesFromVpValues(vpOption.getValue());
			resultEffect = SoxEffect.withOptions(soxEffect, params);
		}
	}
	
	@Override
	public List<Positioned<SoxEffect>> getCollectedSoxEffects() {
		return List.of(Positioned.of(position, resultEffect));
	}

	@Override
	public Set<String> observedParams() {
		return Set.of(vpParam);
	}

	private static List<SoxValue> soxValuesFromVpValues(VPScriptValue value) {
		return CollectionUtils.mapList(value.asMany(), FilterMapperSimpleSoxImpl::soxValueFromVpValue);
	}
	
	private static SoxValue soxValueFromVpValue(VPScriptValue value) {
		switch(value.getType()) {
			case INT:
				return SoxValue.i(value.intValue());
			case NUM:
				return SoxValue.f(value.numValue());
			case TEXT:
			default:
				return SoxValue.s(value.textValue());
		}
		
	}
	
}
