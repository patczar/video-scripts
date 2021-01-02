package net.patrykczarnik.utils;

import java.util.Objects;
import java.util.function.Predicate;

public class Positioned<T> implements Comparable<Positioned<?>> {
	private final int position;
	private final T value;

	public Positioned(int position, T value) {
		this.position = position;
		this.value = value;
	}
	
	public static <T> Positioned<T> of(int position, T value) {
		Objects.requireNonNull(value);
		return new Positioned<>(position, value);
	}
	
	public final int getPosition() {
		return position;
	}

	public final T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "(p" + position + ":" + value + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(position, value);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Positioned<?> other = (Positioned<?>)obj;
		return position == other.position && Objects.equals(value, other.value);
	}

	@Override
	public int compareTo(Positioned<?> other) {
		return Integer.compare(this.position, other.position);
	}
	
	public int compareTo(int otherPosition) {
		return Integer.compare(this.position, otherPosition);
	}
	
	public boolean posEQ(Positioned<?> other) {
		return this.compareTo(other) == 0;
	}
	
	public boolean posEQ(int otherPosition) {
		return this.compareTo(otherPosition) == 0;
	}
	
	public boolean posNE(Positioned<?> other) {
		return this.compareTo(other) != 0;
	}
	
	public boolean posNE(int otherPosition) {
		return this.compareTo(otherPosition) != 0;
	}
	
	public boolean posLT(Positioned<?> other) {
		return this.compareTo(other) < 0;
	}
	
	public boolean posLT(int otherPosition) {
		return this.compareTo(otherPosition) < 0;
	}
	
	public boolean posLE(Positioned<?> other) {
		return this.compareTo(other) <= 0;
	}
	
	public boolean posLE(int otherPosition) {
		return this.compareTo(otherPosition) <= 0;
	}
	
	public boolean posGT(Positioned<?> other) {
		return this.compareTo(other) > 0;
	}
	
	public boolean posGT(int otherPosition) {
		return this.compareTo(otherPosition) > 0;
	}
	
	public boolean posGE(Positioned<?> other) {
		return this.compareTo(other) >= 0;
	}
	
	public boolean posGE(int otherPosition) {
		return this.compareTo(otherPosition) >= 0;
	}

	public boolean posBetween(Positioned<?> low, Positioned<?> high) {
		return this.compareTo(low) >= 0 && this.compareTo(high) < 0;
	}
	
	public boolean posBetween(int low, int high) {
		return this.compareTo(low) >= 0 && this.compareTo(high) < 0;
	}
	
	public static Predicate<Positioned<?>> beingEQ(Positioned<?> other) {
		return positioned -> positioned.posEQ(other);
	}

	public static Predicate<Positioned<?>> beingEQ(int otherPos) {
		return positioned -> positioned.posEQ(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingNE(Positioned<?> other) {
		return positioned -> positioned.posNE(other);
	}

	public static Predicate<Positioned<?>> beingNE(int otherPos) {
		return positioned -> positioned.posNE(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingLT(Positioned<?> other) {
		return positioned -> positioned.posLT(other);
	}

	public static Predicate<Positioned<?>> beingLT(int otherPos) {
		return positioned -> positioned.posLT(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingLE(Positioned<?> other) {
		return positioned -> positioned.posLE(other);
	}

	public static Predicate<Positioned<?>> beingLE(int otherPos) {
		return positioned -> positioned.posLE(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingGT(Positioned<?> other) {
		return positioned -> positioned.posGT(other);
	}

	public static Predicate<Positioned<?>> beingGT(int otherPos) {
		return positioned -> positioned.posGT(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingGE(Positioned<?> other) {
		return positioned -> positioned.posGE(other);
	}

	public static Predicate<Positioned<?>> beingGE(int otherPos) {
		return positioned -> positioned.posGE(otherPos);
	}
	
	public static Predicate<Positioned<?>> beingBetween(Positioned<?> low, Positioned<?> high) {
		return positioned -> positioned.posBetween(low, high);
	}

	public static Predicate<Positioned<?>> beingBetween(int low, int high) {
		return positioned -> positioned.posBetween(low, high);
	}	
}
