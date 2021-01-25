package net.patrykczarnik.sox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.patrykczarnik.commands.CmdUtils;
import net.patrykczarnik.commands.CommandFragment;

public class SoxEffect implements CommandFragment {
	private final String name;
	private final List<SoxValue> params;

	private SoxEffect(String name, List<SoxValue> params) {
		this.name = name;
		this.params = params;
	}

	public static SoxEffect of(String name) {
		return new SoxEffect(name, new ArrayList<>());
	}

	public static SoxEffect withOptions(String name, Collection<? extends SoxValue> params) {
		return new SoxEffect(name, new ArrayList<>(params));
	}

	public static SoxEffect withOptions(String name, SoxValue... params) {
		return new SoxEffect(name, new ArrayList<>(List.of(params)));
	}
	
	public String getName() {
		return name;
	}

	public List<SoxValue> getParams() {
		return Collections.unmodifiableList(params);
	}

	@Override
	public List<String> getCmdFragments() {
		return CmdUtils.joinAnyCmdFragments(name, params);
	}

	@Override
	public String toString() {
		return this.getCmdText();
	}
}
