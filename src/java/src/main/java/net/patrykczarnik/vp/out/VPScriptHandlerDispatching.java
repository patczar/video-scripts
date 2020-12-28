package net.patrykczarnik.vp.out;

import net.patrykczarnik.vp.in.VPScriptEntry;
import net.patrykczarnik.vp.in.VPScriptEntryFile;
import net.patrykczarnik.vp.in.VPScriptEntrySetOptions;

public interface VPScriptHandlerDispatching extends VPScriptHandler {
	
	default void accept(VPScriptEntry entry) throws VPTranslatorException {
		this.acceptAll(entry);

		if(entry instanceof VPScriptEntrySetOptions) {
			this.acceptSetOptions((VPScriptEntrySetOptions)entry);
		} else if(entry instanceof VPScriptEntryFile) {
			this.acceptFile((VPScriptEntryFile)entry);
		} else {
			this.acceptOther(entry);
		}
	}
	
	void acceptSetOptions(VPScriptEntrySetOptions entry) throws VPTranslatorException;

	void acceptFile(VPScriptEntryFile entry) throws VPTranslatorException;

	default void acceptOther(VPScriptEntry entry) throws VPTranslatorException {
		// default: do nothing
	}
	
	default void acceptAll(VPScriptEntry entry) throws VPTranslatorException {
		// default: do nothing
	}
}
