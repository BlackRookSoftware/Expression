/*******************************************************************************
 * Copyright (c) 2017-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.node;

import com.blackrook.expression.ExpressionStack;
import com.blackrook.expression.ExpressionVariableContext;

/**
 * Describes a type of function entry point.
 * @author Matthew Tropiano
 */
public interface ExpressionFunctionType
{
	/**
	 * Gets the name of this function.
	 * This name returned must be a valid name that can be parsed in the script ("identifier" type: starts with letter, alphanumeric plus "_").
	 * @return the function name.
	 */
	public String name();
	
	/**
	 * @return the amount of arguments this takes.
	 */
	public int getArgumentCount();
	
	/**
	 * Checks if this node type is collapsable.
	 * "Collapsible" functions should be completely deterministic and stable (returns the same things given the same input).
	 * This aids in optimizing scripts.
	 * @return true if so, false if not.
	 */
	public boolean isCollapsable();

	/**
	 * Executes this function.
	 * @param stack the stack to use.
	 * @param context the context for added variables.
	 * @return if false, this halts script execution, else if true, continue.
	 */
	public boolean execute(ExpressionStack stack, ExpressionVariableContext context);

}
