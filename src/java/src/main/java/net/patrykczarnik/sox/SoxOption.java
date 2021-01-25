package net.patrykczarnik.sox;

import java.util.List;

import net.patrykczarnik.commands.CommandFragment;

public class SoxOption implements CommandFragment {
	private final String name;
	private final SoxValue value;
	
	private SoxOption(String name, SoxValue value) {
		this.name = name;
		this.value = value;
	}
	
	public static SoxOption noValue(String name) {
		return new SoxOption(name, null);
	}

	public static SoxOption withValue(String name, SoxValue value) {
		return new SoxOption(name, value);
	}

	public static SoxOption withValue(String name, String value) {
		return withValue(name, SoxValue.s(value));
	}

	public static SoxOption withValue(String name, int value) {
		return withValue(name, SoxValue.i(value));
	}

	public static SoxOption withValue(String name, double value) {
		return withValue(name, SoxValue.f(value));
	}

	public String getName() {
		return name;
	}

	public SoxValue getValue() {
		return value;
	}
	
	private String myPrefix() {
		if(name.length() == 1) {
			return "-";
		} else {
			return "--";
		}
	}
	
	private String printName() {
		return myPrefix() + getName();
	}

	@Override
	public List<String> getCmdFragments() {
		if(value != null) {
			return List.of(printName(), value.getCmdText());
		} else {
			return List.of(printName());			
		}
	}
	
}
