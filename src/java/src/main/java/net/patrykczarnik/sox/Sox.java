package net.patrykczarnik.sox;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Sox implements Command {
	private final List<SoxInputOutput> inputs = new ArrayList<>();
	private SoxInputOutput output = null;
	private final List<SoxOption> globalOptions = new ArrayList<>();
	private final List<SoxEffect> effects =  new ArrayList<>();


    public String getCmd() {
        return "sox";
    }

    public List<String> getOptions() {
        return CmdUtils.joinAnyCmdFragments(globalOptions, inputs, output, effects);
    }

    @Override
    public String toString() {
        return "FFMPEG " + String.join(" ", getOptions());
    }
    
    public List<SoxInputOutput> getInputs() {
		return inputs;
	}
    
    public void addInputs(Collection<? extends SoxInputOutput> inputs) {
    	this.inputs.addAll(inputs);
    }
    
    public void addInputs(SoxInputOutput... inputs) {
    	this.inputs.addAll(List.of(inputs));
    }
    
    public SoxInputOutput getOutput() {
		return output;
	}
    
    public void setOutput(SoxInputOutput output) {
    	this.output = output;
    }
    
    public List<SoxOption> getGlobalOptions() {
		return globalOptions;
	}
    
    public void addGlobalOptions(Collection<? extends SoxOption> options) {
    	this.globalOptions.addAll(options);
    }

    public void addGlobalOptions(SoxOption... options) {
    	this.globalOptions.addAll(List.of(options));
    }
    
    public List<SoxEffect> getEffects() {
		return effects;
	}
    
    public void addEffects(Collection<? extends SoxEffect> options) {
    	this.effects.addAll(options);
    }

    public void addEffects(SoxEffect... options) {
    	this.effects.addAll(List.of(options));
    }
    
   
}
