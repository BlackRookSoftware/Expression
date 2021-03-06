/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import com.blackrook.expression.node.ExpressionFunctionType;

/**
 * Resolver encapsulation for finding a relevant function by name.
 * @author Matthew Tropiano
 */
public interface ExpressionFunctionResolver
{

	/**
	 * Attempts to find if this resolver has a function by its calling name.
	 * <p>Names must be resolved case-insensitively!
	 * <p>If this returns <code>true</code> for a function name, {@link #getFunctionByName(String)} must NOT return null if called with that name!
	 * @param name the name to find.
	 * @return a corresponding function or null for no such function.
	 */
	public boolean containsFunctionByName(String name);

	/**
	 * Attempts to resolve a pertinent function by its calling name.
	 * <p>Names must be resolved case-insensitively!
	 * <p>If this returns a non-null for a function name, {@link #containsFunctionByName(String)} must NOT return <code>false</code> if called with that name!
	 * <p>The same function must be returned if this is called again with the same name!
	 * @param name the name to resolve.
	 * @return a corresponding function or null for no such function.
	 * @see #containsFunctionByName(String)
	 */
	public ExpressionFunctionType getFunctionByName(String name);
	
}
