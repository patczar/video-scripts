package net.patrykczarnik.ffmpeg;

import java.util.List;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.CommandFragment;

public abstract class FFOption implements CommandFragment {
	private static final String PREFIX = "-";
	
	public static FFOption of(String name) {
		return new FFOptionSingle(name);
	}

	public static FFOption of(String name, String value) {
		return new FFOptionWithValue(name, value);
	}
	
	public abstract String getName();

	public abstract List<String> getRest();

	public List<String> getCmdFragments() {
		return CmdUtils.joinAnyCmdFragments(PREFIX + getName(), getRest());
	}		


	private static class FFOptionSingle extends FFOption {
		private final String name;
		
		FFOptionSingle(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public List<String> getRest() {
			return List.of();
		}
	}
	
	private static class FFOptionWithValue extends FFOption {
		private final String name;
		private final String value;
		
		FFOptionWithValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}
		
		@Override
		public List<String> getRest() {
			return List.of(value);
		}
	}
}
