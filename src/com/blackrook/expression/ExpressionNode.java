/*******************************************************************************
 * Copyright (c) 2017-2018 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A single node in a compiled expression.
 * @author Matthew Tropiano
 */
public interface ExpressionNode
{
	/**
	 * Checks if this node is collapsable.
	 * @return true if so, false if not.
	 */
	public boolean isCollapsable();
	
	/**
	 * Executes this node.
	 * @param stack the stack to use.
	 * @param context the context for added variables.
	 * @return if false, stop expression calculation, else if true, continue.
	 */
	public boolean execute(ExpressionStack stack, ExpressionVariableContext context);
	
	/**
	 * Write value as bytes (for digest later).
	 * @param out the output stream.
	 * @throws IOException on a write error.
	 */
	public void writeBytes(OutputStream out) throws IOException;

}
