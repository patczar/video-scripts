package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FFOutput extends FFInputOutput<FFOutput> {
	private List<FFMap> maps = new ArrayList<>();
	
	private FFOutput(String file) {
		super(file);
	}
	
	public static FFOutput forFile(String file) {
		return new FFOutput(file);
	}

	public FFOutput withMap(FFMap map) {
		maps.add(map);
		return self();
	}
	
	public List<FFOption> getSpecialOptions() {
		return Collections.unmodifiableList(maps);
	}
	
	@Override
	protected Optional<String> beforeFileName() {
		return Optional.empty();
	}

}
