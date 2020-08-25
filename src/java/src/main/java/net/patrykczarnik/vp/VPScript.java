package net.patrykczarnik.vp;

import java.io.File;

public class VPScript {

	public static VPScript fromFile(File scriptFile) {
		return VPScriptParser.parse(scriptFile);
	}

}
