package net.patrykczarnik.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patryk Czarnik
 *
 * A single command that can be executed in system command shell.
 */
public interface Command extends CommandFragment {
    public String getCmd();

    public List<String> getOptions();

    @Override
    public default List<String> getCmdFragments() {
        List<String> options = getOptions();
        List<String> result = new ArrayList<>(options.size()+1);
        result.add(getCmd());
        result.addAll(getOptions());
        return result;
    }
}
