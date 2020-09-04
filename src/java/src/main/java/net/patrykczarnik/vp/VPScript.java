package net.patrykczarnik.vp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VPScript {
	private final List<VPScriptEntry> entries;
	
	private VPScript(List<VPScriptEntry> entries) {
		this.entries = entries;
	}
	
	public static VPScript empty() {
		return new VPScript(new ArrayList<>());
	}

	public static VPScript ofEntries(Collection<? extends VPScriptEntry> entries) {
		return new VPScript(new ArrayList<>(entries));
	}

	public List<VPScriptEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}
	
	public void addEntry(VPScriptEntry entry) {
		entries.add(entry);
	}
	
	@Override
	public String toString() {
		// return "VPScript, " + entries.size() + " entries";
		return "VPScript, " + entries;
	}

}
