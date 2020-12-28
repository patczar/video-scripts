package net.patrykczarnik.vp;

import java.io.File;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.vp.in.VPParserException;
import net.patrykczarnik.vp.in.VPScript;
import net.patrykczarnik.vp.in.VPScriptParser;
import net.patrykczarnik.vp.out.VPRunner;
import net.patrykczarnik.vp.out.VPTranslatorException;

public class ProcessVideo {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("You should give one argument: the script");
			return;
		}
		File scriptFile = new File(args[0]);
		try {
			System.out.println("Starting to parse script from " + scriptFile);
			VPScript script = VPScriptParser.parse(scriptFile);
			System.out.println("Script parsed: " + script);
			System.out.println();
			System.out.println(script.getCmdText());
			System.out.println();

			CommandScript scriptToRun = VPRunner.ffmpegFromScript(script);
			System.out.println("Converted to script:");
			System.out.println(scriptToRun);
			System.out.println();
			for(Command command : scriptToRun.getCommands()) {
				System.out.println(command.getCmdText());
			}
			
		} catch (VPParserException e) {
			e.printStackTrace();
		} catch(VPTranslatorException e) {
			e.printStackTrace();
		}
	}

}
