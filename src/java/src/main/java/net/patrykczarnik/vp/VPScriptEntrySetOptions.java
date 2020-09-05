package net.patrykczarnik.vp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class VPScriptEntrySetOptions extends VPScriptEntry {
	private String command;
	private List<VPScriptOption> options;
	
	private VPScriptEntrySetOptions(String command, List<VPScriptOption> options) {
		this.command = command;
		this.options = options;
	}
	
	public static VPScriptEntrySetOptions of(String command, Collection<? extends VPScriptOption> options) {
		return new VPScriptEntrySetOptions(command, new ArrayList<>(options));
	}

	public static VPScriptEntrySetOptions of(String command) {
		return new VPScriptEntrySetOptions(command, new ArrayList<>());
	}

	public String getCommand() {
		return command;
	}

	public List<VPScriptOption> getOptions() {
		return Collections.unmodifiableList(options);
	}
	
	public VPScriptEntrySetOptions with(VPScriptOption option) {
		this.options.add(option);
		return this;
	}

	public VPScriptEntrySetOptions with(VPScriptOption... options) {
		for (VPScriptOption option : options) {
			this.options.add(option);
		}
		return this;
	}

	public VPScriptEntrySetOptions with(Iterable<? extends VPScriptOption> options) {
		for (VPScriptOption option : options) {
			this.options.add(option);
		}
		return this;
	}

	@Override
	public String getCmdText() {
		StringBuilder result = new StringBuilder();
		result.append(command);
		for(VPScriptOption option : options) {
			result.append(' ');
			result.append(option.getCmdText());
		}
		return result.toString();
	}
}
