/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.expression.struct;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Comparator;

public final class Utils
{

	/**
	 * Checks if a value is "empty."
	 * The following is considered "empty":
	 * <ul>
	 * <li><i>Null</i> references.
	 * <li>{@link Array} objects that have a length of 0.
	 * <li>{@link Boolean} objects that are false.
	 * <li>{@link Character} objects that are the null character ('\0', '\u0000').
	 * <li>{@link Number} objects that are zero.
	 * <li>{@link String} objects that are the empty string, or are {@link String#trim()}'ed down to the empty string.
	 * <li>{@link Collection} objects where {@link Collection#isEmpty()} returns true.
	 * </ul> 
	 * @param obj the object to check.
	 * @return true if the provided object is considered "empty", false otherwise.
	 */
	public static boolean isEmpty(Object obj)
	{
		if (obj == null)
			return true;
		else if (isArray(obj.getClass()))
			return Array.getLength(obj) == 0;
		else if (obj instanceof Boolean)
			return !((Boolean)obj);
		else if (obj instanceof Character)
			return ((Character)obj) == '\0';
		else if (obj instanceof Number)
			return ((Number)obj).doubleValue() == 0.0;
		else if (obj instanceof String)
			return ((String)obj).trim().length() == 0;
		else if (obj instanceof Collection<?>)
			return ((Collection<?>)obj).isEmpty();
		else
			return false;
	}

	private static boolean isArray(Class<?> clazz)
	{
		return clazz.getName().startsWith("["); 
	}

	/**
	 * Shifts an object to an appropriate position according to the object's {@link Comparable#compareTo(Object)} function.
	 * @param <T> the object type stored in the array that extends {@link Comparable}.
	 * @param array the array to shift the contents of.
	 * @param index the index to add it to (the contents are replaced).
	 * @return the final index in the array of the sorted object.
	 */
	public static <T extends Comparable<T>> int sortFrom(T[] array, int index)
	{
		while (index > 0 && array[index].compareTo(array[index - 1]) < 0)
		{
			arraySwap(array, index, index - 1);
			index--;
		}
		return index;
	}

	/**
	 * Shifts an object to an appropriate position according to the provided <code>comparator</code> function.
	 * @param <T> the object type stored in the arrays.
	 * @param array the array to shift the contents of.
	 * @param index the index to add it to (the contents are replaced).
	 * @param comparator the comparator to use.
	 * @return the final index in the array of the sorted object.
	 */
	public static <T> int sortFrom(T[] array, int index, Comparator<? super T> comparator)
	{
		while (index > 0 && comparator.compare(array[index], array[index - 1]) < 0)
		{
			arraySwap(array, index, index - 1);
			index--;
		}
		return index;
	}

	/**
	 * Swaps the contents of two indices of an array.
	 * @param <T> the object type stored in the array.
	 * @param array the input array.
	 * @param a the first index.
	 * @param b the second index.
	 */
	public static <T> void arraySwap(T[] array, int a, int b)
	{
		T temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	/**
	 * Returns a hash of a set of bytes digested by an encryption algorithm.
	 * Can return null if this Java implementation cannot perform this.
	 * Do not use this if you care if the algorithm is provided or not.
	 * @param bytes the bytes to encode.
	 * @param algorithmName the name to the algorithm to use.
	 * @return the resultant byte digest, or null if the algorithm is not supported.
	 */
	public static byte[] digest(byte[] bytes, String algorithmName)
	{
		try {
			return MessageDigest.getInstance(algorithmName).digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Returns a 20-byte SHA-1 hash of a set of bytes.
	 * Can return null if this Java implementation cannot perform this,
	 * but it shouldn't, since SHA-1 is mandatorily implemented for all implementations.
	 * @param bytes the input bytes.
	 * @return the resultant 20-byte digest.
	 * @see #digest(byte[], String)
	 */
	public static byte[] sha1(byte[] bytes)
	{
		return digest(bytes, "SHA-1");
	}

	/**
	 * Converts radians to degrees.
	 * @param radians the input angle in radians.
	 * @return the resultant angle in degrees.
	 */
	public static double radToDeg(double radians)
	{
		return radians * (180/Math.PI);
	}

	/**
	 * Converts degrees to radians.
	 * @param degrees the input angle in degrees.
	 * @return the resultant angle in radians.
	 */
	public static double degToRad(double degrees)
	{
		return (degrees * Math.PI)/180;
	}

	/**
	 * Coerces a double to the range bounded by lo and hi.
	 * <br>Example: clampValue(32,-16,16) returns 16.
	 * <br>Example: clampValue(4,-16,16) returns 4.
	 * <br>Example: clampValue(-1000,-16,16) returns -16.
	 * @param val the double.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "forced" into the range.
	 */
	public static double clampValue(double val, double lo, double hi)
	{
		return Math.min(Math.max(val,lo),hi);
	}

	/**
	 * Coerces a double to the range bounded by lo and hi, by "wrapping" the value.
	 * <br>Example: wrapValue(32,-16,16) returns 0.
	 * <br>Example: wrapValue(4,-16,16) returns 4.
	 * <br>Example: wrapValue(-1000,-16,16) returns 8.
	 * @param val the double.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "wrapped" into the range.
	 */
	public static double wrapValue(double val, double lo, double hi)
	{
		double range = hi - lo;
		val = val - lo;
		val = (val % range);
		if (val < 0.0)
			val = val + hi;
		return val;
	}

	/**
	 * Gives a value that is the result of a linear interpolation between two values.
	 * @param factor the interpolation factor.
	 * @param x the first value.
	 * @param y the second value.
	 * @return the interpolated value.
	 */
	public static double linearInterpolate(double factor, double x, double y)
	{
		return factor * (y - x) + x;
	}

}
