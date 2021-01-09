package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScriptWithOptions;
import net.patrykczarnik.vp.in.VPScript;

public interface ATranslator {
	CommandScriptWithOptions translate(VPScript script) throws VPTranslatorException;

}
