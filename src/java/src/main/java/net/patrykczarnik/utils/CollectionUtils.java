package net.patrykczarnik.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class CollectionUtils {
	private CollectionUtils() {
		
	}
	
	public static <T, R> List<R> mapList(Collection<? extends T> source, Function<T, R> fun) {
		List<R> result = new ArrayList<>(source.size());
		for(T value : source) {
			result.add(fun.apply(value));
		}
		return result;
	}

}
