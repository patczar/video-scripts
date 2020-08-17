package net.patrykczarnik.commands;

import java.util.List;

public interface CommandFragment {
    public List<String> getCmdFragments();

    public default String getCmdText() {
        return String.join(" ", getCmdFragments());
    }
}
