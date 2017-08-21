/*******************************************************************************
 * Copyright (c) 2017 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

/**
 * Expression variable set.
 * @author Matthew Tropiano
 */
public interface ExpressionVariableSet
{
	/**
	 * Gets a corresponding expression value by name.
	 * @param name the name of the variable.
	 * @return the value or null if no variable.
	 */
	public ExpressionValue get(String name);

}
