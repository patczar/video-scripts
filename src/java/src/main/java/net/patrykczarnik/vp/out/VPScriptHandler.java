package net.patrykczarnik.vp.out;

import net.patrykczarnik.vp.in.VPScript;
import net.patrykczarnik.vp.in.VPScriptEntry;

public interface VPScriptHandler {

	void begin() throws VPTranslatorException;

	void end() throws VPTranslatorException;
	
	void accept(VPScriptEntry entry) throws VPTranslatorException;
	
	default void applyToScript(VPScript script) throws VPTranslatorException {
		this.begin();
		for(VPScriptEntry entry : script.getEntries()) {
			this.accept(entry);
		}
		this.end();
	}
	
}
