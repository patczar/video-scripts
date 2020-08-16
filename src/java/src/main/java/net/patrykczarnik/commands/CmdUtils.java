package net.patrykczarnik.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class CmdUtils {
	
	private CmdUtils() {
		throw new IllegalStateException("This class should not be instantiated");
	}
	
	public static List<String> joinCmdFragments(CommandFragment... fragments) {
		List<String> list = newList();
		appendCmdFragments(list, fragments);
		return list;
	}
	
	public static List<String> joinCmdFragments(Iterable<? extends CommandFragment> fragments) {
		List<String> list = newList();
		appendCmdFragments(list, fragments);
		return list;
	}
	
	public static List<String> joinCmdFragments(Stream<? extends CommandFragment> fragments) {
		List<String> list = newList();
		appendCmdFragments(list, fragments);
		return list;
	}
	
	@SafeVarargs
	public static List<String> joinCmdFragments(Iterable<? extends CommandFragment>... collections) {
		List<String> list = newList();
		for(var collection : collections) {
			appendCmdFragments(list, collection);
		}
		return list;
	}
	
	@SafeVarargs
	public static List<String> joinAnyCmdFragments(Object... objects) {
		List<String> list = newList();
		appendAnyCmdFragments(list, objects);
		return list;
	}
	
	public static void appendCmdFragment(List<String> list, CommandFragment fragment) {
		list.addAll(fragment.getCmdFragments());
	}
	
	@SafeVarargs
	public static void appendCmdFragments(List<String> list, CommandFragment... fragments) {
		for(CommandFragment fragment : fragments) {
			appendCmdFragment(list, fragment);
		}
	}
	
	public static void appendCmdFragments(List<String> list, Iterable<? extends CommandFragment> fragments) {
		for(CommandFragment fragment : fragments) {
			appendCmdFragment(list, fragment);
		}
	}
	
	public static void appendCmdFragments(List<String> list, Stream<? extends CommandFragment> fragments) {
		fragments.forEachOrdered(fragment -> appendCmdFragments(list, fragment));
	}

	public static void appendAnyCmdFragments(List<String> list, Object... objects) {
		for(Object object : objects) {
			appendAnyObject(list, object);
		}
	}

	private static void appendAnyObject(List<String> list, Object object) {
		if(object == null) {
			// pass
		} else if(object instanceof CommandFragment) {
			appendCmdFragment(list, (CommandFragment)object);
		} else if(object.getClass().isArray()) {
			appendAnyCmdFragments(list, (Object[])object);
		} else if(object instanceof Iterable) {
			((Iterable<?>)object).forEach(o -> appendAnyObject(list, o));
		} else if(object instanceof Stream) {
			((Stream<?>)object).forEach(o -> appendAnyObject(list, o));
		} else if(object instanceof Optional) {
			Optional<?> opt = (Optional<?>)object;
			if(opt.isPresent()) {
				appendAnyObject(list, opt.get());
			}
		} else if(object instanceof String) {
			list.add((String)object);
		} else {
			list.add(object.toString());
		}
	}

	private static List<String> newList() {
		return new ArrayList<>();
	}
}
