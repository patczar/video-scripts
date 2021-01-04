package net.patrykczarnik.vp.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.patrykczarnik.commands.CommandText;
import net.patrykczarnik.ffmpeg.FFFilterOption;

public abstract class VPScriptValue implements CommandText {
	public static enum ValueType {
		TEXT,
		MANY,
		INT,
		NUM,
	}
	
	public abstract ValueType getType();

	public abstract String textValue();

	public abstract double numValue();

	public abstract int intValue();
	
	public abstract boolean isNum();

	public abstract boolean isMany();
	
	public abstract List<? extends VPScriptValue> asMany();
	
	public abstract FFFilterOption toFFFilterOption(String optionName);

	public FFFilterOption toFFFilterOption() {
		return toFFFilterOption(null);
	}

	public List<FFFilterOption> toFFFilterOptions() {
		return this.asMany().stream()
			.map(VPScriptValue::toFFFilterOption)
			.collect(Collectors.toList());
	}

	
	@Override
	public String getCmdText() {
		return this.textValue();
	}
	
	@Override
	public String toString() {
		return getCmdText();
	}

	public static Many many() {
		return new Many();
	}
	
	public static Many many(Collection<? extends VPScriptValue.Single> values) {
		return new Many(values);
	}
	
	public static Many many(VPScriptValue.Single... values) {
		return new Many(List.of(values));
	}
	
	public static Text text(String value) {
		return new Text(value, false);
	}
	
	public static Text text(String value, boolean wasCited) {
		return new Text(value, wasCited);
	}
	
	public static Int int_(int value) {
		return new Int(value);
	}
	
	public static Num num_(double value) {
		return new Num(value);
	}
	
	
	public static class Many extends VPScriptValue {
		private List<VPScriptValue.Single> values;
		
		private Many() {
			this.values = new ArrayList<>();
		}
		
		private Many(Collection<? extends VPScriptValue.Single> values) {
			this.values = new ArrayList<>(values);
		}
		
		@Override
		public ValueType getType() {
			return ValueType.MANY;
		}
		
		public Many add(VPScriptValue.Single value) {
			this.values.add(value);
			return this;
		}
		
		@Override
		public List<? extends VPScriptValue> asMany() {
			return this.values;
		}

		@Override
		public String textValue() {
			return values.stream().map(VPScriptValue::textValue).collect(Collectors.joining(","));
		}

		@Override
		public double numValue() {
			// TODO Auto-generated method stub
			return 0.0;
		}

		@Override
		public int intValue() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isNum() {
			return false;
		}

		@Override
		public boolean isMany() {
			return true;
		}

		@Override
		public FFFilterOption toFFFilterOption(String optionName) {
			throw new IllegalStateException("VPValue of type MANY cannot be converted into a single FFFilterOption");
		}
	}
	
	public static abstract class Single extends VPScriptValue {
		@Override
		public List<VPScriptValue> asMany() {
			return List.of(this);
		}
		
		@Override
		public boolean isMany() {
			return false;
		}
	}
	
	public static class Text extends Single {
		private String value;
		private boolean wasCited;
		
		private Text(String value, boolean wasCited) {
			this.value = value;
			this.wasCited = wasCited;
		}

		@Override
		public ValueType getType() {
			return ValueType.TEXT;
		}
		
		public boolean wasCited() {
			return wasCited;
		}
		
		@Override
		public String textValue() {
			return value;
		}

		@Override
		public double numValue() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int intValue() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isNum() {
			return false;
		}
		
		@Override
		public String getCmdText() {
			if(wasCited) {
				if(value.contains("\"")) {
					return "'" + this.textValue() + "'";
				} else {
					return "\"" + this.textValue() + "\"";
				}
			} else {
				return this.textValue();
			}
		}

		@Override
		public FFFilterOption toFFFilterOption(String optionName) {
			if(wasCited()) {
				return FFFilterOption.citedText(optionName, value);
			} else {
				return FFFilterOption.text(optionName, value);
			}
		}
	}
	
	public static class Int extends Single {
		private int value;
		
		private Int(int value) {
			this.value = value;
		}

		@Override
		public ValueType getType() {
			return ValueType.INT;
		}
		
		@Override
		public String textValue() {
			return String.valueOf(value);
		}

		@Override
		public double numValue() {
			return value;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public boolean isNum() {
			return true;
		}

		@Override
		public FFFilterOption toFFFilterOption(String optionName) {
			return FFFilterOption.integer(optionName, value);
		}
	}

	public static class Num extends Single {
		private double value;
		
		private Num(double value) {
			this.value = value;
		}

		@Override
		public ValueType getType() {
			return ValueType.NUM;
		}
		
		@Override
		public String textValue() {
			return String.valueOf(value);
		}

		@Override
		public double numValue() {
			return value;
		}

		@Override
		public int intValue() {
			return (int)value;
		}

		@Override
		public boolean isNum() {
			return true;
		}

		@Override
		public FFFilterOption toFFFilterOption(String optionName) {
			return FFFilterOption.number(optionName, value);
		}
	}
}
