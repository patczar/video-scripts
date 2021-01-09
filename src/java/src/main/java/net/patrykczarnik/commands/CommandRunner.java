package net.patrykczarnik.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {
	private File workingDir;
	private boolean showCmd;
	// TODO stdio / stdout
	
	public CommandRunner(File workingDir, boolean showCmd) {
		this.workingDir = workingDir;
		this.showCmd = showCmd;
	}

	public CommandRunner(String workingDir, boolean showCmd) {
		this(new File(workingDir), showCmd);
	}
	
	public CommandRunner() {
		this((File)null, true);
	}
	
	public File getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.setWorkingDir(new File(workingDir));
	}

	public boolean getShowCmd() {
		return showCmd;
	}

	public void setShowCmd(boolean showCmd) {
		this.showCmd = showCmd;
	}

	public int execute(Command command) throws ExecutionException {
		try {
			ProcessBuilder pb = new ProcessBuilder(command.getCmdFragments());
			if(workingDir != null) {
				pb.directory(workingDir);
			}
			pb.inheritIO();
			if(showCmd) {
				System.out.println(command.getCmdText());
			}
			Process process = pb.start();
			int resultCode = process.waitFor();
			return resultCode;
		} catch (IOException e) {
			throw new ExecutionException(String.format("Error during execution of command [%s]", command), e);
		} catch (InterruptedException e) {
			throw new ExecutionException(String.format("Execution of command [%s] has been interrupted", command), e);
		}
	}

	public List<Integer> execute(CommandScript script, boolean breakNonZero) throws ExecutionException {
		List<Integer> codes = new ArrayList<>();
		for(Command command : script.getCommands()) {
			int code = execute(command);
			codes.add(code);
			if(breakNonZero && code != 0) {
				break;
			}
		}
		return codes;		
	}

	public List<Integer> execute(CommandScript script) throws ExecutionException {
		return execute(script, false);
	}
}
