/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import java.io.IOException;
import java.io.OutputStream;

import com.blackrook.expression.struct.SerialWriter;

/**
 * Expression value encapsulation.
 * @author Matthew Tropiano
 */
public class ExpressionValue implements Comparable<ExpressionValue>
{
	public static enum Type
	{
		BOOLEAN,
		LONG,
		DOUBLE;
	}
	
	/** Value internal type. */
	private Type type;
	/** Value raw data. */
	private long rawbits;
	
	// Private constructor.
	private ExpressionValue(Type type, long rawbits)
	{
		this.type = type;
		this.rawbits = rawbits;
	}
	
	/**
	 * Creates an expression value.
	 * @param value the source value.
	 * @return a new expression value.
	 */
	public static ExpressionValue create(boolean value)
	{
		return new ExpressionValue(Type.BOOLEAN, value ? -1L : 0L);
	}

	/**
	 * Creates an expression value.
	 * @param value the source value.
	 * @return a new expression value.
	 */
	public static ExpressionValue create(long value)
	{
		return new ExpressionValue(Type.LONG, value);
	}
	
	/**
	 * Creates an expression value.
	 * @param value the source value.
	 * @return a new expression value.
	 */
	public static ExpressionValue create(double value)
	{
		return new ExpressionValue(Type.DOUBLE, Double.doubleToRawLongBits(value));
	}
	
	/**
	 * Creates an expression value.
	 * @param value the source value.
	 * @return a new expression value.
	 */
	public static ExpressionValue create(ExpressionValue value)
	{
		return new ExpressionValue(value.type, value.rawbits);
	}
	
	/**
	 * Sets this value using another value.
	 * @param value the source value to use.
	 */
	public void set(ExpressionValue value)
	{
		this.type = value.type;
		this.rawbits = value.rawbits;
	}
	
	/**
	 * Sets this value using another value.
	 * @param value the source value to use.
	 */
	public void set(boolean value)
	{
		type = Type.BOOLEAN;
		rawbits = value ? -1L : 0L;
	}
	
	/**
	 * Sets this value using another value.
	 * @param value the source value to use.
	 */
	public void set(long value)
	{
		type = Type.LONG; 
		rawbits = value;
	}
	
	/**
	 * Sets this value using another value.
	 * @param value the source value to use.
	 */
	public void set(double value)
	{
		type = Type.DOUBLE; 
		rawbits = Double.doubleToRawLongBits(value);
	}
	
	/**
	 * @return true if this value is NaN.
	 */
	public boolean isNaN()
	{
		return type == Type.DOUBLE && Double.isNaN(Double.longBitsToDouble(rawbits));
	}
	
	/**
	 * @return true if this value is positive or negative infinity.
	 */
	public boolean isInfinite()
	{
		return type == Type.DOUBLE && Double.isInfinite(Double.longBitsToDouble(rawbits));
	}
	
	/**
	 * Gets this value as a boolean.
	 * @return true if the value is nonzero and not NaN, false otherwise.
	 */
	public boolean asBoolean()
	{
		switch (type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
			case LONG:
				return rawbits != 0L; 
			case DOUBLE:
				return !isNaN() && Double.longBitsToDouble(rawbits) != 0.0; 
		}
	}
	
	/**
	 * Gets this value as a double-precision float.
	 * If this is a boolean type, this returns <code>1.0</code>.
	 * If this is a long type, this is cast to a double.
	 * @return the double value of this value.
	 */
	public double asDouble()
	{
		switch (type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				return asBoolean() ? 1.0 : 0.0; 
			case LONG:
				return (double)rawbits; 
			case DOUBLE:
				return Double.longBitsToDouble(rawbits); 
		}
	}
	
	/**
	 * Gets this value as a long integer.
	 * If this is a boolean type, this return <code>-1L</code>.
	 * If this is a double type, this is cast to a long.
	 * @return the long value of this value.
	 */
	public long asLong()
	{
		switch (type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				return asBoolean() ? -1L : 0L; 
			case LONG:
				return rawbits; 
			case DOUBLE:
				return (long)asDouble(); 
		}
	}
	
	/**
	 * Gets this value as a byte.
	 * Depending on the internal value, this may end up truncating data.
	 * <pre>(byte)asLong()</pre>
	 * @return the byte value of this value.
	 */
	public byte asByte()
	{
		return (byte)asLong();
	}
	
	/**
	 * Gets this value as a short.
	 * Depending on the internal value, this may end up truncating data.
	 * <pre>(short)asLong()</pre>
	 * @return the byte value of this value.
	 */
	public short asShort()
	{
		return (short)asLong();
	}
	
	/**
	 * Gets this value as an integer.
	 * Depending on the internal value, this may end up truncating data.
	 * <pre>(int)asLong()</pre>
	 * @return the byte value of this value.
	 */
	public int asInt()
	{
		return (int)asLong();
	}
	
	/**
	 * Gets this value as a short.
	 * Depending on the internal value, this may end up truncating data.
	 * <pre>isNaN() ? Float.NaN : (float)asDouble()</pre>
	 * @return the byte value of this value.
	 */
	public float asFloat()
	{
		return isNaN() ? Float.NaN : (float)asDouble();
	}
	
