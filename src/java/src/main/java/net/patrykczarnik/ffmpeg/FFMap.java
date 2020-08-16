package net.patrykczarnik.ffmpeg;

import java.util.List;

public class FFMap extends FFOption {
	private String value;
	
	private FFMap(String value) {
		this.value = value;
	}

	public static FFMap ofString(String value) {
		return new FFMap(value);
	}
	
	public static FFMap ofLabel(String label) {
		return new FFMap("[" + label + "]");
	}
	
	public static FFMap ofStream(int nr) {
		return new FFMap("" + nr);
	}
	
	public static FFMap ofStream(int nr, String type) {
		return new FFMap("" + nr + ":" + type);
	}
	
	@Override
	public String getName() {
		return "map";
	}

	@Override
	public List<String> getRest() {
		// TODO write it better
		return List.of(value);
	}

}
