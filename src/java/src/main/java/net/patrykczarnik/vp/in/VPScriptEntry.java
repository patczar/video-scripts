package net.patrykczarnik.vp.in;

import net.patrykczarnik.commands.CommandText;

public abstract class VPScriptEntry implements CommandText {
	@Override
	public String toString() {
		return getCmdText();
	}

	public abstract boolean isSetOptions();

	public abstract VPScriptEntrySetOptions asSetOptions();
}
