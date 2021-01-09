package net.patrykczarnik.vp.out;

import net.patrykczarnik.commands.CommandScriptWithOptions;
import net.patrykczarnik.vp.in.VPScript;

public abstract class TranslatorAbstractImpl implements ATranslator, VPScriptHandlerDispatching {
	protected VPScript vpScript;
	
	@Override
	public CommandScriptWithOptions translate(VPScript script) throws VPTranslatorException {
		vpScript = script;
		this.applyToScript(vpScript);
		return this.getResultScript();
	}

	protected abstract CommandScriptWithOptions getResultScript();
	
}
