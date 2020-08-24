package net.patrykczarnik.commands;

import java.io.IOException;

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

}
