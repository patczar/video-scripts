package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.commands.CommandScriptImpl;
import net.patrykczarnik.vp.in.VPScript;
import net.patrykczarnik.vp.in.VPScriptEntry;

public class VPRunner {
	private VPScript vpScript;
	private CommandScript resultScript;
	private CurrentOptions currentOptions;
	
	
	private VPRunner(VPScript vpScript) {
		this.vpScript = vpScript;
		this.resultScript = CommandScriptImpl.empty();
		this.currentOptions = new CurrentOptions();
	}

	public static CommandScript ffmpegFromScript(VPScript vpScript) {
		VPRunner builder = new VPRunner(vpScript);
		builder.process();	
		return builder.getCommandScript();
	}
	
	private CommandScript getCommandScript() {
		return resultScript;
	}
	
	private void process() {
		for (VPScriptEntry scriptEntry : vpScript.getEntries()) {
			if(scriptEntry.isSetOptions()) {
				currentOptions.update(scriptEntry.asSetOptions());
			}
		}
	}

}
