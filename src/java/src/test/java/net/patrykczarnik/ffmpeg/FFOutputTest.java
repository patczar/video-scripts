package net.patrykczarnik.ffmpeg;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

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

	@Test
	public void testMap1() {
		FFOutput ffOutput = FFOutput.forFile(FILE1)
				.withMap(FFMap.ofString("map1"))
				.withMap(FFMap.ofStream(1, "a"));
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-map", "map1", "-map", "1:a", FILE1);
	}

	@Test
	public void testMap2() {
		FFOutput ffOutput = FFOutput.forFile(FILE1)
				.withFormat(FORMAT1)
				.withOption(FFOption.of("option1"))
				.withOption(FFOption.of("option2", "value2"))
				.withMap(FFMap.ofLabel("LAB"))
				.withMap(FFMap.ofStream(0, "v"));
		List<String> cmdFragments = ffOutput.getCmdFragments();
		assertThat(cmdFragments).containsExactly("-f", FORMAT1,
				"-option1", "-option2", "value2",
				"-map", "[LAB]", "-map", "0:v", FILE1);
	}

}
