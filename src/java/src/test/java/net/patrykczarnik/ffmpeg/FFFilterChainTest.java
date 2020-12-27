package net.patrykczarnik.ffmpeg;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class FFFilterChainTest {

	@Test
	public void testEmpty() {
		FFFilterChain chain = FFFilterChain.noLabels();
		assertThat(chain.getStartLabels()).isEmpty();
		assertThat(chain.getEndLabels()).isEmpty();
		assertThat(chain.getFilters()).isEmpty();
		assertThat(chain.getCmdText()).isEqualTo("null");
	}

	@Test
	public void testLabels() {
		FFFilterChain chain = FFFilterChain.withLabels("AA", "BB");
		assertThat(chain.getStartLabels()).containsExactly("AA");
		assertThat(chain.getEndLabels()).containsExactly("BB");
		assertThat(chain.getFilters()).isEmpty();
		assertThat(chain.getCmdText()).isEqualTo("[AA]null[BB]");
	}

	@Test
	public void testFilters1() {
		FFFilter filter1 = FFFilter.newFilter("eq",
				FFFilterOption.number("gamma", 1.2),
				FFFilterOption.number("contrast", 1.1));
		FFFilter filter2 = FFFilter.newFilter("crop",
				FFFilterOption.integer(800),
				FFFilterOption.integer(600));
		FFFilterChain chain = FFFilterChain.noLabels(filter1, filter2);
		assertThat(chain.getStartLabels()).isEmpty();
		assertThat(chain.getEndLabels()).isEmpty();
		assertThat(chain.getFilters()).hasSize(2);
		assertThat(chain.getCmdText()).isEqualTo("eq=gamma=1.2:contrast=1.1,crop=800:600");
	}

	@Test
	public void testFilters2() {
		FFFilter filter1 = FFFilter.newFilter("eq",
				FFFilterOption.number("gamma", 1.2),
				FFFilterOption.number("contrast", 1.1));
		FFFilter filter2 = FFFilter.newFilter("crop",
				FFFilterOption.integer(800),
				FFFilterOption.integer(600));
		FFFilterChain chain = FFFilterChain.withLabels("AA", "BB", filter1, filter2);
		assertThat(chain.getStartLabels()).containsExactly("AA");
		assertThat(chain.getEndLabels()).containsExactly("BB");
		assertThat(chain.getFilters()).hasSize(2);
		assertThat(chain.getCmdText()).isEqualTo("[AA]eq=gamma=1.2:contrast=1.1,crop=800:600[BB]");
	}

	@Test
	public void testFilters3() {
		FFFilter filter1 = FFFilter.newFilter("eq",
				FFFilterOption.number("gamma", 1.2),
				FFFilterOption.number("contrast", 1.1));
		FFFilter filter2 = FFFilter.newFilter("crop",
				FFFilterOption.integer(800),
				FFFilterOption.integer(600));
		FFFilter filter3 = FFFilter.newFilter("foo",
				FFFilterOption.citedText("bar"));
		FFFilterChain chain = FFFilterChain.withLabels("AA", "BB");
		chain.addFilters(filter1, filter2);
		chain.addFilter(filter3);
		assertThat(chain.getStartLabels()).containsExactly("AA");
		assertThat(chain.getEndLabels()).containsExactly("BB");
		assertThat(chain.getFilters()).hasSize(3);
		assertThat(chain.getCmdText()).isEqualTo("[AA]eq=gamma=1.2:contrast=1.1,crop=800:600,foo='bar'[BB]");
	}

}
