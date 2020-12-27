package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.patrykczarnik.commands.CommandText;

public class FFFilterChain implements CommandText {
	private List<String> startLabels = new ArrayList<>();
	private List<String> endLabels = new ArrayList<>();
	private List<FFFilter> filters;
	
	private FFFilterChain() {
		this.filters = new ArrayList<>();
	}

	private FFFilterChain(Collection<? extends FFFilter> filters) {
		this.filters = new ArrayList<>(filters);
	}
	
	public static FFFilterChain withLabels(String startLabel, String endLabel) {
		FFFilterChain chain = new FFFilterChain();
		chain.startLabels.add(startLabel);
		chain.endLabels.add(endLabel);
		return chain;
	}

	public static FFFilterChain withLabels(String startLabel, String endLabel, Collection<? extends FFFilter> filters) {
		FFFilterChain chain = new FFFilterChain(filters);
		chain.startLabels.add(startLabel);
		chain.endLabels.add(endLabel);
		return chain;		
	}

	public static FFFilterChain withLabels(String startLabel, String endLabel, FFFilter... filters) {
		FFFilterChain chain = new FFFilterChain(List.of(filters));
		chain.startLabels.add(startLabel);
		chain.endLabels.add(endLabel);
		return chain;
	}

	public static FFFilterChain withLabels(Collection<String> startLabels, Collection<String> endLabels, Collection<? extends FFFilter> filters) {
		FFFilterChain chain = new FFFilterChain(filters);
		chain.startLabels.addAll(startLabels);
		chain.endLabels.addAll(endLabels);
		return chain;		
	}

	public static FFFilterChain noLabels() {
		return new FFFilterChain();
	}

	public static FFFilterChain noLabels(Collection<FFFilter> filters) {
		return new FFFilterChain(filters);
	}

	public static FFFilterChain noLabels(FFFilter... filters) {
		return new FFFilterChain(List.of(filters));
	}
	
	public List<String> getStartLabels() {
		return Collections.unmodifiableList(startLabels);
	}
	
	public List<String> getEndLabels() {
		return Collections.unmodifiableList(endLabels);
	}
	
	public List<FFFilter> getFilters() {
		return Collections.unmodifiableList(this.filters);
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
	
	public FFFilterChain addStartLabels(String... labels) {
		startLabels.addAll(List.of(labels));
		return this;
	}

	public FFFilterChain addStartLabels(Collection<String> labels) {
		startLabels.addAll(labels);
		return this;
	}
	
	public FFFilterChain addEndLabels(String... labels) {
		endLabels.addAll(List.of(labels));
		return this;
	}
	
	public FFFilterChain addEndLabels(Collection<String> labels) {
		endLabels.addAll(labels);
		return this;
	}
	
	@Override
	public String getCmdText() {
		String start = startLabels.stream()
				.map(lab -> "[" + lab + "]")
				.collect(Collectors.joining());

		String end = endLabels.stream()
				.map(lab -> "[" + lab + "]")
				.collect(Collectors.joining());

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
