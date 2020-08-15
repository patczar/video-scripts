package net.patrykczarnik.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandFragmentImpl implements CommandFragment {
    private List<String> options;

    private CommandFragmentImpl(List<String> options) {
        this.options = options;
    }

    public static CommandFragmentImpl of(List<String> options) {
        if(options == null) {
            options = new ArrayList<>();
        } else {
            options = new ArrayList<>(options);
        }
        return new CommandFragmentImpl(options);
    }

    public static CommandFragmentImpl of(String command, String... options) {
        return of(List.of(options));
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
    public List<String> getCmdFragments() {
        return getOptions();
    }

    @Override
    public String toString() {
        return this.getCmdString();
    }

}
