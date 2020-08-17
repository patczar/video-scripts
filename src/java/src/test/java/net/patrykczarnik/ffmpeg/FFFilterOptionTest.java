package net.patrykczarnik.ffmpeg;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class FFFilterOptionTest {

	@Test
	public void testValueText() {
		FFFilterOption fo = FFFilterOption.text("abcd");
		assertThat(fo.getName()).isNull();
		assertThat(fo.getValueObject())
			.isInstanceOf(String.class)
			.isEqualTo("abcd");
		assertThat(fo.getCmdText()).isEqualTo("abcd");
	}

	@Test
	public void testValueTextName() {
		FFFilterOption fo = FFFilterOption.text("foo", "abcd");
		assertThat(fo.getName()).isEqualTo("foo");
		assertThat(fo.getValueObject())
			.isInstanceOf(String.class)
			.isEqualTo("abcd");
		assertThat(fo.getCmdText()).isEqualTo("foo=abcd");
	}

	@Test
	public void testValueCitedText() {
		FFFilterOption fo = FFFilterOption.citedText("ab cd");
		assertThat(fo.getName()).isNull();
		assertThat(fo.getValueObject())
			.isInstanceOf(String.class)
			.isEqualTo("ab cd");
		assertThat(fo.getCmdText()).isEqualTo("'ab cd'");
	}

	@Test
	public void testValueCitedTextName() {
		FFFilterOption fo = FFFilterOption.citedText("foo", "ab cd");
		assertThat(fo.getName()).isEqualTo("foo");
		assertThat(fo.getValueObject())
			.isInstanceOf(String.class)
			.isEqualTo("ab cd");
		assertThat(fo.getCmdText()).isEqualTo("foo='ab cd'");
	}

	@Test
	public void testValueInteger() {
		FFFilterOption fo = FFFilterOption.integer(1234);
		assertThat(fo.getName()).isNull();
		assertThat(fo.getValueObject())
			.isInstanceOf(Number.class);
		assertThat(((Number)fo.getValueObject()).intValue())
			.isEqualTo(1234);
		assertThat(fo.getCmdText()).isEqualTo("1234");
	}

	@Test
	public void testValueIntegerName() {
		FFFilterOption fo = FFFilterOption.integer("foo", 1234);
		assertThat(fo.getName()).isEqualTo("foo");
		assertThat(fo.getValueObject())
			.isInstanceOf(Number.class);
		assertThat(((Number)fo.getValueObject()).intValue())
			.isEqualTo(1234);
		assertThat(fo.getCmdText()).isEqualTo("foo=1234");
	}

	@Test
	public void testValueFloat() {
		FFFilterOption fo = FFFilterOption.number(12.25);
		assertThat(fo.getName()).isNull();
		assertThat(fo.getValueObject())
			.isInstanceOf(Number.class);
		assertThat(((Number)fo.getValueObject()).doubleValue())
			.isCloseTo(12.25, offset(0.001));
		assertThat(fo.getCmdText()).isEqualTo("12.25");
	}

	@Test
	public void testValueFloatName() {
		FFFilterOption fo = FFFilterOption.number("foo", 12.25);
		assertThat(fo.getName()).isEqualTo("foo");
		assertThat(fo.getValueObject())
			.isInstanceOf(Number.class);
		assertThat(((Number)fo.getValueObject()).doubleValue())
			.isCloseTo(12.25, offset(0.001));
		assertThat(fo.getCmdText()).isEqualTo("foo=12.25");
	}

}
