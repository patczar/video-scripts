package net.patrykczarnik.vp.out;

import java.util.List;
import java.util.Optional;

import net.patrykczarnik.commands.Command;
import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterChain;
import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptEntryFile;

public class AudioProcessorNone implements AAudioProcessor {

	@Override
	public List<Command> commandsBefore() {
		return List.of();
	}

	@Override
	public List<Command> commandsForEachFile(VPScriptEntryFile fileSpec) {
		return List.of();
	}

	@Override
	public List<Command> commandsForEachSegment(Segment segment) {
		return List.of();
	}

	@Override
	public List<Positioned<FFFilter>> ffForEachFile(VPScriptEntryFile fileSpec) {
		return List.of();
	}

	@Override
	public List<Positioned<FFFilter>> ffForEachSegment(Segment segment) {
		return List.of();
	}

	@Override
	public List<Command> commandsAfter() {
		return List.of();
	}

	@Override
	public List<FFOption> ffGlobalOptions() {
		return List.of(FFOption.of("an"));
	}

	@Override
	public Optional<FFFilterChain> audioSegmentChain(Segment segment) {
		return Optional.empty();
	}
}
