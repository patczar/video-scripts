package net.patrykczarnik.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.patrykczarnik.ffmpeg.FFFilter;

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

	/** Retrieves a list of all the elements of <var>list</var> for which the <var>predicate</var> is satisfied.
	 * The original list is not modified.
	 * 
	 * @param <E> the type of list elements
	 * @param list the input list
	 * @param predicate the predicate which is checked for every element
	 * @return a new list containing the elements of <var>list</var> for which the <var>predicate</var> holds. The order of elements is preserved. The elements (objects) are not copied.
	 */
	public static <E> List<E> sublist(List<E> list,
			Predicate<? super E> predicate) {
		List<E> resultList = new ArrayList<>();
		for(E e : list) {
			if(predicate.test(e)) {
				resultList.add(e);
			}
		}
		return resultList;
	}

}
