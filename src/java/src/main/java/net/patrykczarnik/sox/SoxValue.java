package net.patrykczarnik.sox;

import net.patrykczarnik.commands.CommandText;

public abstract class SoxValue implements CommandText {

	public static SoxValue_String s(String value) {
		return new SoxValue_String(value);
	}

	public static SoxValue_Int i(int value) {
		return new SoxValue_Int(value);
	}

	public static SoxValue_Float f(double value) {
		return new SoxValue_Float(value);
	}

	public String textValue() {
		return this.getCmdText();
	}
	
	public abstract double numValue();
	
	@Override
	public String toString() {
		return this.getCmdText();
	}

	static class SoxValue_String extends SoxValue {
		private final String value;

		private SoxValue_String(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String getCmdText() {
			return value;
		}

		@Override
		public double numValue() {
			try {
				return Double.parseDouble(value);
			} catch(NumberFormatException e) {
				return 0.0;
			}
		}
	}

	static class SoxValue_Int extends SoxValue {
		private final int value;

		private SoxValue_Int(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String getCmdText() {
			return String.valueOf(value);
		}

		@Override
		public double numValue() {
			return value;
		}
	}

	static class SoxValue_Float extends SoxValue {
		private final double value;

		private SoxValue_Float(double value) {
			this.value = value;
		}

		public double getValue() {
			return value;
		}

		@Override
		public String getCmdText() {
			return String.valueOf(value);
		}

		@Override
		public double numValue() {
			return value;
		}
	}	
}
