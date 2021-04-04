package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.patrykczarnik.ffmpeg.FFInput;

public class Segment {
	private int segmentNumber, firstInputNumber;
	private List<FFInput> inputs = new ArrayList<>();
	private CurrentOptions remeberedOptions;
	
	public Segment(int segmentNumber, int firstInputNumber) {
		this.segmentNumber = segmentNumber;
		this.firstInputNumber = firstInputNumber;
	}

	public void addInput(FFInput input) {
		inputs.add(input);
	}
	
	public List<FFInput> getInputs() {
		return Collections.unmodifiableList(inputs);
	}
	
	public void remeberOptions(CurrentOptions currentOptions) {
		remeberedOptions = currentOptions.clone();
	}
	
	public CurrentOptions getRemeberedOptions() {
		return remeberedOptions;
	}

	public int getSegmentNumber() {
		return segmentNumber;
	}

	public int getFirstInputNumber() {
		return firstInputNumber;
	}

}
