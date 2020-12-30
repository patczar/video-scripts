package net.patrykczarnik.vp.in;

import net.patrykczarnik.commands.CommandText;
import net.patrykczarnik.ffmpeg.FFOption;

public class VPScriptOption implements CommandText, Cloneable {
	private final String name;
	private VPScriptValue value;
	
	private VPScriptOption(String name, VPScriptValue value) {
		this.name = name;
		this.value = value;
	}
	
	public static VPScriptOption of(String name, VPScriptValue value) {
		return new VPScriptOption(name, value);
	}
	
	public static VPScriptOption withName(String name) {
		return new VPScriptOption(name, null);
	}
	
	public VPScriptOption withValue(VPScriptValue value) {
		this.value = value;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public VPScriptValue getValue() {
		return value;
	}

	public String textValue() {
		if(value == null || value.textValue() == null) {
			return "";
		} else {
			return value.textValue();
		}
	}

	public double numValue() {
		if(value == null || !value.isNum()) {
			return 0.0;
		} else {
			return value.numValue();
		}
	}

	public int intValue() {
		if(value == null || !value.isNum()) {
			return 0;
		} else {
			return value.intValue();
		}
	}

	@Override
	public String toString() {
		return getCmdText();
	}

	@Override
	public String getCmdText() {
		String sValue = value == null ? "" : value.toString();
		return "-" + name + " " + sValue;
	}
	
	@Override
	public VPScriptOption clone() {
		return new VPScriptOption(this.name, this.value);
	}

	public FFOption toFFOption() {
		return FFOption.of(getName(), textValue()); // FIXME
	}
}
