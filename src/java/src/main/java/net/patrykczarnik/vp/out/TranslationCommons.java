package net.patrykczarnik.vp.out;

import net.patrykczarnik.vp.in.VPScriptOption;

public final class TranslationCommons {
	
	private TranslationCommons() { }
	
	public static double fpsFromSpec(VPScriptOption spec) {
		if(spec.getValue().isNum()) {
			return spec.numValue();
		}
		switch(spec.textValue().toLowerCase()) {
			case "pal": return 25.0;
			case "ntsc":
			case "ntsc30": return 30_000.0 / 1001.0;
			case "ntsc25": return 25_000.0 / 1001.0;
			case "ntsc60": return 60_000.0 / 1001.0;
			default: throw new IllegalArgumentException("rate spec not known: " + spec.textValue());
		}
	}
	
	public static double realSpeedChange(CurrentOptions spec) {
		double inputFps = 30.0;
		if(spec.getInput().containsKey("fps")) {
			inputFps = fpsFromSpec(spec.getInput().get("fps"));
		}
		
		double outputFps = inputFps;
		if(spec.getOutput().containsKey("fps")) {
			outputFps = fpsFromSpec(spec.getOutput().get("fps"));
		}
		
		double speed = 1.0;
		if(spec.getSpeed().containsKey("mod")) {
			speed = spec.getSpeed().get("mod").numValue();
		}
		
		return speed * outputFps / inputFps;
	}

	public static String segmentLabel(int nseg) {
		return "B" + nseg;
	}

}
