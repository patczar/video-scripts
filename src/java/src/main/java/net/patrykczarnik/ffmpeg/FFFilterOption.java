package net.patrykczarnik.ffmpeg;

import net.patrykczarnik.commands.CommandText;

public class FFFilterOption implements CommandText {
	private String name;
	private FFFilterOptionValue value;
	
	private FFFilterOption(String name, FFFilterOptionValue value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public FFFilterOptionValue getValue() {
		return value;
	}

	public Object getValueObject() {
		return value.getAsObject();
	}

	@Override
	public String getCmdText() {
		if(name == null) {
			return value.getCmdText();
		} else {
			return name + "=" + value.getCmdText();
		}
	}
	
	public static FFFilterOption text(String text) {
		return new FFFilterOption(null, FFFilterOptionValue.text(text));
	}
	
	public static FFFilterOption text(String name, String text) {
		return new FFFilterOption(name, FFFilterOptionValue.text(text));
	}
	
	public static FFFilterOption citedText(String text) {
		return new FFFilterOption(null, FFFilterOptionValue.citedText(text));
	}
	
	public static FFFilterOption citedText(String name, String text) {
		return new FFFilterOption(name, FFFilterOptionValue.citedText(text));
	}

	public static FFFilterOption integer(long value) {
		return new FFFilterOption(null, FFFilterOptionValue.integer(value));
	}
	
	public static FFFilterOption integer(String name, long value) {
		return new FFFilterOption(name, FFFilterOptionValue.integer(value));
	}

	public static FFFilterOption number(double value) {
		return new FFFilterOption(null, FFFilterOptionValue.number(value));
	}
	
	public static FFFilterOption number(String name, double value) {
		return new FFFilterOption(name, FFFilterOptionValue.number(value));
	}
	
}
