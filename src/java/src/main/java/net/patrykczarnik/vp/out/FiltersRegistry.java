package net.patrykczarnik.vp.out;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
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
	private Map<String, FilterMapperMergingImpl> mergingMappers = new LinkedHashMap<>();
	
	public static FiltersRegistry loadFromInternalJson() {
		FiltersRegistry reg = new FiltersRegistry();
		reg.loadJson();
		return reg;
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
				defineMappers(jsonObject.getJsonObject("video"), this.videoMappers);
			}
			if(jsonObject.containsKey("audio")) {
				defineMappers(jsonObject.getJsonObject("audio"), this.audioMappers);
			}
		}
	}
	
	private void defineMappers(JsonObject jsonObject, Map<String, AFilterMapper> map) {
		jsonObject.forEach((vpName, jsonValue) -> {
			JsonObject spec = jsonValue.asJsonObject();
			AFilterMapper mapper = forJsonSpec(vpName, spec);
			map.put(vpName, mapper);
		});
	}
	
	private AFilterMapper forJsonSpec(String vpName, JsonObject spec) {
		String impl = spec.getString("impl", DEFAULT_IMPL);
		switch(impl) {
			case "simple": return newSimple(vpName, spec);
			case "merging": return getMerging(vpName, spec);
			default: throw new IllegalArgumentException("Unknown filter mapper impl: " + impl);
		}
	}

	private FilterMapperSimpleImpl newSimple(String vpName, JsonObject spec) {
		String ffName = spec.getString("ff-name", vpName);
		int position = spec.getInt("position", AFilterMapper.POSITION_DEFAULT);
		List<JsonString> paramsSpec = spec.getJsonArray("params").getValuesAs(JsonString.class);
		JsonObject defaultParamsSpec = spec.getJsonObject("default-params");
		
		FilterMapperSimpleImpl mapper = new FilterMapperSimpleImpl(ffName, position);
		mapper.addParams(CollectionUtils.mapList(paramsSpec, JsonString::getString));
		if(defaultParamsSpec != null)
			defaultParamsSpec.forEach((k, v) -> {
				mapper.addDefaultParams(jsonValueToFFFilterOption(k, v));
			});
		return mapper;
	}

	private FilterMapperMergingImpl getMerging(String vpName, JsonObject spec) {
		String ffName = spec.getString("ff-name", vpName);
		String ffParam= spec.getString("ff-param", vpName);
		int position = spec.getInt("position", AFilterMapper.POSITION_DEFAULT);
		
		FilterMapperMergingImpl mapper = mergingMappers.get(ffName);
		if(mapper == null) {
			mapper = new FilterMapperMergingImpl(ffName, position);
		}
		mapper.addParamMapping(vpName, ffParam);
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
