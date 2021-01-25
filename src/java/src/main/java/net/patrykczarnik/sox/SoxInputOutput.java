package net.patrykczarnik.sox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.CommandFragment;

public class SoxInputOutput implements CommandFragment {
	private final String file;
	private final List<SoxOption> options;
	
	private SoxInputOutput(String file, List<SoxOption> options) {
		this.file = file;
		this.options = options;
	}
	
	public static SoxInputOutput ofFile(String file) {
		return new SoxInputOutput(file, new ArrayList<>());
	}

	public static SoxInputOutput ofFileWithOptions(String file, Collection<? extends SoxOption> options) {
		return new SoxInputOutput(file, new ArrayList<>(options));
	}

	public static SoxInputOutput ofFileWithOptions(String file, SoxOption... options) {
		return new SoxInputOutput(file, new ArrayList<>(List.of(options)));
	}

	public SoxInputOutput addOption(SoxOption option) {
		this.options.add(option);
		return this;
	}

	public SoxInputOutput addOptions(Collection<? extends SoxOption> options) {
		this.options.addAll(options);
		return this;
	}

	public SoxInputOutput addOptions(SoxOption... options) {
		this.options.addAll(List.of(options));
		return this;
	}
	
	public String getFile() {
		return file;
	}
	
	public List<SoxOption> getOptions() {
		return Collections.unmodifiableList(options);
	}

	@Override
	public List<String> getCmdFragments() {
		return CmdUtils.joinAnyCmdFragments(options, file);
	}
}
