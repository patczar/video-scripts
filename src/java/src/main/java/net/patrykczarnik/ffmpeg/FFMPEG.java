package net.patrykczarnik.ffmpeg;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FFMPEG implements Command {
	private List<FFInput> inputs = new ArrayList<>();
	private List<FFOutput> outputs = new ArrayList<>();
	private List<FFOption> globalOptions = new ArrayList<>();
	private FFFilterGraph filterGraph = null;


    public String getCmd() {
        return "ffmpeg";
    }

    public List<String> getOptions() {
        return CmdUtils.joinAnyCmdFragments(globalOptions, inputs, filterGraph, outputs);
    }

    @Override
    public String toString() {
        return "FFMPEG " + String.join(" ", getOptions());
    }
    
    public List<FFInput> getInputs() {
		return inputs;
	}
    
    public void addInputs(Collection<? extends FFInput> inputs) {
    	this.inputs.addAll(inputs);
    }
    
    public void addInputs(FFInput... inputs) {
    	this.inputs.addAll(List.of(inputs));
    }
    
    public List<FFOutput> getOutputs() {
		return outputs;
	}
    
    public void addOutputs(Collection<? extends FFOutput> outputs) {
    	this.outputs.addAll(outputs);
    }
    
    public void addOutputs(FFOutput... outputs) {
    	this.outputs.addAll(List.of(outputs));
    }
    
    public List<FFOption> getGlobalOptions() {
		return globalOptions;
	}
    
    public void addGlobalOptions(Collection<? extends FFOption> options) {
    	this.globalOptions.addAll(options);
    }

    public void addGlobalOptions(FFOption... options) {
    	this.globalOptions.addAll(List.of(options));
    }
    
    public FFFilterGraph getFilterGraph() {
		return filterGraph;
	}
    
    public void setFilterGraph(FFFilterGraph filterGraph) {
		this.filterGraph = filterGraph;
	}
}
