package net.patrykczarnik.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandScriptImpl implements CommandScript {
	private List<Command> commands;
	
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

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public void add(Command... commands) {
		for (Command command : commands) {
			this.commands.add(command);
		}
	}

	public void add(Iterable<Command> commands) {
		for (Command command : commands) {
			this.commands.add(command);
		}
	}

	@Override
	public String toString() {
		return "Script:\n" + 
			this.getCommands().stream()
				.map(Command::toString)
				.collect(Collectors.joining("\n"));
	}
}
