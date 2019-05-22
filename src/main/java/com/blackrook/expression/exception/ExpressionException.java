/*******************************************************************************
 * Copyright (c) 2017-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.exception;

/**
 * Exception that can be thrown during expression evaluation.
 * @author Matthew Tropiano
 */
public class ExpressionException extends RuntimeException
{
	private static final long serialVersionUID = -2032491701653096911L;

	public ExpressionException()
	{
		super();
	}

	public ExpressionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ExpressionException(String message) 
	{
		super(message);
	}

	public ExpressionException(String message, Object ... args) 
	{
		super(String.format(message, args));
	}

	public ExpressionException(Throwable cause)
	{
		super(cause);
	}

}
