package net.patrykczarnik.vp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CurrentOptions {
	private Map<String, Map<String, VPScriptOption>> options = new TreeMap<>();
	
	void update(VPScriptEntrySetOptions cmd) {
		String commandName = cmd.getCommand();
		Map<String, VPScriptOption> optionsMap = options.get(commandName);
		if(optionsMap == null) {
			optionsMap = new LinkedHashMap<>();
			options.put(commandName, optionsMap);
		}
		updateOptions(optionsMap, cmd.getOptions());
	}

	private void updateOptions(Map<String, VPScriptOption> here, List<VPScriptOption> source) {
		for (VPScriptOption option : source) {
			here.put(option.getName(), option);
		}
	}

}