	/**
	 * Gets this value as a string.
	 * @return the string value of this value.
	 */
	public String asString()
	{
		switch (type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				return String.valueOf(asBoolean()); 
			case LONG:
				return String.valueOf(asLong()); 
			case DOUBLE:
				return String.valueOf(asDouble()); 
		}
	}
	
	/**
	 * Converts this value to another value.
	 * @param newType the new type to convert to.
	 * @throws IllegalArgumentException if newType is null.
	 */
	public void convertTo(Type newType)
	{
		switch (type)
		{
			case BOOLEAN:
			{
				switch (newType)
				{
					default:
						throw new IllegalArgumentException("bad type.");
					case BOOLEAN:
						return;
					case LONG:
						rawbits = asLong();
						type = newType;
						return;
					case DOUBLE:
						rawbits = Double.doubleToRawLongBits(asBoolean() ? 1.0 : 0.0);
						type = newType;
						return;
				}
			}
			
			case LONG:
			{
				switch (newType)
				{
					default:
						throw new IllegalArgumentException("bad type.");
					case BOOLEAN:
						rawbits = asBoolean() ? -1L : 0L;
						type = newType;
						return;
					case LONG:
						return;
					case DOUBLE:
						rawbits = Double.doubleToRawLongBits(asDouble());
						type = newType;
						return;
				}
			}
			
			case DOUBLE:
			{
				switch (newType)
				{
					default:
						throw new IllegalArgumentException("bad type.");
					case BOOLEAN:
						rawbits = asBoolean() ? -1L : 0L;
						type = newType;
						return;
					case LONG:
						rawbits = Double.doubleToRawLongBits(asDouble());
						type = newType;
						return;
					case DOUBLE:
						return;
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ExpressionValue)
			this.equals((ExpressionValue)obj);
		return super.equals(obj);
	}
	
	/**
	 * @param value the other value.
	 * @return true if this value is STRICTLY EQUAL to another.
	 */
	public boolean equals(ExpressionValue value)
	{
		return this.type == value.type && this.rawbits == value.rawbits;
	}
	
	@Override
	public int compareTo(ExpressionValue o)
	{
		double d1 = asDouble();
		double d2 = o.asDouble();
		return d1 == d2 ? 0 : (d1 < d2 ? -1 : 1);
	}

	/**
	 * @return string representation of this value suitable for debugging.
	 */
	@Override
	public String toString()
	{
		return String.format("%s: %s 0x%016x", asString(), type.name(), rawbits); 
	}
	
	/**
	 * Bitwise not calculation.
	 * @param operand the input value.
	 * @param out the output value.
	 */
	public static void not(ExpressionValue operand, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(!operand.asBoolean());
				return;
			case LONG:
				out.set(~operand.asLong());
				return;
			case DOUBLE:
				out.type = Type.DOUBLE;
				out.rawbits = ~operand.rawbits;
				return;
		}
	}
	
	/**
	 * Negate calculation.
	 * @param operand the input value.
	 * @param out the output value.
	 */
	public static void negate(ExpressionValue operand, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(!operand.asBoolean());
				return;
			case LONG:
				out.set(-operand.asLong());
				return;
			case DOUBLE:
				out.set(-operand.asDouble());
				return;
		}
	}
	
