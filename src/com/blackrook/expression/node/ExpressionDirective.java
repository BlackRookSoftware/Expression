/*******************************************************************************
 * Copyright (c) 2017 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.node;

import java.io.IOException;
import java.io.OutputStream;

import com.blackrook.expression.ExpressionNode;
import com.blackrook.expression.ExpressionStack;
import com.blackrook.expression.ExpressionVariableContext;
import com.blackrook.io.SuperWriter;

/**
 * Single expression directive.
 * @author Matthew Tropiano
 */
public class ExpressionDirective implements ExpressionNode
{
	/** Directive type. */
	private ExpressionDirectiveType type;
	/** Operand. */
	private Object operand;
	
	// Private constructor.
	private ExpressionDirective(ExpressionDirectiveType type, Object operand)
	{
		this.type = type;
		this.operand = operand;
	}
	
	/**
	 * Creates a new Expression directive.
	 * @param type the directive type.
	 * @return a new expression directive.
	 */
	public static ExpressionDirective create(ExpressionDirectiveType type)
	{
		return new ExpressionDirective(type, null);
	}

	/**
	 * Creates a new Expression directive.
	 * @param type the directive type.
	 * @param operand the operand.
	 * @return a new expression directive.
	 */
	public static ExpressionDirective create(ExpressionDirectiveType type, boolean operand)
	{
		return new ExpressionDirective(type, operand);
	}
	
	/**
	 * Creates a new Expression directive.
	 * @param type the directive type.
	 * @param operand the operand.
	 * @return a new expression directive.
	 */
	public static ExpressionDirective create(ExpressionDirectiveType type, long operand)
	{
		return new ExpressionDirective(type, operand);
	}
	
	/**
	 * Creates a new Expression directive.
	 * @param type the directive type.
	 * @param operand the operand.
	 * @return a new expression directive.
	 */
	public static ExpressionDirective create(ExpressionDirectiveType type, double operand)
	{
		return new ExpressionDirective(type, operand);
	}
	
	/**
	 * Creates a new Expression directive.
	 * @param type the directive type.
	 * @param operand the operand.
	 * @return a new expression directive.
	 */
	public static ExpressionDirective create(ExpressionDirectiveType type, String operand)
	{
		return new ExpressionDirective(type, operand);
	}
	
	public ExpressionDirectiveType getType()
	{
		return type;
	}
	
	public Object getOperand()
	{
		return operand;
	}
	
	@Override
	public boolean isCollapsable()
	{
		return type.isCollapsable();
	}

	@Override
	public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
	{
		return type.execute(stack, context, operand);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeVariableLengthInt(type.ordinal());
		sw.writeBoolean(operand != null);
		if (operand != null)
		{
			if (operand instanceof Long)
				sw.writeLong((Long)operand);
			else if (operand instanceof Double)
				sw.writeDouble((Double)operand);
			else if (operand instanceof Boolean)
				sw.writeBoolean((Boolean)operand);
		}
	}

}
