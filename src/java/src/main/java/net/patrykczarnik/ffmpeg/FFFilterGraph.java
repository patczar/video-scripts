package net.patrykczarnik.ffmpeg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.patrykczarnik.commands.CommandText;

public class FFFilterGraph extends FFOption implements CommandText {
	private List<FFFilterChain> chains;
	
	private FFFilterGraph() {
		this.chains = new ArrayList<>();
	}

	private FFFilterGraph(Collection<? extends FFFilterChain> chains) {
		this.chains = new ArrayList<>(chains);
	}

	public static FFFilterGraph empty() {
		return new FFFilterGraph();
	}
	
	public static FFFilterGraph ofChains(FFFilterChain chain) {
		return new FFFilterGraph(List.of(chain));
	}
	
	public static FFFilterGraph ofChains(FFFilterChain... chains) {
		return new FFFilterGraph(List.of(chains));
	}
	
	public static FFFilterGraph ofChains(Collection<? extends FFFilterChain> chains) {
		return new FFFilterGraph(chains);
	}
	
	public List<FFFilterChain> getChains() {
		return Collections.unmodifiableList(chains);
	}
	
	public FFFilterGraph addChain(FFFilterChain chain) {
		this.chains.add(chain);
		return this;
	}
	
	public FFFilterGraph addChains(FFFilterChain... chains) {
		for (FFFilterChain chain : chains) {
			this.chains.add(chain);
		}
		return this;
	}
	
	public FFFilterGraph addChains(Iterable<? extends FFFilterChain> chains) {
		for (FFFilterChain chain : chains) {
			this.chains.add(chain);
		}
		return this;
	}
	
	@Override
	public String getCmdText() {
		return chains.stream()
			.map(FFFilterChain::getCmdText)
			.collect(Collectors.joining(";"));
	}

	@Override
	public String getName() {
		return "filter_complex";
	}

	@Override
	public List<String> getRest() {
		return List.of(getCmdText());
	}

}
