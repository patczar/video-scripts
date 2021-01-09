package net.patrykczarnik.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {
	
	public static int execute(Command command) throws ExecutionException {
		try {
			ProcessBuilder pb = new ProcessBuilder(command.getCmdFragments());
			pb.inheritIO();
			Process process = pb.start();
			int resultCode = process.waitFor();
			return resultCode;
		} catch (IOException e) {
			throw new ExecutionException(String.format("Error during execution of command [%s]", command), e);
		} catch (InterruptedException e) {
			throw new ExecutionException(String.format("Execution of command [%s] has been interrupted", command), e);
		}
	}

	public static List<Integer> execute(CommandScript script, boolean showCmd, boolean breakNonZero) throws ExecutionException {
		List<Integer> codes = new ArrayList<>();
		for(Command command : script.getCommands()) {
			if(showCmd) {
				System.out.println(command);
			}
			int code = execute(command);
			codes.add(code);
			if(showCmd) {
				System.out.println();
			}
			if(breakNonZero && code != 0) {
				break;
			}
		}
		return codes;		
	}

	public static List<Integer> execute(CommandScript script) throws ExecutionException {
		return execute(script, true, false);
	}
}
