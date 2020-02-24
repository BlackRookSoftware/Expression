/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.resolver;

import java.util.HashMap;
import java.util.Map;

import com.blackrook.expression.ExpressionFunctionResolver;
import com.blackrook.expression.node.ExpressionFunctionType;

/**
 * A special kind of function resolver that wraps an {@link Enum} of {@link ExpressionFunctionType} 
 * @author Matthew Tropiano 
 */
public class EnumResolver implements ExpressionFunctionResolver
{
	private Map<String, ExpressionFunctionType> map;
	
	/**
	 * Creates a new resolver using a list of enum values.
	 * @param en the list of enum values (usually Enum.values()).
	 */
	@SafeVarargs
	public EnumResolver(Enum<? extends ExpressionFunctionType> ... en)
	{
		this.map = new HashMap<>(10, 1f);
		for (Enum<? extends ExpressionFunctionType> e : en)
			map.put(e.name(), (ExpressionFunctionType)e);
	}
	
	@Override
	public ExpressionFunctionType getFunctionByName(String name)
	{
		return map.get(name);
	}
	
	@Override
	public boolean containsFunctionByName(String name)
	{
		return map.containsKey(name);
	}

}
