package net.patrykczarnik.vp.out;

import javax.json.JsonObject;

public abstract class FilterMapperAbstractImpl implements AFilterMapper {
	static final String DEFAULT_IMPL = "simple";
	
	public static AFilterMapper forJsonSpec(JsonObject spec) {
		String impl = spec.getString("impl", DEFAULT_IMPL);
		switch(impl) {
			default: throw new IllegalArgumentException("Unknown filter mapper impl: " + impl);
		}
	}
}
