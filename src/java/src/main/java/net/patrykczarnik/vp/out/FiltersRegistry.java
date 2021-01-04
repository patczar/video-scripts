package net.patrykczarnik.vp.out;

import java.util.LinkedHashMap;
import java.util.Map;

public class FiltersRegistry {
	private Map<String, AFilterMapper> videoMappers = new LinkedHashMap<>();
	private Map<String, AFilterMapper> audioMappers = new LinkedHashMap<>();
	
	public static FiltersRegistry loadFromInternalJson() {
		FiltersRegistry reg = new FiltersRegistry();
		
		return reg;
	}
	
	public AFilterMapper get(String category, String name) {
		switch(category) {
			case "video": return videoMappers.computeIfAbsent(name, FiltersRegistry::notFound);
			case "audio": return audioMappers.computeIfAbsent(name, FiltersRegistry::notFound);
			default: throw new IllegalArgumentException(category);
		}
	}

	private static AFilterMapper notFound(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("Filter mapper not found: " + name);
	}

}
