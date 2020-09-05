package net.patrykczarnik.vp;

import net.patrykczarnik.commands.CommandText;

abstract class VPScriptEntry implements CommandText {
	@Override
	public String toString() {
		return getCmdText();
	}
}
