package net.patrykczarnik.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandScriptImpl implements CommandScriptWithOptions {
	private List<Command> commands;
	private File workingDir;
	
	private CommandScriptImpl() {
		this.commands = new ArrayList<>();
	}

	private CommandScriptImpl(Collection<Command> commands) {
		this.commands = new ArrayList<>(commands);
	}

	public static CommandScriptImpl empty() {
		return new CommandScriptImpl();
	}

	public static CommandScriptImpl of(Collection<Command> commands) {
		return new CommandScriptImpl(commands);
	}

	public static CommandScriptImpl of(Command... commands) {
		return new CommandScriptImpl(List.of(commands));
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public CommandScriptImpl add(Command... commands) {
		for (Command command : commands) {
			this.commands.add(command);
		}
		return this;
	}

	public CommandScriptImpl add(Iterable<Command> commands) {
		for (Command command : commands) {
			this.commands.add(command);
		}
		return this;
	}

	public File getWorkingDir() {
		return workingDir;
	}
	
	public CommandScriptImpl setWorkingDir(File dir) {
		this.workingDir = dir;
		return this;
	}
	
	public CommandScriptImpl setWorkingDir(String dir) {
		return setWorkingDir(new File(dir));
	}

	@Override
	public String toString() {
		return "Script:\n" + 
				this.getCommands().stream()
				.map(Command::toString)
				.collect(Collectors.joining("\n"));
	}
}
