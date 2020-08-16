package net.patrykczarnik.ffmpeg;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class FFMPEG implements Command {
	private List<FFInput> inputs = new ArrayList<>();
	private List<FFOutput> outputs = new ArrayList<>();
	private List<FFOption> globalOptions = new ArrayList<>();


    public String getCmd() {
        return "ffmpeg";
    }

    public List<String> getOptions() {
        return CmdUtils.joinAnyCmdFragments(globalOptions, inputs, outputs);
    }

    @Override
    public String toString() {
        return "FFMPEG " + String.join(" ", getOptions());
    }
}
