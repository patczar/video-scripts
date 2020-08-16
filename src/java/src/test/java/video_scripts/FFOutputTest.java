package video_scripts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import net.patrykczarnik.ffmpeg.FFOption;
import net.patrykczarnik.ffmpeg.FFOutput;

public class FFOutputTest {

	private static final String FILE1 = "file.mp4";
	private static final String FORMAT1 = "image";

	@Test
	public void testFileName() {
		FFOutput ffOutput = FFOutput.forFile(FILE1);
		assertThat(ffOutput.getFile()).isEqualTo(FILE1);
		assertThat(ffOutput.getFormat()).isNull();
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly(FILE1);
	}

	@Test
	public void testFormat() {
		FFOutput ffOutput = FFOutput.forFile(FILE1)
				.withFormat(FORMAT1);
		assertThat(ffOutput.getFile()).isEqualTo(FILE1);
		assertThat(ffOutput.getFormat()).isEqualTo(FORMAT1);
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-f", FORMAT1, FILE1);
	}

	@Test
	public void testFormatAndOptions() {
		FFOutput ffOutput = FFOutput.forFile(FILE1)
				.withFormat(FORMAT1)
				.withOption(FFOption.of("option1"))
				.withOption(FFOption.of("option2", "value2"))
				.withOption(FFOption.of("option3", "value3"))
				.withOption(FFOption.of("option4"));
		assertThat(ffOutput.getFile()).isEqualTo(FILE1);
		assertThat(ffOutput.getFormat()).isEqualTo(FORMAT1);
		assertThat(ffOutput.getOptions()).hasSize(4);
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-f", FORMAT1,
				"-option1", "-option2", "value2", "-option3", "value3", "-option4", FILE1);
	}

	@Test
	public void testNoFormatAndOptions() {
		FFOutput ffOutput = FFOutput.forFile(FILE1)
				.withOption(FFOption.of("option1"))
				.withOption(FFOption.of("option2", "value2"));
		assertThat(ffOutput.getFile()).isEqualTo(FILE1);
		assertThat(ffOutput.getFormat()).isNull();
		assertThat(ffOutput.getOptions()).hasSize(2);
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-option1", "-option2", "value2", FILE1);
	}

}
