/*******************************************************************************
 * Copyright (c) 2017-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.node;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.blackrook.expression.ExpressionNode;
import com.blackrook.expression.ExpressionStack;
import com.blackrook.expression.ExpressionValue;
import com.blackrook.expression.ExpressionVariableContext;
import com.blackrook.expression.util.Utils;

/**
 * Expression branch.
 * @author Matthew Tropiano
 */
public class ExpressionBranch implements ExpressionNode
{
	private static final ExpressionNode[] ALWAYS_TRUE_CONDITIONAL = {ExpressionDirective.create(ExpressionDirectiveType.PUSH, true)};
	
	/** Conditional block. */
	private ExpressionNode[] conditional;
	/** Success block. */
	private ExpressionNode[] successBlock;
	/** Failure block. */
	private ExpressionNode[] failureBlock;
	
	// Private constructor.
	private ExpressionBranch(ExpressionNode[] conditional, ExpressionNode[] successBlock, ExpressionNode[] failureBlock)
	{
		this.conditional = conditional;
		this.successBlock = successBlock;
		this.failureBlock = failureBlock;
	}
	
	/**
	 * Creates a new expression branch - always succeed, one success block.
	 * @param successBlock the block to use on conditional success.
	 * @return a new expression branch node.
	 */
	public static ExpressionBranch create(ExpressionNode[] successBlock)
	{
		return new ExpressionBranch(ALWAYS_TRUE_CONDITIONAL, successBlock, null);
	}
	
	/**
	 * Creates a new expression branch, no failure block.
	 * @param conditional the conditional block. Must end in a directive that leaves a value on the stack.
	 * @param successBlock the block to use on conditional success.
	 * @return a new expression branch node.
	 */
	public static ExpressionBranch create(ExpressionNode[] conditional, ExpressionNode[] successBlock)
	{
		return new ExpressionBranch(conditional, successBlock, null);
	}
	
	/**
	 * Creates a new expression branch, no failure block.
	 * @param conditional the conditional block. Must end in a directive that leaves a value on the stack.
	 * @param successBlock the block to use on conditional success.
	 * @param failureBlock the block to use on conditional failure.
	 * @return a new expression branch node.
	 */
	public static ExpressionBranch create(ExpressionNode[] conditional, ExpressionNode[] successBlock, ExpressionNode[] failureBlock)
	{
		return new ExpressionBranch(conditional, successBlock, failureBlock);
	}
	
	@Override
	public boolean isCollapsable()
	{
		for (ExpressionNode node : conditional)
			if (!node.isCollapsable())
				return false;
		
		for (ExpressionNode node : successBlock)
			if (!node.isCollapsable())
				return false;
		
		if (!Utils.isEmpty(failureBlock)) 
		{
			for (ExpressionNode node : failureBlock)
				if (!node.isCollapsable())
					return false;
		}
		
		return true;
	}

	@Override
	public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
	{
		for (int i = 0; i < conditional.length; i++)
			if (!conditional[i].execute(stack, context))
				return false;
		ExpressionValue value = stack.pop();
		if (value.asBoolean())
		{
			for (int i = 0; i < successBlock.length; i++)
				if (!successBlock[i].execute(stack, context))
					return false;
		}
		else if (!Utils.isEmpty(failureBlock))
		{
			for (int i = 0; i < failureBlock.length; i++)
				if (!failureBlock[i].execute(stack, context))
					return false;
		}
		return true;
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (ExpressionNode node : conditional)
			node.writeBytes(out);
		for (ExpressionNode node : successBlock)
			node.writeBytes(out);
		if (!Utils.isEmpty(failureBlock)) for (ExpressionNode node : failureBlock)
			node.writeBytes(out);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("COND: ").append(Arrays.toString(conditional));
		sb.append("SUCCESS: ").append(Arrays.toString(conditional));
		if (failureBlock != null)
			sb.append("FAILURE: ").append(Arrays.toString(conditional));
		return sb.toString();
	}

}
