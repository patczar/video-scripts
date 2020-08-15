package net.patrykczarnik.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Patryk Czarnik
 *
 * A simple implementation of {@link Command}.
 */
public class CommandImpl implements Command {
    private String command;
    private List<String> options;

    private CommandImpl(String command, List<String> options) {
        this.command = command;
        this.options = options;
    }

    public static CommandImpl of(String command, List<String> options) {
        if(options == null) {
            options = new ArrayList<>();
        } else {
            options = new ArrayList<>(options);
        }
        return new CommandImpl(command, options);
    }

    public static CommandImpl of(String command, String... options) {
        return of(command, List.of(options));
    }

    public String getCmd() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void setOptions(List<String> options) {
        this.options.clear();
        this.options.addAll(options);
    }

    public void addOption(String option) {
        options.add(option);
    }

    @Override
    public String toString() {
        return this.getCmdString();
    }
}
