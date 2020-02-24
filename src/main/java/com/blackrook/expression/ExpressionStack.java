/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

/**
 * A calculation stack for expression calculation.
 * @author Matthew Tropiano
 */
public class ExpressionStack
{
	/** Default capacity. */
	public static final int DEFAULT_CAPACITY = 8;
	
	/** List of entries. */
	private ExpressionValue[] stack;
	/** Position. */
	private int count;

	/**
	 * Creates a stack with a default size.
	 * @see #DEFAULT_CAPACITY
	 */
	public ExpressionStack()
	{
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Creates a context with a default size.
	 * @param capacity the initial capacity.
	 */
	public ExpressionStack(int capacity)
	{
		if (capacity < 1) 
			capacity = 1;
		expand(capacity);
		this.count = 0;
	}
	
	// Expands this.
	private void expand(int capacity)
	{
		ExpressionValue[] newStack = new ExpressionValue[capacity];
		if (stack != null)
			System.arraycopy(stack, 0, newStack, 0, stack.length);
		for (int i = stack != null ? stack.length : 0; i < newStack.length; i++)
			newStack[i] = ExpressionValue.create(false);
		stack = newStack;
	}

	/**
	 * Pushes a value onto the stack.
	 * @param value the value to push.
	 */
	public void push(boolean value)
	{
		checkExpandPush();
		stack[count++].set(value);
	}

	/**
	 * Pushes a value onto the stack.
	 * @param value the value to push.
	 */
	public void push(long value)
	{
		checkExpandPush();
		stack[count++].set(value);
	}

	/**
	 * Pushes a value onto the stack.
	 * @param value the value to push.
	 */
	public void push(double value)
	{
		checkExpandPush();
		stack[count++].set(value);
	}

	/**
	 * Pushes a value onto the stack.
	 * @param value the value to push.
	 */
	public void push(ExpressionValue value)
	{
		checkExpandPush();
		stack[count++].set(value);
	}

	private void checkExpandPush()
	{
		if (count == stack.length - 1)
			expand(stack.length * 2);
	}
	
	/**
	 * Pops a value off the stack.
	 * @return the value at the top of the stack, or null if none left.
	 */
	public ExpressionValue pop()
	{
		if (count == 0)
			return null;
		else
			return stack[--count];
	}
	
	/**
	 * Peeks at a value on the top of the stack.
	 * @return the value at the top of the stack, or null if none left.
	 */
	public ExpressionValue peek()
	{
		if (count == 0)
			return null;
		else
			return stack[count-1];
	}
	
	/**
	 * Clears the context.
	 */
	public void clear()
	{
		this.count = 0;
	}

	/**
	 * @return the stack size.
	 */
	public int size()
	{
		return count;
	}

	/**
	 * @return true if the stack is empty, false if not.
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int i = 0;
		while (i < count)
		{
			sb.append(stack[i]);
			if (i < count - 1)
				sb.append(", ");
			i++;
		}
		sb.append(']');
		return sb.toString();
	}
	

}
