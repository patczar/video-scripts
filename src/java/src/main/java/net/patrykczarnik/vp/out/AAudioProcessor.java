package net.patrykczarnik.vp.out;

import java.util.List;
import java.util.Optional;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptEntryFile;

public interface AAudioProcessor {
	List<Command> commandsBefore();
	
	List<Command> commandsForEachFile(VPScriptEntryFile fileSpec);

 	List<Command> commandsForEachSegment(Segment segment);

 	List<Positioned<FFFilter>> ffForEachFile(VPScriptEntryFile fileSpec);

 	List<Positioned<FFFilter>> ffForEachSegment(Segment segment);

 	Optional<FFFilterChain> audioSegmentChain(Segment segment);
	
	List<Command> commandsAfter();
	
	List<FFOption> ffGlobalOptions();
	
	public static AAudioProcessor getImpl(String name, FiltersRegistry filtersRegistry) {
		switch(name) {
			case "sox": return new AudioProcessorSox(filtersRegistry);
			case "none":
			case "":
				return new AudioProcessorNone();
			
			default: throw new IllegalArgumentException("Audio processor not known: " + name);
		}
	}
}
