package net.patrykczarnik.vp.out;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.CollectionUtils;

public class FiltersRegistry {
	private static final String DEFAULT_JSON = "/net/patrykczarnik/vp/filters.json";
	static final String DEFAULT_IMPL = "simple";

	private Map<String, AFilterMapper> videoMappers = new LinkedHashMap<>();
	private Map<String, AFilterMapper> audioMappers = new LinkedHashMap<>();
	
	public static FiltersRegistry loadFromInternalJson() {
		FiltersRegistry reg = new FiltersRegistry();
		reg.loadJson();
		return reg;
	}

	public Set<AFilterMapper> getAll() {
		return new HashSet<>(videoMappers.values());
	}
	
	public AFilterMapper get(String category, String name) {
		switch(category) {
			case "video": return videoMappers.computeIfAbsent(name, FiltersRegistry::notFound);
			case "audio": return audioMappers.computeIfAbsent(name, FiltersRegistry::notFound);
			default: throw new IllegalArgumentException(category);
		}
	}

	private void loadJson() {
		try(JsonParser jsonParser = Json.createParser(FiltersRegistry.class.getResourceAsStream(DEFAULT_JSON))) {
			while(jsonParser.next() != Event.START_OBJECT);
			
			JsonObject jsonObject = jsonParser.getObject();
			if(jsonObject.containsKey("video")) {
				defineMappers(jsonObject.getJsonArray("video"), this.videoMappers);
			}
			if(jsonObject.containsKey("audio")) {
				defineMappers(jsonObject.getJsonArray("audio"), this.audioMappers);
			}
		}
	}
	
	private void defineMappers(JsonArray jsonArray, Map<String, AFilterMapper> map) {
		jsonArray.forEach((jsonValue) -> {
			JsonObject spec = jsonValue.asJsonObject();
			AParamOrientedFilterMapper mapper = forJsonSpec(spec);
			for(String vpName : mapper.observedParams()) {
				map.put(vpName, mapper);				
			}
		});
	}
	
	private AParamOrientedFilterMapper forJsonSpec(JsonObject spec) {
		String impl = spec.getString("impl", DEFAULT_IMPL);
		switch(impl) {
			case "simple": return newSimple(spec);
			case "merging": return getMerging(spec);
			default: throw new IllegalArgumentException("Unknown filter mapper impl: " + impl);
		}
	}

	private AParamOrientedFilterMapper newSimple(JsonObject spec) {
		int position = spec.getInt("position", AFilterMapper.POSITION_DEFAULT);
		String ffName = spec.getString("ff-filter");
		String vpName = spec.getString("vp-param");
		List<JsonString> paramsSpec = spec.getJsonArray("ff-params").getValuesAs(JsonString.class);
		JsonObject defaultParamsSpec = spec.getJsonObject("ff-default-params");
		
		FilterMapperSimpleImpl mapper = new FilterMapperSimpleImpl(vpName, ffName, position);
		mapper.addParams(CollectionUtils.mapList(paramsSpec, JsonString::getString));
		if(defaultParamsSpec != null)
			defaultParamsSpec.forEach((k, v) -> {
				mapper.addDefaultParams(jsonValueToFFFilterOption(k, v));
			});
		return mapper;
	}

	private AParamOrientedFilterMapper getMerging(JsonObject spec) {
		int position = spec.getInt("position", AFilterMapper.POSITION_DEFAULT);
		String ffName = spec.getString("ff-filter");
		JsonArray paramSpecs = spec.getJsonArray("param-mappings");
		JsonObject defaultParamsSpec = spec.getJsonObject("ff-default-params");
		
		FilterMapperMergingImpl mapper = new FilterMapperMergingImpl(ffName, position);

		paramSpecs.forEach(pspec -> {
			String vpName = pspec.asJsonObject().getString("vp-param");
			List<JsonString> paramsSpec = pspec.asJsonObject().getJsonArray("ff-params").getValuesAs(JsonString.class);
			mapper.addParamMapping(vpName, CollectionUtils.mapList(paramsSpec, JsonString::getString));
		});
		if(defaultParamsSpec != null)
			defaultParamsSpec.forEach((k, v) -> {
				mapper.addDefaultParams(jsonValueToFFFilterOption(k, v));
			});
		return mapper;
	}
	
	private static FFFilterOption jsonValueToFFFilterOption(String name, JsonValue value) {
		switch(value.getValueType()) {
			case NULL:
				return FFFilterOption.text(name, "");
			case STRING:
				return FFFilterOption.text(name, ((JsonString)value).getString());
			case NUMBER: {
				JsonNumber jn = (JsonNumber)value;
				if(jn.isIntegral()) {
					return FFFilterOption.integer(name, jn.longValue()); 
				} else {
					return FFFilterOption.number(name, jn.doubleValue());
				}
			}
			case TRUE:
			case FALSE:
			case ARRAY:
			case OBJECT:
			default:
				throw new IllegalArgumentException("Bad JSON value as default value (" + name + ")");
		}
	}

	private static AFilterMapper notFound(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("Filter mapper not found: " + name);
	}

}
