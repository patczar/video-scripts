package net.patrykczarnik.vp;

import java.io.File;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.commands.CommandRunner;
import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.commands.CommandScriptWithOptions;
import net.patrykczarnik.commands.ExecutionException;
import net.patrykczarnik.vp.in.VPParserException;
import net.patrykczarnik.vp.in.VPScript;
import net.patrykczarnik.vp.in.VPScriptParser;
import net.patrykczarnik.vp.out.VPRunner;
import net.patrykczarnik.vp.out.VPTranslatorException;

public class ProcessVideo {

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Usage:");
			System.out.println("  java net.patrykczarnik.vp.ProcessVideo script [run|show]");
			System.out.println("Where script is the VP script file");
			System.out.println("  run - actually run the script");
			System.out.println("  show - only show the resulting commands");
			return;
		}
		File scriptFile = new File(args[0]);
		boolean run;
		switch(args[1]) {
			case "run": run = true; break;
			case "show": run = false; break;
			default: run = false;
		}
		try {
			System.out.println("Starting to parse script from " + scriptFile);
			VPScript script = VPScriptParser.parse(scriptFile);
			System.out.println("Script parsed: " + script);
			System.out.println();
			System.out.println(script.getCmdText());
			System.out.println();

			CommandScriptWithOptions scriptToRun = VPRunner.ffmpegFromScript(script);
			System.out.println("Converted to script:");
			System.out.println(scriptToRun);
			System.out.println();
			
			if(run) {
				try {
					CommandRunner cmdRunner = new CommandRunner(scriptToRun.getWorkingDir(), true);
					cmdRunner.execute(scriptToRun);
				} catch(ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				for(Command command : scriptToRun.getCommands()) {
					System.out.println(command.getCmdText());
				}
			}
		} catch (VPParserException e) {
			e.printStackTrace();
		} catch(VPTranslatorException e) {
			e.printStackTrace();
		}
	}

}
