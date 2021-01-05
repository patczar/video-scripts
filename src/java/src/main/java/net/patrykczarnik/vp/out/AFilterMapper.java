package net.patrykczarnik.vp.out;

import java.util.List;

import net.patrykczarnik.ffmpeg.FFFilter;
import net.patrykczarnik.utils.Positioned;
import net.patrykczarnik.vp.in.VPScriptOption;

public interface AFilterMapper {
	List<Positioned<FFFilter>> getFFFilters(VPScriptOption vpOption);

	default List<Positioned<FFFilter>> getPostponed() {
		return List.of();
	}
	
	// TODO SOX filters? Other options?
	
	public static final int POSITION_DEFAULT = 400;

	public static final int POSITION_START = 0;
	public static final int POSITION_VIDEO_START = 100;
	public static final int POSITION_VIDEO_SELECT = 150;
	public static final int POSITION_VIDEO_FORMAT = 200;
	public static final int POSITION_VIDEO_CROP = 280;
	public static final int POSITION_VIDEO_SIZE = 300;
	public static final int POSITION_VIDEO_EQ = 350;
	public static final int POSITION_VIDEO_FINAL_FORMAT = 500;

	public static final int POSITION_AUDIO_START = 600;
	public static final int POSITION_AUDIO_END = 700;
	
	public static final int POSITION_JOIN = 900;
	public static final int POSITION_END = 1000;

}
