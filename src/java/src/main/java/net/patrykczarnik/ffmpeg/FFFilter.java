package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.patrykczarnik.commands.CommandText;

public class FFFilter implements CommandText {
	private String name;
	private List<FFFilterOption> options;
	
	private FFFilter(String name) {
		this.name = name;
		this.options = new ArrayList<>();
	}

	private FFFilter(String name, Collection<? extends FFFilterOption> options) {
		this.name = name;
		this.options = new ArrayList<>(options);
	}
	
	public static FFFilter newFilter(String name) {
		return new FFFilter(name);
	}
	
	public static FFFilter newFilter(String name, Collection<? extends FFFilterOption> options) {
		return new FFFilter(name, options);
	}

	public static FFFilter newFilter(String name, FFFilterOption... options) {
		return new FFFilter(name, List.of(options));
	}

	public FFFilter withOption(FFFilterOption option) {
		this.options.add(option);
		return this;
	}
	
	public FFFilter withOptions(FFFilterOption... options) {
		for(FFFilterOption option : options) {
			this.options.add(option);
		}
		return this;
	}
	
	public FFFilter withOptions(Iterable<? extends FFFilterOption> options) {
		for(FFFilterOption option : options) {
			this.options.add(option);
		}
		return this;
	}

	public String getName() {
		return name;
	}
	
	public List<FFFilterOption> getOptions() {
		return Collections.unmodifiableList(this.options);
	}
	
	@Override
	public String getCmdText() {
		if(this.options == null || this.options.isEmpty()) {
			return name;
		} else {
			return name + "=" +
				this.options.stream()
				.map(FFFilterOption::getCmdText)
				.collect(Collectors.joining(":"));
		}
	}
	
	@Override
	public String toString() {
		return getCmdText();
	}
}
