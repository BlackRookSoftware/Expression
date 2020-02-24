/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression.functions;

import com.blackrook.expression.ExpressionFunctionResolver;
import com.blackrook.expression.ExpressionStack;
import com.blackrook.expression.ExpressionValue;
import com.blackrook.expression.ExpressionVariableContext;
import com.blackrook.expression.node.ExpressionFunctionType;
import com.blackrook.expression.resolver.EnumResolver;
import com.blackrook.expression.struct.Utils;

/**
 * Expression functions.
 * @author Matthew Tropiano
 */
public enum CommonFunctions implements ExpressionFunctionType
{
	/**
	 * Minimum.
	 */
	MIN(2)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg2 = stack.pop();
			ExpressionValue arg1 = stack.pop();
			if (arg1.asDouble() < arg2.asDouble())
				stack.push(arg1);
			return true;
		}
	},
		
	/**
	 * Maximum.
	 */
	MAX(2)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg2 = stack.pop();
			ExpressionValue arg1 = stack.pop();
			if (arg1.asDouble() < arg2.asDouble())
				stack.push(arg2);
			return true;
		}
	},
	
	/**
	 * Sine.
	 */
	SIN(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg1 = stack.pop();
			stack.push(Math.sin(arg1.asDouble()));
			return true;
		}
	},
	
	/**
	 * Cosine.
	 */
	COS(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg1 = stack.pop();
			stack.push(Math.cos(arg1.asDouble()));
			return true;
		}
	},
	
	/**
	 * Clamp.
	 */
	CLAMP(3)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg3 = stack.pop();
			ExpressionValue arg2 = stack.pop();
			ExpressionValue arg1 = stack.pop();
			stack.push(Utils.clampValue(arg1.asDouble(), arg2.asDouble(), arg3.asDouble()));
			return true;
		}
	},
	
	/**
	 * Wrap.
	 */
	WRAP(3)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg3 = stack.pop();
			ExpressionValue arg2 = stack.pop();
			ExpressionValue arg1 = stack.pop();
			stack.push(Utils.wrapValue(arg1.asDouble(), arg2.asDouble(), arg3.asDouble()));
			return true;
		}
	},
	
	/**
	 * Linear-interpolate.
	 */
	LERP(3)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg3 = stack.pop();
			ExpressionValue arg2 = stack.pop();
			ExpressionValue arg1 = stack.pop();
			stack.push(Utils.linearInterpolate(arg1.asDouble(), arg2.asDouble(), arg3.asDouble()));
			return true;
		}
	},
	
	/**
	 * Convert to boolean.
	 */
	BOOL(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg1 = stack.pop();
			stack.push(arg1.asBoolean());
			return true;
		}
	},
	
	/**
	 * Convert to integer (long, internally).
	 */
	INT(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg1 = stack.pop();
			stack.push(arg1.asLong());
			return true;
		}
	},
	
	/**
	 * Convert to floating point (double, internally).
	 */
	FLOAT(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg1 = stack.pop();
			stack.push(arg1.asDouble());
			return true;
		}
	},
	
	/**
	 * Color (byte) components to ARGB.
	 */
	COLOR(4)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue alpha = stack.pop();
			ExpressionValue blue = stack.pop();
			ExpressionValue green = stack.pop();
			ExpressionValue red = stack.pop();
			long argb = alpha.asByte() << 24 
					| red.asByte() << 16 
					| green.asByte() << 8 
					| blue.asByte();
			stack.push(argb);
			return true;
		}
	},
	
	/**
	 * Color (float) components to ARGB.
	 */
	COLORF(4)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue alpha = stack.pop();
			ExpressionValue blue = stack.pop();
			ExpressionValue green = stack.pop();
			ExpressionValue red = stack.pop();
			long argb = ((int)(alpha.asDouble() * 255.0) & 0x0ff) << 24 
					| ((int)(red.asDouble() * 255.0) & 0x0ff) << 16 
					| ((int)(green.asDouble() * 255.0) & 0x0ff) << 8 
					| ((int)(blue.asDouble() * 255.0) & 0x0ff);
			stack.push(argb);
			return true;
		}
	},
	
	/**
	 * Degrees to radians.
	 */
	DEG2RAD(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg = stack.pop();
			stack.push(Utils.degToRad(arg.asDouble()));
			return true;
		}
	},
	
	/**
	 * Radians to degrees.
	 */
	RAD2DEG(1)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			ExpressionValue arg = stack.pop();
			stack.push(Utils.radToDeg(arg.asDouble()));
			return true;
		}
	},
	
	/**
	 * Returns PI.
	 */
	PI(0)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			stack.push(Math.PI);
			return true;
		}
	},
	
	/**
	 * Returns Euler's constant.
	 */
	E(0)
	{
		@Override
		public boolean execute(ExpressionStack stack, ExpressionVariableContext context)
		{
			stack.push(Math.E);
			return true;
		}
	},
	
	;
	
	private int argumentCount;
	private CommonFunctions(int argumentCount)
	{
		this.argumentCount = argumentCount;
	}
	
	/**
	 * @return the amount of arguments this takes.
	 */
	public int getArgumentCount()
	{
		return argumentCount;
	}
	
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
	 */
	public abstract boolean execute(ExpressionStack stack, ExpressionVariableContext context);

	/**
	 * @return a function resolver that handles all of the functions in this enum.
	 */
	public static final ExpressionFunctionResolver getResolver()
	{
		return new EnumResolver(CommonFunctions.values());
	}
	

}
