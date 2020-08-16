package net.patrykczarnik.ffmpeg;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

public class FFInputTest {
	private static final String FILE1 = "file.mp4";
	private static final String FORMAT1 = "image";

	@Test
	public void testFileName() {
		FFInput ffInput = FFInput.forFile(FILE1);
		assertThat(ffInput.getFile()).isEqualTo(FILE1);
		assertThat(ffInput.getFormat()).isNull();
		List<String> cmdFragments = ffInput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-i", FILE1);
	}

	@Test
	public void testFormat() {
		FFInput ffInput = FFInput.forFile(FILE1)
				.withFormat(FORMAT1);
		assertThat(ffInput.getFile()).isEqualTo(FILE1);
		assertThat(ffInput.getFormat()).isEqualTo(FORMAT1);
		List<String> cmdFragments = ffInput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-f", FORMAT1, "-i", FILE1);
	}

	@Test
	public void testFormatAndOptions() {
		FFInput ffInput = FFInput.forFile(FILE1)
				.withFormat(FORMAT1)
				.withOption(FFOption.of("option1"))
				.withOption(FFOption.of("option2", "value2"))
				.withOption(FFOption.of("option3", "value3"))
				.withOption(FFOption.of("option4"));
		assertThat(ffInput.getFile()).isEqualTo(FILE1);
		assertThat(ffInput.getFormat()).isEqualTo(FORMAT1);
		assertThat(ffInput.getOptions())
			.hasSize(4);
		List<String> cmdFragments = ffInput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-f", FORMAT1,
				"-option1", "-option2", "value2", "-option3", "value3", "-option4", "-i", FILE1);
	}

	@Test
	public void testNoFormatAndOptions() {
		FFInput ffInput = FFInput.forFile(FILE1)
				.withOption(FFOption.of("option1"))
				.withOption(FFOption.of("option2", "value2"));
		assertThat(ffInput.getFile()).isEqualTo(FILE1);
		assertThat(ffInput.getFormat()).isNull();
		assertThat(ffInput.getOptions())
			.hasSize(2);
		List<String> cmdFragments = ffInput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-option1", "-option2", "value2", "-i", FILE1);
	}

}
