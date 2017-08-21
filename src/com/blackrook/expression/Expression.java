/*******************************************************************************
 * Copyright (c) 2017 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.expression.exception.ExpressionException;
import com.blackrook.expression.node.ExpressionBranch;

/**
 * An expression object for evaluating dynamic calculations.
 * Expressions, at best, should do no memory allocations during evaluation - they can be called multiple times per gametic.
 * @author Matthew Tropiano
 */
public class Expression
{
	/** Return variable name. */
	public static final String RETURN_VARIABLE = "-0. Return .0-";

	/** Map of internalized expressions. */
	private static final HashMap<String, Expression> EXPRESSION_INTERN_MAP;

	/** Expression: literal true. */
	public static final Expression TRUE;
	/** Expression: literal false. */
	public static final Expression FALSE;
	/** Expression: literal 0.0. */
	public static final Expression FLOAT_0;
	/** Expression: literal 1.0. */
	public static final Expression FLOAT_1;
	/** Expression: literal 0. */
	public static final Expression INTEGER_0;
	/** Expression: literal 1. */
	public static final Expression INTEGER_1;
	/** Expression: Infinity. */
	public static final Expression INFINITY;
	/** Expression: NaN. */
	public static final Expression NAN;

	static
	{
		EXPRESSION_INTERN_MAP = new HashMap<>(32);
		TRUE = Expression.create(true).intern();
		FALSE = Expression.create(false).intern();
		FLOAT_0 = Expression.create(0.0).intern();
		FLOAT_1 = Expression.create(1.0).intern();
		INTEGER_0 = Expression.create(0).intern();
		INTEGER_1 = Expression.create(1).intern();
		INFINITY = Expression.create(Double.POSITIVE_INFINITY).intern();
		NAN = Expression.create(Double.NaN).intern();
	}
	
	/** Expression digest - used to find duplicate system expressions. */
	private String digest;
	
	// Of the following, only one of the two will be null.
	
	/** Expression is a single value. */
	private ExpressionValue value;
	/** Expression is a set of nodes. */
	private ExpressionNode nodes;
	/** Expression source if parsed. */
	private String source;
	
	// Private constructor.
	private Expression(ExpressionValue value, ExpressionBranch nodes)
	{
		this.value = value;
		this.nodes = nodes;
		this.source = null;
	}
	
	/**
	 * Creates an expression that is just one value.
	 * @param value the value.
	 * @return a new expression.
	 */
	public static Expression create(boolean value)
	{
		return new Expression(ExpressionValue.create(value), null);
	}
	
	/**
	 * Creates an expression that is just one value.
	 * @param value the value.
	 * @return a new expression.
	 */
	public static Expression create(long value)
	{
		return new Expression(ExpressionValue.create(value), null);
	}
	
	/**
	 * Creates an expression that is just one value.
	 * @param value the value.
	 * @return a new expression.
	 */
	public static Expression create(double value)
	{
		return new Expression(ExpressionValue.create(value), null);
	}
	
	/**
	 * Creates an expression that is just one value.
	 * @param value the value.
	 * @return a new expression.
	 */
	public static Expression create(ExpressionValue value)
	{
		return new Expression(ExpressionValue.create(value), null);
	}
	
	/**
	 * Creates an expression that is a full expression.
	 * @param nodes the list of expression nodes.
	 * @return a new expression.
	 */
	public static Expression create(ExpressionNode[] nodes)
	{
		return new Expression(null, ExpressionBranch.create(nodes));
	}

	/**
	 * Evaluates this expression.
	 * @param stack the expression stack to use.
	 * @param context the mutable variable context to use.
	 * @param out the output value (returned value, top of stack, or literal value encapsulated).
	 */
	public void evaluate(ExpressionStack stack, ExpressionVariableContext context, ExpressionValue out)
	{
		if (value != null)
		{
			out.set(value);
			return;
		}
		
		nodes.execute(stack, context);
		ExpressionValue value;
		if ((value = context.get(RETURN_VARIABLE)) != null)
			out.set(value);
		else if (stack.isEmpty())
			out.set(false);
		else
			out.set(stack.peek());
	}

	/**
	 * Evaluates this expression.
	 * Creates a new stack.
	 * @param context the mutable variable context to use.
	 * @param out the output value (returned value, top of stack, or literal value encapsulated).
	 */
	public void evaluate(ExpressionVariableContext context, ExpressionValue out)
	{
		if (value != null)
		{
			out.set(value);
			return;
		}
		evaluate(new ExpressionStack(), context, out);
	}
	
