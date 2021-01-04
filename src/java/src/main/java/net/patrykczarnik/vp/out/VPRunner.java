package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.vp.in.VPScript;

public class VPRunner {
	
	public static CommandScript ffmpegFromScript(VPScript vpScript) throws VPTranslatorException {
		FiltersRegistry filtersRegistry = FiltersRegistry.loadFromInternalJson();
		ATranslator translator = new TranslatorImpl1(filtersRegistry);
		CommandScript resultScript = translator.translate(vpScript);
		return resultScript;
	}
}
