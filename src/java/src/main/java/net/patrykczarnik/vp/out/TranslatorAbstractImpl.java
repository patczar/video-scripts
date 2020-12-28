package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.vp.in.VPScript;

public abstract class TranslatorAbstractImpl implements ATranslator, VPScriptHandlerDispatching {
	protected VPScript vpScript;
	
	@Override
	public CommandScript translate(VPScript script) throws VPTranslatorException {
		vpScript = script;
		this.applyToScript(vpScript);
		return this.getResultScript();
	}

	protected abstract CommandScript getResultScript();
	
}
