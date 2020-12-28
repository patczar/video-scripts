package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.vp.in.VPScript;

public interface ATranslator {
	CommandScript translate(VPScript script) throws VPTranslatorException;

}