	/**
	 * Absolute calculation.
	 * @param operand the input value.
	 * @param out the output value.
	 */
	public static void absolute(ExpressionValue operand, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(operand.asBoolean());
				return;
			case LONG:
				out.set(Math.abs(operand.asLong()));
				return;
			case DOUBLE:
				out.set(Math.abs(operand.asDouble()));
				return;
		}
	}
	
	/**
	 * Logical not calculation.
	 * @param operand the input value.
	 * @param out the output value.
	 */
	public static void logicalNot(ExpressionValue operand, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(!operand.asBoolean());
				return;
			case LONG:
				out.set(operand.asLong() != 0 ? 0 : -1);
				return;
			case DOUBLE:
				if (operand.isNaN())
					out.set(1.0);
				else
					out.set(operand.asDouble() != 0.0 ? 0.0 : 1.0);
				return;
		}
	}
	
	/**
	 * Add calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void add(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean() || cacheValue.value2.asBoolean());
				return;
			case LONG:
				out.set(cacheValue.value1.asLong() + cacheValue.value2.asLong());
				return;
			case DOUBLE:
				out.set(cacheValue.value1.asDouble() + cacheValue.value2.asDouble());
				return;
		}
	}
	
	/**
	 * Subtract calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void subtract(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				boolean v1 = cacheValue.value1.asBoolean();
				out.set(!v1 ? false : (cacheValue.value2.asBoolean() ? false : v1));
				return;
			case LONG:
				out.set(cacheValue.value1.asLong() - cacheValue.value2.asLong());
				return;
			case DOUBLE:
				out.set(cacheValue.value1.asDouble() - cacheValue.value2.asDouble());
				return;
		}
	}

	/**
	 * Multiply calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void multiply(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean() && cacheValue.value2.asBoolean());
				return;
			case LONG:
				out.set(cacheValue.value1.asLong() * cacheValue.value2.asLong());
				return;
			case DOUBLE:
				out.set(cacheValue.value1.asDouble() * cacheValue.value2.asDouble());
				return;
		}
	}

	/**
	 * Divide calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void divide(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean());
				return;
			case LONG:
				long dividend = cacheValue.value2.asLong();
				if (dividend != 0)
					out.set(cacheValue.value1.asLong() / dividend);
				else
					out.set(Double.NaN);
				return;
			case DOUBLE:
				out.set(cacheValue.value1.asDouble() / cacheValue.value2.asDouble());
				return;
		}
	}

	/**
	 * Modulo calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void modulo(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean());
				return;
			case LONG:
				long dividend = cacheValue.value2.asLong();
				if (dividend != 0)
					out.set(cacheValue.value1.asLong() % dividend);
				else
					out.set(Double.NaN);
				return;
			case DOUBLE:
				out.set(cacheValue.value1.asDouble() % cacheValue.value2.asDouble());
				return;
		}
	}

	/**
	 * Bitwise And calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void and(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean() && cacheValue.value2.asBoolean());
				return;
			case LONG:
			case DOUBLE:
				out.set(cacheValue.value1.rawbits & cacheValue.value2.rawbits);
				return;
		}
	}

	/**
	 * Bitwise Or calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void or(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean() || cacheValue.value2.asBoolean());
				return;
			case LONG:
			case DOUBLE:
				out.set(cacheValue.value1.rawbits | cacheValue.value2.rawbits);
				return;
		}
	}

	/**
	 * Bitwise XOr calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void xor(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		Cache cacheValue = CACHE.get();
		cacheValue.value1.set(operand);
		cacheValue.value2.set(operand2);
		if (operand.type.ordinal() < operand2.type.ordinal())
			cacheValue.value1.convertTo(operand2.type);
		else if (operand.type.ordinal() > operand2.type.ordinal())
			cacheValue.value2.convertTo(operand.type);
		
		switch (cacheValue.value2.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(cacheValue.value1.asBoolean() ^ cacheValue.value2.asBoolean());
				return;
			case LONG:
			case DOUBLE:
				out.set(cacheValue.value1.rawbits ^ cacheValue.value2.rawbits);
				return;
		}
	}

	/**
	 * Logical And calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void logicalAnd(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asBoolean() && operand2.asBoolean());
	}

	/**
	 * Logical Or calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void logicalOr(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asBoolean() || operand2.asBoolean());
	}

	/**
	 * Left shift calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void leftShift(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(operand.asBoolean());
				return;
			case LONG:
				out.set(operand.asLong() << operand2.asLong());
				return;
			case DOUBLE:
				out.set(Double.longBitsToDouble(operand.rawbits << operand2.asLong()));
				return;
		}
	}

	/**
	 * Right shift calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void rightShift(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(operand.asBoolean());
				return;
			case LONG:
				out.set(operand.asLong() >> operand2.asLong());
				return;
			case DOUBLE:
				out.set(Double.longBitsToDouble(operand.rawbits >> operand2.asLong()));
				return;
		}
	}

	/**
	 * Right shift padded calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void rightShiftPadded(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		switch (operand.type)
		{
			default:
				throw new RuntimeException("Bad internal type.");
			case BOOLEAN:
				out.set(operand.asBoolean());
				return;
			case LONG:
				out.set(operand.asLong() >>> operand2.asLong());
				return;
			case DOUBLE:
				out.set(Double.longBitsToDouble(operand.rawbits >>> operand2.asLong()));
				return;
		}
	}

	/**
	 * Less-than calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void less(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() < operand2.asDouble());
	}

	/**
	 * Less-than-or-equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void lessOrEqual(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() <= operand2.asDouble());
	}

	/**
	 * Greater-than calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void greater(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() > operand2.asDouble());
	}

	/**
	 * Greater-than-or-equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void greaterOrEqual(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() >= operand2.asDouble());
	}

	/**
	 * Logical Equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void equal(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() == operand2.asDouble());
	}

	/**
	 * Logical Not Equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void notEqual(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.asDouble() != operand2.asDouble());
	}

	/**
	 * Strict Equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void strictEqual(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(operand.equals(operand2));
	}

	/**
	 * Strict Not Equal calculation.
	 * @param operand the source operand.
	 * @param operand2 the second operand. 
	 * @param out the output value.
	 */
	public static void strictNotEqual(ExpressionValue operand, ExpressionValue operand2, ExpressionValue out)
	{
		out.set(!operand.equals(operand2));
	}

	/**
	 * Write value as bytes (for digest later).
	 * @param out the output stream.
	 * @throws IOException if a write error occurs.
	 */
	public void writeBytes(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeVariableLengthInt(out, type.ordinal());
		sw.writeLong(out, rawbits);
	}
	
	private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(()->new Cache());

	// Mathematics cache.
	private static class Cache
	{
		private ExpressionValue value1;
		private ExpressionValue value2;
		
		public Cache()
		{
			this.value1 = ExpressionValue.create(false);
			this.value2 = ExpressionValue.create(false);
		}
		
	}
	
}
