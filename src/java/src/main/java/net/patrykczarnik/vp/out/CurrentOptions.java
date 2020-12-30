package net.patrykczarnik.vp.out;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.patrykczarnik.vp.in.VPScriptEntrySetOptions;
import net.patrykczarnik.vp.in.VPScriptOption;

public class CurrentOptions implements Cloneable {
	private final Map<String, VPScriptOption> global = new LinkedHashMap<>();
	private final Map<String, VPScriptOption> input = new LinkedHashMap<>();
	private final Map<String, VPScriptOption> output = new LinkedHashMap<>();
	private final Map<String, VPScriptOption> speed = new LinkedHashMap<>();
	private final Map<String, VPScriptOption> audio = new LinkedHashMap<>();
	private final Map<String, VPScriptOption> video = new LinkedHashMap<>();
	
	private final Map<String, Map<String, VPScriptOption>> options;
	
	public CurrentOptions() {
		this.options = new TreeMap<>();
		this.options.put("set-global", global);
		this.options.put("set-input", input);
		this.options.put("set-output", output);
		this.options.put("set-speed", speed);
		this.options.put("set-audio", audio);
		this.options.put("set-video", video);
	}
	
	public Map<String, VPScriptOption> getGlobal() {
		return Collections.unmodifiableMap(global);
	}

	public Map<String, VPScriptOption> getInput() {
		return Collections.unmodifiableMap(input);
	}

	public Map<String, VPScriptOption> getOutput() {
		return Collections.unmodifiableMap(output);
	}

	public Map<String, VPScriptOption> getSpeed() {
		return Collections.unmodifiableMap(speed);
	}

	public Map<String, VPScriptOption> getAudio() {
		return Collections.unmodifiableMap(audio);
	}

	public Map<String, VPScriptOption> getVideo() {
		return Collections.unmodifiableMap(video);
	}

	public Map<String, VPScriptOption> getOptions(String type) {
		return Collections.unmodifiableMap(options.get(type));
	}

	public void update(VPScriptEntrySetOptions cmd) {
		genericUpdate(cmd);
	}
	
	void genericUpdate(VPScriptEntrySetOptions cmd) {
		String commandName = cmd.getCommand();
		Map<String, VPScriptOption> optionsMap = options.get(commandName);
		if(optionsMap == null) {
			optionsMap = new LinkedHashMap<>();
			options.put(commandName, optionsMap);
		}
		updateOptionsOfOneType(optionsMap, cmd.getOptions());
	}

	private void updateOptionsOfOneType(Map<String, VPScriptOption> here, List<VPScriptOption> source) {
		for (VPScriptOption option : source) {
			here.put(option.getName(), option);
		}
	}
	
	@Override
	public CurrentOptions clone() {
		CurrentOptions result = new CurrentOptions();
		this.options.forEach((type, optionsMap) -> {
			Map<String, VPScriptOption> targetMap = result.options.computeIfAbsent(type, t -> new LinkedHashMap<>());
			optionsMap.forEach((option, value) -> {
				targetMap.put(option, value.clone());
			});
		});
		
		return result;
	}

}
