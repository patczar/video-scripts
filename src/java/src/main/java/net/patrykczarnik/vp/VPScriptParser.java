package net.patrykczarnik.vp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.patrykczarnik.vp.VPScriptValue.Single;

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

	private String readText() {
		if(pos == len) {
			return "";
		}
		if(chars[pos] == '"' || chars[pos] == '\'') {
			return readCited();
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

	private String readCited() {
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
	
	private List<VPScriptOption> readOptions() throws VPParserException {
		List<VPScriptOption> list = new ArrayList<>();
		while(pos < len) {
			skipWhitespace();
			if(chars[pos] != '-') {
				throw new VPParserException("- expected at this point");
			} else {
				pos++;
			}
			String name = readUntilWhitespace();
			
			List<VPScriptValue.Single> values = new ArrayList<>();
			VPScriptValue.Single nextValue;
			while((nextValue = readValue()) != null) {
				values.add(nextValue);
			}

			VPScriptValue optionValue;
			if(values.size() == 0) {
				optionValue = null;
			} else if(values.size() == 1) {
				optionValue = values.get(0);
			} else {
				optionValue = VPScriptValue.many(values);
			}
			
			VPScriptOption option = VPScriptOption.of(name, optionValue);
			list.add(option);
		}
		return list;
	}

	private VPScriptValue.Single readValue() {
		skipWhitespace();
		if(pos < len && chars[pos] == ',') {
			pos++;
			skipWhitespace();
		}

		if(pos >= len || chars[pos] == '-') {
			return null;
		}
		

		if(chars[pos] == '"' || chars[pos] == '\'') {
			String txt = readCited();
			return VPScriptValue.text(txt, true);
		}
		{
			String txt = readText();
			try {
				int ival = Integer.parseInt(txt);
				return VPScriptValue.int_(ival);				
			} catch(NumberFormatException e) {
			}
			try {
				double dval = Double.parseDouble(txt);
				return VPScriptValue.num_(dval);				
			} catch(NumberFormatException e) {
			}
			return VPScriptValue.text(txt, false);
		}
	}

	private VPScriptEntryFile parseFileEntry() throws VPParserException {
		skipWhitespace();
		String path = readText();
		VPScriptEntryFile entry = VPScriptEntryFile.ofPath(path);
		List<VPScriptOption> options = readOptions();
		for (VPScriptOption option : options) {
			switch(option.getName()) {
			case "s":
				entry.setStart(option.numValue());
				break;
			case "e":
				entry.setEnd(option.numValue());
				break;
			}
		}
		return entry;
	}

	private VPScriptEntrySetOptions parseOptionsEntry() {
		// TODO Auto-generated method stub
		return new VPScriptEntrySetOptions();
	}
}

