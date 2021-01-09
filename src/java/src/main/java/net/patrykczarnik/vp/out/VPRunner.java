package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScriptWithOptions;
import net.patrykczarnik.vp.in.VPScript;

public class VPRunner {
	
	public static CommandScriptWithOptions ffmpegFromScript(VPScript vpScript) throws VPTranslatorException {
		FiltersRegistry filtersRegistry = FiltersRegistry.loadFromInternalJson();
		ATranslator translator = new TranslatorImpl1(filtersRegistry);
		CommandScriptWithOptions resultScript = translator.translate(vpScript);
		return resultScript;
	}
}
