/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.node;

import com.blackrook.expression.Expression;
import com.blackrook.expression.ExpressionStack;
import com.blackrook.expression.ExpressionValue;
import com.blackrook.expression.ExpressionVariableContext;
import com.blackrook.expression.exception.ExpressionException;

/**
 * Expression directive.
 * @author Matthew Tropiano
 */
public enum ExpressionDirectiveType
{
	/**
	 * Return value.
	 * No operand.
	 */
	RETURN
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			context.set(Expression.RETURN_VARIABLE, value);
			return false;
		}
	},
	
	/**
	 * PUSH value.
	 * Operand is Boolean, Double, or Long.
	 */
	PUSH
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			Cache cache = CACHE.get();
			if (operand instanceof Long)
				cache.tempValue.set((Long)operand);
			else if (operand instanceof Double)
				cache.tempValue.set((Double)operand);
			else if (operand instanceof Boolean)
				cache.tempValue.set((Boolean)operand);
			else
				cache.tempValue.set(false);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * PUSH variable.
	 * Operand is String.
	 */
	PUSH_VARIABLE
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			String name = String.valueOf(operand);
			ExpressionValue value;
			if ((value = context.get(name)) == null)
				stack.push(false);
			else
				stack.push(value);
			return true;
		}
		
		@Override
		public boolean isCollapsable()
		{
			return false;
		}

	},
	
	/**
	 * POP into variable.
	 * Operand is String.
	 */
	POP
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			String name = String.valueOf(operand);
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			context.set(name, value);
			return true;
		}
	},
	
	/**
	 * Bitwise NOT.
	 */
	NOT
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.logicalNot(value, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Negate.
	 */
	NEGATE
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.negate(value, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Absolute.
	 */
	ABSOLUTE
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.absolute(value, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Logical Not.
	 */
	LOGICAL_NOT
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value = stack.pop();
			if (value == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.logicalNot(value, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Add.
	 */
	ADD
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.add(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Subtract.
	 */
	SUBTRACT
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.subtract(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Multiply.
	 */
	MULTIPLY
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.multiply(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Divide.
	 */
	DIVIDE
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.divide(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Modulo.
	 */
	MODULO
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.modulo(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Bitwise And.
	 */
	AND
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.and(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Bitwise Or.
	 */
	OR
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.or(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Bitwise Xor.
	 */
	XOR
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.xor(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Logical And.
	 */
	LOGICAL_AND
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.logicalAnd(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Logical Or.
	 */
	LOGICAL_OR
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.logicalOr(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Left Bit Shift.
	 */
	LEFT_SHIFT
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.leftShift(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Right Bit Shift.
	 */
	RIGHT_SHIFT
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.rightShift(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Right Bit Shift Padded.
	 */
	RIGHT_SHIFT_PADDED
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.rightShiftPadded(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Less than.
	 */
	LESS
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.less(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Less than or equal.
	 */
	LESS_OR_EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.lessOrEqual(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Greater than.
	 */
	GREATER
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.greater(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Greater than or equal.
	 */
	GREATER_OR_EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.greaterOrEqual(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Equal.
	 */
	EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.equal(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Not Equal.
	 */
	NOT_EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.notEqual(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Strict Equal.
	 */
	STRICT_EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.strictEqual(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	/**
	 * Strict Not Equal.
	 */
	STRICT_NOT_EQUAL
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand)
		{
			ExpressionValue value2 = stack.pop();
			if (value2 == null)
				throw new ExpressionException("Stack underflow!");
			ExpressionValue value1 = stack.pop();
			if (value1 == null)
				throw new ExpressionException("Stack underflow!");
			Cache cache = CACHE.get();
			ExpressionValue.strictNotEqual(value1, value2, cache.tempValue);
			stack.push(cache.tempValue);
			return true;
		}
	},
	
	;
	
	/**
	 * Checks if this node type is collapsable.
	 * @return true if so, false if not.
	 */
	public boolean isCollapsable()
	{
		return true;
	}
	
	/**
	 * Executes this node.
	 * @param stack the stack to use.
	 * @param context the context for added variables.
	 * @param operand the operand, if any.
	 * @return if false, terminate the calculation, else if true, continue.
	 */
	public abstract boolean execute(ExpressionStack stack, ExpressionVariableContext context, Object operand);

	private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(()->new Cache());

	// Expression cache.
	private static class Cache
	{
		private ExpressionValue tempValue;
		
		public Cache()
		{
			this.tempValue = ExpressionValue.create(false);
		}
		
	}

}
