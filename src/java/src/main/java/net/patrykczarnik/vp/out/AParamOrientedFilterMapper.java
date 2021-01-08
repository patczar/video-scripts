package net.patrykczarnik.vp.out;

import java.util.Set;

public interface AParamOrientedFilterMapper extends AFilterMapper {
	Set<String> observedParams();
}
