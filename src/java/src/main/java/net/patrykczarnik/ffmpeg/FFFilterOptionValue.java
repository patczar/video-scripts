package net.patrykczarnik.ffmpeg;

import java.util.Objects;

import net.patrykczarnik.commands.CommandText;

abstract class FFFilterOptionValue implements CommandText {
	public static enum ValueType {
		TEXT,
		CITED,
		INT,
		FLOAT,
	}
	
	public abstract ValueType getType();
	
	public abstract String getAsText();

	public abstract Object getAsObject();
	
	@Override
	public String getCmdText() {
		return getAsText();
	}
	
	@Override
	public String toString() {
		return getAsText();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof ValueType)) {
			return false;
		}
		FFFilterOptionValue other = (FFFilterOptionValue)obj;
		return this.getType() == other.getType() && Objects.equals(this.getAsObject(), other.getAsObject());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getType(), this.getAsObject());
	}
	
	public static TextValue text(String text) {
		return new TextValue(text);
	}
	
	public static CitedTextValue citedText(String text) {
		return new CitedTextValue(text);
	}
	
	public static IntValue integer(long value) {
		return new IntValue(value);
	}
	
	public static FloatValue number(double value) {
		return new FloatValue(value);
	}

	private static class TextValue extends FFFilterOptionValue {
		protected String text;

		TextValue(String text) {
			this.text = text;
		}
		
		@Override
		public ValueType getType() {
			return ValueType.TEXT;
		}

		@Override
		public String getAsText() {
			return text;
		}

		@Override
		public String getAsObject() {
			return text;
		}
	}
	
	private static class CitedTextValue extends TextValue {
		CitedTextValue(String text) {
			super(text);
		}
		
		@Override
		public ValueType getType() {
			return ValueType.CITED;
		}

		@Override
		public String getAsText() {
			// TODO escape special characters
			return "\'" + text + "\'";
		}
	}
	
	private static class IntValue extends FFFilterOptionValue {
		private long value;
		
		IntValue(long value) {
			this.value = value;
		}
		
		@Override
		public ValueType getType() {
			return ValueType.INT;
		}

		@Override
		public String getAsText() {
			return String.valueOf(value);
		}

		@Override
		public Number getAsObject() {
			return value;
		}
	}
	
	private static class FloatValue extends FFFilterOptionValue {
		private double value;
		
		FloatValue(double value) {
			this.value = value;
		}
		
		@Override
		public ValueType getType() {
			return ValueType.FLOAT;
		}

		@Override
		public String getAsText() {
			return String.valueOf(value);
		}

		@Override
		public Number getAsObject() {
			return value;
		}
	}
}
