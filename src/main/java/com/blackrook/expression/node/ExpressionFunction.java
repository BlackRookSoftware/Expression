/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
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
import com.blackrook.expression.struct.SerialWriter;

/**
 * Single expression function.
 * @author Matthew Tropiano
 */
public class ExpressionFunction implements ExpressionNode
{
	/** Function type. */
	private ExpressionFunctionType type;
	
	// Private constructor.
	private ExpressionFunction(ExpressionFunctionType type)
	{
		this.type = type;
	}
	
	/**
	 * Creates a new Expression function.
	 * @param type the function type.
	 * @return a new expression directive.
	 */
	public static ExpressionFunction create(ExpressionFunctionType type)
	{
		return new ExpressionFunction(type);
	}

	@Override
	public boolean isCollapsable()
	{
		return type.isCollapsable();
	}
	
	@Override
	public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
	{
		return type.execute(stack, context);
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		(new SerialWriter(SerialWriter.LITTLE_ENDIAN)).writeString(out, type.name());
	}

	@Override
	public String toString()
	{
		return type.name();
	}

}
