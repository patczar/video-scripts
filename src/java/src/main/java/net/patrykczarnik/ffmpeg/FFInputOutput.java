package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.CommandFragment;

abstract class FFInputOutput<S extends FFInputOutput<S>> implements CommandFragment {
	private final String file;
	private String format = null;
	private final List<FFOption> options = new ArrayList<>();
	
	protected FFInputOutput(String file) {
		this.file = file;
	}
	
	@SuppressWarnings("unchecked")
	protected S self() {
		return (S)this;
	}
	
	public final String getFile() {
		return file;
	}

	public S withFormat(String format) {
		this.format = format;
		return self();
	}

	public String getFormat() {
		return format;
	}

	public S withOption(FFOption option) {
		options.add(option);
		return self();
	}
	
	public List<FFOption> getOptions() {
		return Collections.unmodifiableList(options);
	}

	protected List<FFOption> getSpecialOptions() {
		return List.of();
	}
	
	protected abstract Optional<String> beforeFileName();
	
	public List<String> getCmdFragments() {
		List<String> formatFragment = format == null ? List.of() : List.of("-f", format);
		return CmdUtils.joinAnyCmdFragments(formatFragment, getOptions(), getSpecialOptions(), beforeFileName(), getFile());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getCmdString();
	}
}
