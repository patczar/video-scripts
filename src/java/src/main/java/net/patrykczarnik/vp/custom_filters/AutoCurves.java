package net.patrykczarnik.vp.custom_filters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.ffmpeg.FFFilterOption;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;
import net.patrykczarnik.vp.in.VPScriptValue;
import net.patrykczarnik.vp.out.AFilterMapper;
import net.patrykczarnik.vp.out.AParamOrientedFilterMapper;

public class AutoCurves implements AParamOrientedFilterMapper {
	public static final int POSITION = AFilterMapper.POSITION_VIDEO_EQ+1;
	
	private int points = 256;
	private double g = 1.0; // should work as gamma, any value >0 is correct, 1.0 is neutral, use >1 to brighten the picture, especially darker parts 
	private double a = 0.0; // brightness (stronger for light parts than gamma) without overflow, proper values are between 0 and 1, 0 is neutral
	private double c = 0.0; // non-linear contrast without overflow, proper values are between 0.0 (neutral) and 1.0 (contrast maximally increased)

	@Override
	public void begin() {
	}

	@Override
	public void acceptOption(VPScriptOption vpOption) {
		if("autocurves".equals(vpOption.getName())) {
			List<? extends VPScriptValue> paramValues = vpOption.getValue().asMany();
			if(paramValues.size() >= 1) {
				g = paramValues.get(0).numValue();
			}
			if(paramValues.size() >= 2) {
				a = paramValues.get(1).numValue();
			}
			if(paramValues.size() >= 3) {
				c = paramValues.get(2).numValue();
			}
			System.out.println("g=" + g + ", a="+a + ", c="+c);
		}
	}

	@Override
	public List<Positioned<FFFilter>> getCollectedFFFilters() {
		
		FFFilter filter = FFFilter.newFilter("curves", FFFilterOption.citedText("master", printPoints()));
		return List.of(Positioned.of(351, filter));
	}

	@Override
	public Set<String> observedParams() {
		return Set.of("autocurves");
	}
	
	private double curvesFunction(double x) {
		return Math.pow((1.0-c)*(x + a*(Math.pow(0.5, 2) - Math.pow(0.5-x, 2))) + c*(2*(x+(0.5-x)*Math.abs(0.5-x)) - 0.5), 1.0/g);
	}
	
	private String printPoints() {
		return IntStream.range(0, points)
			.mapToDouble(i -> (double)i / (points-1))
			.mapToObj(x -> x + "/" + curvesFunction(x))
			.collect(Collectors.joining(" "));
	}

}

// examples to check i.e. in kmplot
// g=1.5: f(x) = (x) ^ (1/1.5)
// a=0.7: f(x) = (x + 0.7∙(0.5^2 −(0.5−x)^2))
// c=1.0: f(x) = 1 ∙( 2∙(x + (0.5−x)∙abs(0.5−x)) − 0.5)
// all combined with g = 1.4, a = 0.3, c = 0.6:
// f(x) =  (0.4(x + 0.3∙(0.5^2 −(0.5−x)^2)) + 0.6∙( 2∙(x + (0.5−x)∙abs(0.5−x)) − 0.5)) ^ (1/1.4)