	/**
	 * Evaluates this expression.
	 * Creates a new stack, and an empty context.
	 * @param out the output value (returned value, top of stack, or literal value encapsulated).
	 */
	public void evaluate(ExpressionValue out)
	{
		if (value != null)
		{
			out.set(value);
			return;
		}
		evaluate(new ExpressionStack(), new ExpressionVariableContext(), out);
	}

	/**
	 * Evaluates this expression.
	 * Creates a new stack, an empty context, and a value to put the result in.
	 * @return the output value (returned value, top of stack, or literal value encapsulated).
	 */
	public ExpressionValue evaluate()
	{
		ExpressionValue out = ExpressionValue.create(false);
		if (value != null)
		{
			out.set(value);
			return out;
		}
		evaluate(new ExpressionStack(), new ExpressionVariableContext(), out);
		return out;
	}
	
	/**
	 * Collapses this expression.
	 * @see #evaluate(ExpressionValue)
	 * @see #isCollapsible()
	 */
	public void collapse()
	{
		ExpressionValue out = ExpressionValue.create(false);
		evaluate(out);
		this.value = out;
		this.nodes = null;
		if (digest != null)
			getDigest();
	}

	/**
	 * Checks if this expression is collapsable.
	 * @return true if so, false if not.
	 */
	public boolean isCollapsible()
	{
		// already collapsed? Return true anyway. Why not?
		if (value != null)
			return true;
		
		return nodes.isCollapsable();
	}
	
	/**
	 * Checks if this is just a literal value (not complex).
	 * @return true if so, false if not.
	 */
	public boolean isValue()
	{
		return value != null;
	}

	/**
	 * Sets the source code.
	 * @param source
	 */
	void setSource(String source)
	{
		this.source = source;
	}
	
	/**
	 * Gets the source code that was used to create this expression.
	 * @return the source or null if not created from a string.
	 */
	public String getSource()
	{
		return source;
	}
	
	/**
	 * Gets the SHA-1 digest of this expression, in order to find duplicates.
	 * Within the Universe of all expressions, this digest should be sufficient enough
	 * to find equal expressions.
	 * Under the covers, this calculates the digest if it hasn't been calculated, yet.
	 * If it has, it returns the previously calculated digest.
	 * @return the SHA-1 digest.
	 */
	public String getDigest()
	{
		return getDigest(false);
	}
	
	/**
	 * Gets the SHA1 digest of this expression, in order to find duplicates.
	 * @param forceRecalc if true, forces a recalculation before return.
	 * @return a byte array of the digest.
	 * @throws ExpressionException if the digest could not be calculated for some reason.
	 */
	public String getDigest(boolean forceRecalc)
	{
		if (!forceRecalc && digest != null)
			return digest;
		
		synchronized (this)
		{
			if (digest != null)
				return digest;
			else
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					if (value != null)
						value.writeBytes(bos);
					else
						nodes.writeBytes(bos);
				} catch (IOException e) {
					throw new ExpressionException("The digest could not be calculated!", e);
				}
				
				StringBuilder sb = new StringBuilder();
				for (byte b : Common.sha1(bos.toByteArray()))
					sb.append(String.format("%02x", b));
				
				return (digest = sb.toString());
			}
			
		}
		
	}
	
	/**
	 * Internalizes an expression: adds this expression to an internal bank and returns it, 
	 * or returns an existing reference for the same expression already stored.
	 * @return this expression, or a reference to an equal one. 
	 */
	public Expression intern()
	{
		String digest = getDigest();
		Expression out;
		if ((out = EXPRESSION_INTERN_MAP.get(digest)) != null)
			return out;
		EXPRESSION_INTERN_MAP.put(digest, out = this);
		return out;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Expression)
			return equals((Expression)obj);
		else
			return super.equals(obj);
	}
	
	/**
	 * Tests if two expressions are the same.
	 * Only checks the digests - SHA1 hashes should be good enough.
	 * @param other the other expression.
	 * @return true if so, false if not.
	 */
	public boolean equals(Expression other)
	{
		return this.getDigest().equals(other.getDigest());
	}
	
	@Override
	public String toString()
	{
		if (isValue())
			return value.asString();
		else if (source != null)
			return source;
		else
			return "digest:" + getDigest();  
	}
	
}
