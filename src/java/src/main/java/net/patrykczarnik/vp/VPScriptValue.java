package net.patrykczarnik.vp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.patrykczarnik.commands.CommandText;

public abstract class VPScriptValue implements CommandText {
	public abstract String textValue();

	public abstract double numValue();

	public abstract int intValue();
	
	public abstract boolean isNum();

	public abstract boolean isMany();
	
	public abstract List<? extends VPScriptValue> asMany();
	
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
	}
	
	public static class Int extends Single {
		private int value;
		
		private Int(int value) {
			this.value = value;
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
	}

	public static class Num extends Single {
		private double value;
		
		private Num(double value) {
			this.value = value;
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
	}
}
