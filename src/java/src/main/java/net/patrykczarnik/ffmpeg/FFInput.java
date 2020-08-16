package net.patrykczarnik.ffmpeg;

import java.util.Optional;

public class FFInput extends FFInputOutput<FFInput> {
	private FFInput(String file) {
		super(file);
	}
	
	@Override
	protected Optional<String> beforeFileName() {
		return Optional.of("-i");
	}

	public static FFInput forFile(String file) {
		return new FFInput(file);
	}
	
}
