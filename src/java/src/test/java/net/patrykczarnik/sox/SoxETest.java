package net.patrykczarnik.sox;

public class SoxETest {

	public static void main(String[] args) {
		Sox sox = new Sox();
		sox.addGlobalOptions(SoxOption.noValue("guard"));
		sox.addInputs(SoxInputOutput.ofFile("input1.flac"));
		sox.addInputs(SoxInputOutput.ofFileWithOptions("input2.flac", SoxOption.withValue("b", 16)));
		sox.setOutput(SoxInputOutput.ofFile("out.mp3"));
		
		sox.addEffects(SoxEffect.withOptions(
				"chorus", SoxValue.f(0.7), SoxValue.f(0.9),
				SoxValue.i(55), SoxValue.f(0.4), SoxValue.f(0.25), SoxValue.i(2),
				SoxValue.s("−t")));

		sox.addEffects(SoxEffect.withOptions("speed", SoxValue.f(4.004)),
				SoxEffect.withOptions("vol", SoxValue.f(2)));

		System.out.println(sox.getCmdText());
	}

}
