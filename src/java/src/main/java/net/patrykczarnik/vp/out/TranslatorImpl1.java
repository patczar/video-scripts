package net.patrykczarnik.vp.out;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.patrykczarnik.commands.CommandScript;
import net.patrykczarnik.commands.CommandScriptImpl;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFFilterGraph;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.ffmpeg.FFInput;
import net.patrykczarnik.ffmpeg.FFMPEG;
import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.vp.in.VPScriptEntryFile;
import net.patrykczarnik.vp.in.VPScriptEntrySetOptions;

/**
 * @author Patryk Czarnik
 * 
 * Implementation based on stream concatenation.
 * 
 */
public class TranslatorImpl1 extends TranslatorAbstractImpl {
	private List<FFInput> inputs;
	private List<FFFilterChain> chunkChains;
	private int nextInput = 0;
	private CommandScript resultScript = null;

	@Override
	public void begin() {
		inputs = new ArrayList<>();
		chunkChains = new ArrayList<>();
	}

	@Override
	public void end() {
		FFMPEG ffmpeg = new FFMPEG();
		ffmpeg.addInputs(inputs);
		
		FFFilterGraph filterGraph = FFFilterGraph.ofChains(chunkChains);
		FFFilter concatFilter = FFFilter.newFilter("concat",
				FFFilterOption.integer("n", chunkChains.size()),
				FFFilterOption.integer("v", 1),
				FFFilterOption.integer("a", 0));
		
		List<String> startLabels = IntStream.range(0, nextInput)
				.mapToObj(TranslatorImpl1::chunkLabel)
				.collect(Collectors.toList());
		
		FFFilterChain concatChain = FFFilterChain.withLabels(startLabels, List.of("v"), List.of(concatFilter));
		filterGraph.addChain(concatChain);
		ffmpeg.setFilterGraph(filterGraph);
		
		resultScript = CommandScriptImpl.of(ffmpeg);
	}

	@Override
	public void acceptSetOptions(VPScriptEntrySetOptions entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptFile(VPScriptEntryFile entry) {
		FFInput newInput = FFInput.forFile(entry.getPath());
		if(entry.getStart() != null) {
			newInput.withOption(FFOption.of("ss", String.valueOf(entry.getStart())));
		}
		if(entry.getEnd() != null) {
			newInput.withOption(FFOption.of("to", String.valueOf(entry.getEnd())));
		}
		inputs.add(newInput);
		
		String startLabel = nextInput + ":0";
		String endLabel = chunkLabel(nextInput);
		FFFilterChain newChain = FFFilterChain.withLabels(startLabel, endLabel);
		chunkChains.add(newChain);
		nextInput++;
	}

	@Override
	protected CommandScript getResultScript() {
		return resultScript;
	}

	private static String chunkLabel(int n) {
		return "v" + n;
	}

}
