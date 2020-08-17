package net.patrykczarnik.ffmpeg;

import net.patrykczarnik.commands.CommandText;

abstract class FFFilterOptionValue implements CommandText {
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
		public String getAsText() {
			return String.valueOf(value);
		}

		@Override
		public Number getAsObject() {
			return value;
		}
	}
}
