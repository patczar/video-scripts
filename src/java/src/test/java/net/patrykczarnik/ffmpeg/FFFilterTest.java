package net.patrykczarnik.ffmpeg;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class FFFilterTest {

	@Test
	public void testName() {
		FFFilter filter = FFFilter.newFilter("make");
		assertThat(filter.getName()).isEqualTo("make");
		assertThat(filter.getOptions()).isEmpty();
		assertThat(filter.getCmdText()).isEqualTo("make");
	}

	@Test
	public void testOptionsArray1() {
		FFFilter filter = FFFilter.newFilter("crop",
				FFFilterOption.integer(800),
				FFFilterOption.integer(600),
				FFFilterOption.integer(120),
				FFFilterOption.integer(0),
				FFFilterOption.text("lanchos"));
		assertThat(filter.getName()).isEqualTo("crop");
		assertThat(filter.getOptions()).hasSize(5);
		assertThat(filter.getCmdText()).isEqualTo(
				"crop=800:600:120:0:lanchos");
	}

	@Test
	public void testOptionsArray2() {
		FFFilter filter = FFFilter.newFilter("eq",
				FFFilterOption.number("gamma", 1.25),
				FFFilterOption.number("contrast", 1.1));
		assertThat(filter.getName()).isEqualTo("eq");
		assertThat(filter.getOptions()).hasSize(2);
		assertThat(filter.getCmdText()).isEqualTo(
				"eq=gamma=1.25:contrast=1.1");
	}

	@Test
	public void testWithOption() {
		FFFilter filter = FFFilter.newFilter("eq")
				.withOption(FFFilterOption.number("gamma", 1.25))
				.withOption(FFFilterOption.number("contrast", 1.1));
		assertThat(filter.getName()).isEqualTo("eq");
		assertThat(filter.getOptions()).hasSize(2);
		assertThat(filter.getCmdText()).isEqualTo(
				"eq=gamma=1.25:contrast=1.1");
	}

}
