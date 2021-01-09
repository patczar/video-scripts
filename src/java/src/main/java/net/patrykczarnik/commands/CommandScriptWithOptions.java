package net.patrykczarnik.commands;

import java.io.File;

public interface CommandScriptWithOptions extends CommandScript {
	File getWorkingDir();
}
