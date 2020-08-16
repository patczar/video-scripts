package net.patrykczarnik.ffmpeg;

import java.util.Optional;

public class FFInput extends FFInputOutput<FFInput> {
	private FFInput(String file) {
		super(file);
	}
	
	@Override
	protected Optional<String> beforeFileName() {
		return Optional.of("-f");
	}
	
}
