package net.patrykczarnik.vp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class VPScriptParser {
	private String line;
	private char[] chars;
	private int pos, len;
	
	public static VPScript parse(File scriptFile) throws VPParserException {
		VPScriptParser parser = new VPScriptParser();
		return parser.doParse(scriptFile);
	}
	
	private VPScript doParse(File scriptFile) throws VPParserException {
		try(BufferedReader input = new BufferedReader(new FileReader(scriptFile))) {
			final VPScript vpScript = VPScript.empty();

			while((line = input.readLine()) != null) {
				Optional<VPScriptEntry> optEntry = parseLine();
				if(optEntry.isPresent()) {
					vpScript.addEntry(optEntry.get());
				}
			}
			return vpScript;
		} catch (IOException e) {
			throw new VPParserException("IO error when parsing " + scriptFile, e);
		}
	}
	

	Optional<VPScriptEntry> parseLine() throws VPParserException {
		if(line.startsWith("#")) {
			// comment
			return Optional.empty();
		}
		line = line.strip();
		if(line.isEmpty()) {
			// blank line
			return Optional.empty();
		}
		
		VPScriptEntry entry = parseEntryFromChars();
		return Optional.of(entry);
	}

	private VPScriptEntry parseEntryFromChars() throws VPParserException {
		initChars();
		String command = readUntilWhitespace();
		if("file".equals(command)) {
			return parseFileEntry();
		}
		if(command.startsWith("set-")) {
			return parseOptionsEntry();
		}
		throw new VPParserException("Not recognized command: " + command);
	}

	private void initChars() {
		chars = line.toCharArray();
		pos = 0;
		len = chars.length;
	}
	
	private void skipWhitespace() {
		int i = pos;
		while(i < len && Character.isWhitespace(chars[i])) {
			i++;
		}
		pos = i;
	}
	
	private String readUntilWhitespace() {
		int i = pos;
		while(i < len && !Character.isWhitespace(chars[i])) {
			i++;
		}
		try {
			return String.valueOf(chars, pos, i-pos);
		} finally {
			pos = i;
		}
	}

	private String readTextValue() {
		if(pos == len) {
			return "";
		}
		if(chars[pos] == '"' || chars[pos] == '\'') {
			return readCitedValue();
		}
		
		int i = pos;
		while(i < len && !Character.isWhitespace(chars[i]) && chars[i] != ',') {
			i++;
		}
		try {
			return String.valueOf(chars, pos, i-pos);
		} finally {
			pos = i;
		}
	}

	private String readCitedValue() {
		char citeChar = chars[pos];
		pos++;
		int i = pos;
		while(i < len && chars[i] != citeChar) {
			i++;
		}
		try {
			return String.valueOf(chars, pos, i-pos);
		} finally {
			pos = i+1;
		}
	}

	private VPScriptEntryFile parseFileEntry() {
		skipWhitespace();
		String path = readTextValue();
		
		return VPScriptEntryFile.ofPath(path);
	}

	private VPScriptEntrySetOptions parseOptionsEntry() {
		// TODO Auto-generated method stub
		return new VPScriptEntrySetOptions();
	}
}

