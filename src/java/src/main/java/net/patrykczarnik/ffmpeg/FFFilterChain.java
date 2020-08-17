package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.patrykczarnik.commands.CommandText;

public class FFFilterChain implements CommandText {
	private String startLabel, endLabel;
	private List<FFFilter> filters;
	
	private FFFilterChain(String startLabel, String endLabel) {
		this.startLabel = startLabel;
		this.endLabel = endLabel;
		this.filters = new ArrayList<>();
	}

	private FFFilterChain(String startLabel, String endLabel, Collection<? extends FFFilter> filters) {
		this.startLabel = startLabel;
		this.endLabel = endLabel;
		this.filters = new ArrayList<>(filters);
	}
	
	public static FFFilterChain withLabels(String startLabel, String endLabel) {
		return new FFFilterChain(startLabel, endLabel);
	}

	public static FFFilterChain withLabels(String startLabel, String endLabel, Collection<? extends FFFilter> filters) {
		return new FFFilterChain(startLabel, endLabel, filters);
	}

	public static FFFilterChain withLabels(String startLabel, String endLabel, FFFilter... filters) {
		return new FFFilterChain(startLabel, endLabel, List.of(filters));
	}

	public static FFFilterChain noLabels() {
		return new FFFilterChain(null, null);
	}

	public static FFFilterChain noLabels(Collection<FFFilter> filters) {
		return new FFFilterChain(null, null, filters);
	}

	public static FFFilterChain noLabels(FFFilter... filters) {
		return new FFFilterChain(null, null, List.of(filters));
	}
	
	public String getStartLabel() {
		return startLabel;
	}
	
	public String getEndLabel() {
		return endLabel;
	}
	
	public List<FFFilter> getFilters() {
		return Collections.unmodifiableList(this.filters);
	}

	public FFFilterChain withStartLabel(String startLabel) {
		this.startLabel = startLabel;
		return this;
	}
	
	public FFFilterChain withEndLabel(String endLabel) {
		this.endLabel = endLabel;
		return this;
	}
	
	public FFFilterChain addFilter(FFFilter filter) {
		this.filters.add(filter);
		return this;
	}
	
	public FFFilterChain addFilters(FFFilter... filters) {
		for(FFFilter filter : filters) {
			this.filters.add(filter);
		}
		return this;
	}
	
	public FFFilterChain addFilters(Iterable<? extends FFFilter> filters) {
		for(FFFilter filter : filters) {
			this.filters.add(filter);
		}
		return this;
	}
	
	@Override
	public String getCmdText() {
		String start = startLabel == null ? "" : ("[" + startLabel + "]");
		String end = endLabel == null ? "" : ("[" + endLabel + "]");
		Stream<String> elements =
			filters.isEmpty()
				? Stream.of("null")
				: filters.stream().map(FFFilter::getCmdText);
		return elements.collect(Collectors.joining(",", start, end));
	}
	
	@Override
	public String toString() {
		return getCmdText();
	}
	
}
