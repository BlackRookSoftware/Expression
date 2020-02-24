/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.exception;

/**
 * Exception that can be thrown during expression parsing.
 * @author Matthew Tropiano
 */
public class ExpressionParseException extends RuntimeException
{
	private static final long serialVersionUID = -6360137775756012622L;

	public ExpressionParseException()
	{
		super();
	}

	public ExpressionParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ExpressionParseException(String message) 
	{
		super(message);
	}

	public ExpressionParseException(String message, Object ... args) 
	{
		super(String.format(message, args));
	}

	public ExpressionParseException(Throwable cause)
	{
		super(cause);
	}


}
