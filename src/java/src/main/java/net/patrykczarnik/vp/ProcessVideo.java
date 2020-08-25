package net.patrykczarnik.vp;

import java.io.File;

import net.patrykczarnik.ffmpeg.FFMPEG;

public class ProcessVideo {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("You should give one argument: the script");
			return;
		}
		File scriptFile = new File(args[0]);
		System.out.println("Starting to parse script from " + scriptFile);
		VPScript script = VPScript.fromFile(scriptFile);
		System.out.println("Script parsed: " + script);
		FFMPEG ffmpeg = VPRunner.ffmpegFromScript(script);
		System.out.println("Converted to FFMPEG:");
		System.out.println(ffmpeg);
	}

}
