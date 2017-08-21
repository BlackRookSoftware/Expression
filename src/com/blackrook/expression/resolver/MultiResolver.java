/*******************************************************************************
 * Copyright (c) 2017 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.resolver;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.expression.ExpressionFunctionResolver;
import com.blackrook.expression.node.ExpressionFunctionType;

/**
 * A resolver that is a combination of multiple resolvers.
 * @author Matthew Tropiano
 */
public class MultiResolver implements ExpressionFunctionResolver
{
	private Queue<ExpressionFunctionResolver> resolvers;

	/**
	 * Creates a new MultiResolver using a list of resolvers.
	 * @param resolvers the list of resolvers.
	 */
	public MultiResolver(ExpressionFunctionResolver ... resolvers)
	{
		this.resolvers = new Queue<>();
		for (ExpressionFunctionResolver r : resolvers)
			this.resolvers.add(r);
	}
	
	@Override
	public boolean containsFunctionByName(String name)
	{
		for (ExpressionFunctionResolver r : resolvers)
			if (r.containsFunctionByName(name))
				return true;
		return false;
	}

	@Override
	public ExpressionFunctionType getFunctionByName(String name)
	{
		for (ExpressionFunctionResolver r : resolvers)
			if (r.containsFunctionByName(name))
				return r.getFunctionByName(name);
		return null;
	}
	
}
