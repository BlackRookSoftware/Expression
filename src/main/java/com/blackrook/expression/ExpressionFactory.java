/*******************************************************************************
 * Copyright (c) 2017-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;

import com.blackrook.expression.exception.ExpressionParseException;
import com.blackrook.expression.node.ExpressionBranch;
import com.blackrook.expression.node.ExpressionDirective;
import com.blackrook.expression.node.ExpressionDirectiveType;
import com.blackrook.expression.node.ExpressionFunction;
import com.blackrook.expression.node.ExpressionFunctionType;
import com.blackrook.expression.struct.Lexer;

/**
 * Creates expression objects from input text.
 * @author Matthew Tropiano
 */
public final class ExpressionFactory
{
	private static final ExpressionFunctionResolver EMPTY_RESOLVER = new ExpressionFunctionResolver()
	{
		@Override
		public ExpressionFunctionType getFunctionByName(String name)
		{
			return null;
		}
		
		@Override
		public boolean containsFunctionByName(String name)
		{
			return false;
		}
	};
	
	/**
	 * Parses a single-line expression.
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param inputString the input string to parse from.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpression(String inputString)
	{
		return parseExpression(inputString, EMPTY_RESOLVER);
	}

	/**
	 * Parses an expression block (multi-line/statement expression).
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param inputString the input string to parse from.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpressionBlock(String inputString)
	{
		return parseExpressionBlock(inputString, EMPTY_RESOLVER);
	}

	/**
	 * Parses a single-line expression.
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param inputString the input string to parse from.
	 * @param resolver the resolver object for resolving functions in the script.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpression(String inputString, ExpressionFunctionResolver resolver)
	{
		Expression e = (new EParser(new StringReader(inputString), resolver)).parseExpressionPhrase();
		e.setSource(inputString);
		return e;
	}

	/**
	 * Parses an expression block (multi-line/statement expression).
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param inputString the input string to parse from.
	 * @param resolver the resolver object for resolving functions in the script.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpressionBlock(String inputString, ExpressionFunctionResolver resolver)
	{
		Expression e = (new EParser(new StringReader(inputString), resolver)).parseExpressionBlock();
		e.setSource(inputString);
		return e;
	}

	/**
	 * Parses a single-line expression.
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param reader the reader to parse from.
	 * @param resolver the resolver object for resolving functions in the script.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpression(Reader reader, ExpressionFunctionResolver resolver)
	{
		Expression e = (new EParser(reader, resolver)).parseExpressionPhrase();
		return e;
	}

	/**
	 * Parses an expression block (multi-line/statement expression).
	 * The expression returned may be a reference to an expression parsed before, due to intern-ing it.
	 * @param reader the reader to parse from.
	 * @param resolver the resolver object for resolving functions in the script.
	 * @return the expression parsed.
	 * @throws ExpressionParseException if a parse error occurs.
	 */
	public static Expression parseExpressionBlock(Reader reader, ExpressionFunctionResolver resolver)
	{
		Expression e = (new EParser(reader, resolver)).parseExpressionBlock();
		return e;
	}

	private static class EKernel extends Lexer.Kernel
	{
		public static final int TYPE_COMMENT = 0;
		public static final int TYPE_LPAREN = 1;
		public static final int TYPE_RPAREN = 2;
		public static final int TYPE_COMMA = 5;
		public static final int TYPE_SEMICOLON = 6;
		public static final int TYPE_TRUE = 8;
		public static final int TYPE_FALSE = 9;
		public static final int TYPE_LBRACE = 10;
		public static final int TYPE_RBRACE = 11;
		public static final int TYPE_INFINITY = 12;
		public static final int TYPE_NAN = 13;
		public static final int TYPE_RETURN = 14;
		public static final int TYPE_IF = 15;
		public static final int TYPE_ELSE = 16;
		
		public static final int TYPE_DASH = 20;
		public static final int TYPE_PLUS = 21;
		public static final int TYPE_STAR = 22;
		public static final int TYPE_SLASH = 23;
		public static final int TYPE_PERCENT = 24;
		public static final int TYPE_AMPERSAND = 25;
		public static final int TYPE_DOUBLEAMPERSAND = 26;
		public static final int TYPE_PIPE = 27;
		public static final int TYPE_DOUBLEPIPE = 28;
		public static final int TYPE_GREATER = 29;
		public static final int TYPE_DOUBLEGREATER = 30;
		public static final int TYPE_TRIPLEGREATER = 31;
		public static final int TYPE_GREATEREQUAL = 32;
		public static final int TYPE_LESS = 33;
		public static final int TYPE_LESSEQUAL = 34;
		public static final int TYPE_DOUBLELESS = 35;
		public static final int TYPE_EQUAL = 36;
		public static final int TYPE_DOUBLEEQUAL = 37;
		public static final int TYPE_TRIPLEEQUAL = 38;
		public static final int TYPE_NOTEQUAL = 39;
		public static final int TYPE_NOTDOUBLEEQUAL = 40;
		public static final int TYPE_EXCLAMATION = 41;
		public static final int TYPE_TILDE = 42;
		public static final int TYPE_CARAT = 43;
		public static final int TYPE_ABSOLUTE = 44; // not scanned
		public static final int TYPE_NEGATE = 45; // not scanned

		private EKernel()
		{
			addStringDelimiter('"', '"');
			setDecimalSeparator('.');
			
			addCommentStartDelimiter("/*", TYPE_COMMENT);
			addCommentLineDelimiter("//", TYPE_COMMENT);
			addCommentEndDelimiter("*/", TYPE_COMMENT);

			addDelimiter("(", TYPE_LPAREN);
			addDelimiter(")", TYPE_RPAREN);
			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter(",", TYPE_COMMA);
			addDelimiter(";", TYPE_SEMICOLON);

			addDelimiter("+", TYPE_PLUS);
			addDelimiter("-", TYPE_DASH);
			addDelimiter("*", TYPE_STAR);
			addDelimiter("/", TYPE_SLASH);
			addDelimiter("%", TYPE_PERCENT);
			addDelimiter("&", TYPE_AMPERSAND);
			addDelimiter("&&", TYPE_DOUBLEAMPERSAND);
			addDelimiter("|", TYPE_PIPE);
			addDelimiter("||", TYPE_DOUBLEPIPE);
			addDelimiter(">", TYPE_GREATER);
			addDelimiter(">>", TYPE_DOUBLEGREATER);
			addDelimiter(">>>", TYPE_TRIPLEGREATER);
			addDelimiter(">=", TYPE_GREATEREQUAL);
			addDelimiter("<", TYPE_LESS);
			addDelimiter("<=", TYPE_LESSEQUAL);
			addDelimiter("<<", TYPE_DOUBLELESS);
			addDelimiter("=", TYPE_EQUAL);
			addDelimiter("==", TYPE_DOUBLEEQUAL);
			addDelimiter("===", TYPE_TRIPLEEQUAL);
			addDelimiter("!", TYPE_EXCLAMATION);
			addDelimiter("!=", TYPE_NOTEQUAL);
			addDelimiter("!==", TYPE_NOTDOUBLEEQUAL);
			addDelimiter("~", TYPE_TILDE);
			
			addCaseInsensitiveKeyword("true", TYPE_TRUE);
			addCaseInsensitiveKeyword("false", TYPE_FALSE);
			addCaseInsensitiveKeyword("infinity", TYPE_INFINITY);
			addCaseInsensitiveKeyword("nan", TYPE_NAN);
			addCaseInsensitiveKeyword("if", TYPE_IF);
			addCaseInsensitiveKeyword("else", TYPE_ELSE);
			addCaseInsensitiveKeyword("return", TYPE_RETURN);
			
		}
		
	}
	
	/**
	 * Parser. 
	 */
	private static class EParser extends Lexer.Parser
	{
		private static final EKernel KERNEL = new EKernel();
		
		private ExpressionFunctionResolver functionResolver;
		private LinkedList<String> errorMessages;
		
		private EParser(Reader reader, ExpressionFunctionResolver resolver)
		{
			super(new Lexer(KERNEL, reader));
			this.functionResolver = resolver;
		}

		/**
		 * Parses an expression block.
		 * @return the expression parsed.
		 */
		Expression parseExpressionBlock()
		{
			nextToken();
			Queue<ExpressionNode> nodeList = new LinkedList<>();
			if (!parseExpressionStatementList(nodeList))
			{
				String[] errors = getErrorMessages();
				if (errors.length > 0)
				{
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < errors.length; i++)
					{
						sb.append(errors[i]);
						if (i < errors.length-1)
							sb.append('\n');
					}
					throw new ExpressionParseException(sb.toString());
				}
				else
					throw new ExpressionParseException("Some error happened, but you shouldn't see this.");
			}
		
			ExpressionNode[] nodes = new ExpressionNode[nodeList.size()];
			nodeList.toArray(nodes);
			
			Expression expression = Expression.create(nodes);
			if (expression.isCollapsible())
				expression.collapse();
			
			return expression.intern();
		}

		private void addErrorMessage(String message)
		{
			errorMessages.add(message);
		}

		private String[] getErrorMessages()
		{
			String[] out = new String[errorMessages.size()];
			errorMessages.toArray(out);
			return out; 
		}
		
		/**
		 * Parses an expression phrase.
		 * @return the expression parsed.
		 */
		Expression parseExpressionPhrase()
		{
			nextToken();
			Queue<ExpressionNode> nodeList = new LinkedList<>();
			if (!parseExpressionPhrase(nodeList))
			{
				String[] errors = getErrorMessages();
				if (errors.length > 0)
				{
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < errors.length; i++)
					{
						sb.append(errors[i]);
						if (i < errors.length-1)
							sb.append('\n');
					}
					throw new ExpressionParseException(sb.toString());
				}
				else
					throw new ExpressionParseException("Some error happened, but you shouldn't see this.");
			}
		
			ExpressionNode[] nodes = new ExpressionNode[nodeList.size()];
			nodeList.toArray(nodes);
			
			Expression expression = Expression.create(nodes);
			if (expression.isCollapsible())
				expression.collapse();
			
			return expression.intern();
		}

		/*
		 * <ExpressionBlock> :
		 * 		"{" <ExpressionStatementList> "}"
		 * 		<ExpressionStatement>
		 */
		private boolean parseExpressionBlock(Queue<ExpressionNode> nodeList)
		{
			if (matchType(EKernel.TYPE_LBRACE))
			{
				if (!parseExpressionStatementList(nodeList))
					return false;
				
				if (!matchType(EKernel.TYPE_RBRACE))
				{
					addErrorMessage("Expected ending brace \"}\" to terminate multi-line expression block.");
					return false;
				}
				
				return true;
			}
			else
				return parseExpressionStatement(nodeList);
		}

		/*
		 * <ExpressionStatementList> :
		 * 		<ExpressionStatement> <ExpressionStatementList>
		 * 		[e]
		 */
		private boolean parseExpressionStatementList(Queue<ExpressionNode> nodeList)
		{
			if (currentType(EKernel.TYPE_IF, EKernel.TYPE_RETURN, EKernel.TYPE_IDENTIFIER))
			{
				if (!parseExpressionStatement(nodeList))
					return false;
				
				return parseExpressionStatementList(nodeList);
			}
			
			return true;
		}

		/*
		 * <ExpressionStatement> :
		 * 		"if" <ExpressionIfBranch>
		 * 		"return" <ExpressionPhrase> ";"
		 * 		<VARIABLE> "=" <ExpressionPhrase> ";"
		 */
		private boolean parseExpressionStatement(Queue<ExpressionNode> nodeList)
		{
			if (matchType(EKernel.TYPE_IF))
			{
				return parseExpressionIfBranch(nodeList);
			}
			else if (matchType(EKernel.TYPE_RETURN))
			{
				if (!parseExpressionPhrase(nodeList))
					return false;
		
				if (!matchType(EKernel.TYPE_SEMICOLON))
				{
					addErrorMessage("Expected \";\" to terminate return clause.");
					return false;
				}
		
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.RETURN));
				return true;
			}
			else if (currentType(EKernel.TYPE_IDENTIFIER))
			{
				String name = currentToken().getLexeme();
				nextToken();
				
				if (functionResolver.containsFunctionByName(name))
				{
					addErrorMessage("Expected variable or expression statement - can't use function names.");
					return false;
				}
				
				if (!matchType(EKernel.TYPE_EQUAL))
				{
					addErrorMessage("Expected \"=\" assignment operator after variable.");
					return false;
				}
				
				if (!parseExpressionPhrase(nodeList))
					return false;
		
				if (!matchType(EKernel.TYPE_SEMICOLON))
				{
					addErrorMessage("Expected \";\" to terminate assignment statement.");
					return false;
				}
				
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.POP, name));
				return true;
			}
			else
			{
				addErrorMessage("Expected expression statement.");
				return false;
			}
		}

		/*
		 * <ExpressionIfBranch> :
		 * 		"(" <ExpressionPhrase> ")" <ExpressionBlock> <ExpressionElseClause>
		 */
		private boolean parseExpressionIfBranch(Queue<ExpressionNode> nodeList)
		{
			if (!matchType(EKernel.TYPE_LPAREN))
			{
				addErrorMessage("Expected \"(\" after 'if'.");
				return false;
			}
		
			Queue<ExpressionNode> conditionalList = new LinkedList<>();
			if (!parseExpressionPhrase(conditionalList))
				return false;
			
			if (!matchType(EKernel.TYPE_RPAREN))
			{
				addErrorMessage("Expected \")\" after 'if' conditional.");
				return false;
			}
		
			Queue<ExpressionNode> successList = new LinkedList<>();
			if (!parseExpressionBlock(successList))
				return false;
			
			ExpressionNode[] failureNodes = null;
			if (matchType(EKernel.TYPE_ELSE))
			{
				Queue<ExpressionNode> failureList = new LinkedList<>();
				if (!parseExpressionBlock(failureList))
					return false;
				
				failureNodes = getNodeArray(failureList);
			}
			
			nodeList.add(ExpressionBranch.create(getNodeArray(conditionalList), getNodeArray(successList), failureNodes));
			return true;
		}

		// <ExpressionPhrase>
		// If null, bad parse.
		private boolean parseExpressionPhrase(Queue<ExpressionNode> nodeList)
		{
			// make stacks.
			LinkedList<Integer> operatorStack = new LinkedList<>();
			int[] expressionValueCounter = new int[1];
		    
		    // was the last read token a value?
			boolean lastWasValue = false;
		    boolean keepGoing = true;		
		
		    while (keepGoing)
		    {
		    	// if no more tokens...
		    	if (currentToken() == null)
		    	{
		    		keepGoing = false;
		    	}
		    	// if the last thing seen was a value....
		    	else if (lastWasValue)
		    	{
		    		int type = currentToken().getType();
		    		if (isBinaryOperatorType(type))
		    		{
		    			int nextOperator;
		    	        switch (type)
		    	        {
		    	            case EKernel.TYPE_PLUS:
		    	            case EKernel.TYPE_DASH:
		    	            case EKernel.TYPE_STAR:
		    	            case EKernel.TYPE_SLASH:
		    	            case EKernel.TYPE_PERCENT:
		    	            case EKernel.TYPE_AMPERSAND:
		    	            case EKernel.TYPE_DOUBLEAMPERSAND:
		    	            case EKernel.TYPE_PIPE:
		    	            case EKernel.TYPE_DOUBLEPIPE:
		    	            case EKernel.TYPE_CARAT:
		    	            case EKernel.TYPE_GREATER:
		    	            case EKernel.TYPE_GREATEREQUAL:
		    	            case EKernel.TYPE_DOUBLEGREATER:
		    	            case EKernel.TYPE_TRIPLEGREATER:
		    	            case EKernel.TYPE_LESS:
		    	            case EKernel.TYPE_DOUBLELESS:
		    	            case EKernel.TYPE_LESSEQUAL:
		    	            case EKernel.TYPE_DOUBLEEQUAL:
		    	            case EKernel.TYPE_TRIPLEEQUAL:
		    	            case EKernel.TYPE_NOTEQUAL:
		    	            case EKernel.TYPE_NOTDOUBLEEQUAL:
		    	            	nextOperator = type;
		    	            	break;
		    	            default:
		            		{
		        				addErrorMessage("Unexpected binary operator miss.");
		        				return false;
		            		}
		    	        }
		    	        
		    	        nextToken();
		    	        if (!operatorReduce(nodeList, operatorStack, expressionValueCounter, nextOperator))
		    	        	return false;
		    	        
		    	        operatorStack.addFirst(nextOperator);
		    	        lastWasValue = false;
		    		}
		    		else
		    		{
		    			// end on a value.
		    			keepGoing = false;
		    		}
		    	}
		    	// if the last thing seen was an operator (or nothing)...
		    	else
		    	{
		    		int type = currentToken().getType();
		    		// unary operator
		    		if (isUnaryOperatorType(type))
		    		{
		    			switch (type)
		        		{
			                case EKernel.TYPE_PLUS:
			                	operatorStack.push(EKernel.TYPE_ABSOLUTE);
			                	break;
			                case EKernel.TYPE_DASH:
			                	operatorStack.push(EKernel.TYPE_NEGATE);
			                	break;
			                case EKernel.TYPE_EXCLAMATION:
			                	operatorStack.push(EKernel.TYPE_EXCLAMATION);
			                	break;
			                case EKernel.TYPE_TILDE:
			                	operatorStack.push(EKernel.TYPE_TILDE);
			                	break;
			                default:
			                	throw new ExpressionParseException("Unexpected unary operator miss.");
		        		}
		    			nextToken();
		    			lastWasValue = false;
		    		}
		    		// parens.
		    		else if (matchType(EKernel.TYPE_LPAREN))
		    		{
		    			if (!parseExpressionPhrase(nodeList))
		    				return false;
		    			
		    			if (!matchType(EKernel.TYPE_RPAREN))
		    			{
		    				addErrorMessage("Expected ending \")\".");
		    				return false;
		    			}
		    			
		    			expressionValueCounter[0] += 1;
		    			lastWasValue = true;
		    		}
		    		// identifier - may be host function.
		    		else if (currentType(EKernel.TYPE_IDENTIFIER))
		    		{
		    			String lexeme = currentToken().getLexeme();
		
		    			// is function?
		    			ExpressionFunctionType functionType;
		    			if ((functionType = functionResolver.getFunctionByName(lexeme)) != null)
		    			{
		    				nextToken();
		    				if (!parseExpressionFunctionCall(nodeList, functionType))
		    					return false;
		    				
		    				nodeList.add(ExpressionFunction.create(functionType));
		    			}
		    			// must be variable?
		    			else
		    			{
		    				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH_VARIABLE, lexeme));
		    				nextToken();
		    			}
		    			
		    			expressionValueCounter[0] += 1;
		    			lastWasValue = true;
		    		}
		    		// literal value?
		    		else if (isValidLiteralType(type))
		    		{
		    			if (!parseExpressionSingleValue(nodeList))
		                	return false;
		
		    			expressionValueCounter[0] += 1;
		    			lastWasValue = true;
		    		}
		    		else
		            	throw new ExpressionParseException("Expression - Expected value.");
		    		
		    	}
		    	
		    }
			
		    // end of expression - reduce.
		    while (!operatorStack.isEmpty())
		    {
		        if (!expressionReduce(nodeList, operatorStack, expressionValueCounter))
		            return false;
		    }
		    
		    if (expressionValueCounter[0] != 1)
		    {
		        addErrorMessage("Expected valid expression.");
		        return false;
		    }
		
		    return true;
		}

		// Parses a function call.
		// 		( .... , .... )
		private boolean parseExpressionFunctionCall(Queue<ExpressionNode> nodeList, ExpressionFunctionType functionType)
		{
			if (!matchType(EKernel.TYPE_LPAREN))
			{
				addErrorMessage("Expected \"(\" after a function name.");
				return false;
			}
			
			int argCount = functionType.getArgumentCount();
			while (argCount-- > 0)
			{
				if (!parseExpressionPhrase(nodeList))
					return false;
				
				if (argCount > 0)
				{
					if (!matchType(EKernel.TYPE_COMMA))
					{
						addErrorMessage("Expected \",\" after a function parameter.");
						return false;
					}
				}
			}
		
			if (!matchType(EKernel.TYPE_RPAREN))
			{
				addErrorMessage("Expected \")\" after a function call's parameters.");
				return false;
			}
			
			return true;
		}

		// <ExpressionValue> :
		//		
		// If null, bad parse.
		private boolean parseExpressionSingleValue(Queue<ExpressionNode> nodeList)
		{
			if (matchType(EKernel.TYPE_TRUE))
			{
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, true));
				return true;
			}
			else if (matchType(EKernel.TYPE_FALSE))
			{
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, false));
				return true;
			}
			else if (matchType(EKernel.TYPE_INFINITY))
			{
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, Double.POSITIVE_INFINITY));
				return true;
			}
			else if (matchType(EKernel.TYPE_NAN))
			{
				nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, Double.NaN));
				return true;
			}
			else if (currentType(EKernel.TYPE_NUMBER))
			{
				String lexeme = currentToken().getLexeme();
				nextToken();
				if (lexeme.startsWith("0X") || lexeme.startsWith("0x"))
				{
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, Long.parseLong(lexeme.substring(2), 16)));
					return true;
				}
				else if (lexeme.contains("."))
				{
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, Double.parseDouble(lexeme)));
					return true;
				}
				else
				{
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.PUSH, Long.parseLong(lexeme)));
					return true;
				}
			}
			else
			{
				addErrorMessage("Expression - Expected a literal value.");
				return false;
			}
		}

		// Operator reduce.
		private boolean operatorReduce(Queue<ExpressionNode> nodeList, LinkedList<Integer> operatorStack, int[] expressionValueCounter, int nextOperator) 
		{
			Integer top = operatorStack.peek();
			while (top != null && (getOperatorPrecedence(top) > getOperatorPrecedence(nextOperator) || (getOperatorPrecedence(top) == getOperatorPrecedence(nextOperator) && !isOperatorRightAssociative(nextOperator))))
			{
				if (!expressionReduce(nodeList, operatorStack, expressionValueCounter))
					return false;
				top = operatorStack.peek();
			}
			
			return true;
		}

		// Reduces an expression by operator.
		private boolean expressionReduce(Queue<ExpressionNode> nodeList, LinkedList<Integer> operatorStack, int[] expressionValueCounter)
		{
			if (operatorStack.isEmpty())
		        throw new ExpressionParseException("Internal error - operator stack must have one operator in it.");
		
		    int operator = operatorStack.pollFirst();
		    
		    if (isBinaryOperatorType(operator))
		        expressionValueCounter[0] -= 2;
		    else
		        expressionValueCounter[0] -= 1;
		    
		    if (expressionValueCounter[0] < 0)
		        throw new ExpressionParseException("Internal error - value counter did not have enough counter.");
		    
		    expressionValueCounter[0] += 1; // the "push"
		
		    switch (operator)
		    {
		    	case EKernel.TYPE_ABSOLUTE:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.ABSOLUTE));
		            return true;
		    	case EKernel.TYPE_EXCLAMATION: 
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LOGICAL_NOT));
		            return true;
		    	case EKernel.TYPE_TILDE: 
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.NOT));
		            return true;
		    	case EKernel.TYPE_NEGATE:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.NEGATE));
		            return true;
		        case EKernel.TYPE_PLUS:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.ADD));
		            return true;
		        case EKernel.TYPE_DASH:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.SUBTRACT));
		            return true;
		        case EKernel.TYPE_STAR:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.MULTIPLY));
		            return true;
		        case EKernel.TYPE_SLASH:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.DIVIDE));
		            return true;
		        case EKernel.TYPE_PERCENT:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.MODULO));
		            return true;
		        case EKernel.TYPE_AMPERSAND:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.AND));
		            return true;
		        case EKernel.TYPE_DOUBLEAMPERSAND:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LOGICAL_AND));
		            return true;
		        case EKernel.TYPE_PIPE:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.OR));
		            return true;
		        case EKernel.TYPE_DOUBLEPIPE:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LOGICAL_OR));
		            return true;
		        case EKernel.TYPE_CARAT:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.XOR));
		            return true;
		        case EKernel.TYPE_GREATER:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.GREATER));
		            return true;
		        case EKernel.TYPE_GREATEREQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.GREATER_OR_EQUAL));
		            return true;
		        case EKernel.TYPE_DOUBLEGREATER:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.RIGHT_SHIFT));
		            return true;
		        case EKernel.TYPE_TRIPLEGREATER:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.RIGHT_SHIFT_PADDED));
		            return true;
		        case EKernel.TYPE_LESS:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LESS));
		            return true;
		        case EKernel.TYPE_DOUBLELESS:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LEFT_SHIFT));
		            return true;
		        case EKernel.TYPE_LESSEQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.LESS_OR_EQUAL));
		            return true;
		        case EKernel.TYPE_DOUBLEEQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.EQUAL));
		            return true;
		        case EKernel.TYPE_TRIPLEEQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.STRICT_EQUAL));
		            return true;
		        case EKernel.TYPE_NOTEQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.NOT_EQUAL));
		            return true;
		        case EKernel.TYPE_NOTDOUBLEEQUAL:
					nodeList.add(ExpressionDirective.create(ExpressionDirectiveType.STRICT_NOT_EQUAL));
		            return true;
		    	default:
		            throw new ExpressionParseException("Internal error - Bad operator pushed for expression.");
		    }
		    
		}

		// Return true if token type can be a unary operator.
		private boolean isValidLiteralType(int tokenType)
		{
		    switch (tokenType)
		    {
		        case EKernel.TYPE_NUMBER:
		        case EKernel.TYPE_TRUE:
		        case EKernel.TYPE_FALSE:
		        case EKernel.TYPE_INFINITY:
		        case EKernel.TYPE_NAN:
		            return true;
		        default:
		            return false;
		    }
		}

		// Return true if token type can be a unary operator.
		private boolean isUnaryOperatorType(int tokenType)
		{
		    switch (tokenType)
		    {
		        case EKernel.TYPE_DASH:
		        case EKernel.TYPE_PLUS:
		        case EKernel.TYPE_EXCLAMATION:
		        case EKernel.TYPE_TILDE:
		            return true;
		        default:
		            return false;
		    }
		}

		// Return true if token type can be a binary operator.
		private boolean isBinaryOperatorType(int tokenType)
		{
		    switch (tokenType)
		    {
		        case EKernel.TYPE_PLUS:
		        case EKernel.TYPE_DASH:
		        case EKernel.TYPE_STAR:
		        case EKernel.TYPE_SLASH:
		        case EKernel.TYPE_PERCENT:
		        case EKernel.TYPE_AMPERSAND:
		        case EKernel.TYPE_DOUBLEAMPERSAND:
		        case EKernel.TYPE_PIPE:
		        case EKernel.TYPE_DOUBLEPIPE:
		        case EKernel.TYPE_CARAT:
		        case EKernel.TYPE_GREATER:
		        case EKernel.TYPE_GREATEREQUAL:
		        case EKernel.TYPE_DOUBLEGREATER:
		        case EKernel.TYPE_TRIPLEGREATER:
		        case EKernel.TYPE_LESS:
		        case EKernel.TYPE_DOUBLELESS:
		        case EKernel.TYPE_LESSEQUAL:
		        case EKernel.TYPE_DOUBLEEQUAL:
		        case EKernel.TYPE_TRIPLEEQUAL:
		        case EKernel.TYPE_NOTEQUAL:
		        case EKernel.TYPE_NOTDOUBLEEQUAL:
		            return true;
		        default:
		            return false;
		    }
		}

		// Return operator precedence (higher is better).
		private int getOperatorPrecedence(int tokenType)
		{
		    switch (tokenType)
		    {
		    	case EKernel.TYPE_ABSOLUTE: 
		    	case EKernel.TYPE_EXCLAMATION: 
		    	case EKernel.TYPE_TILDE: 
		    	case EKernel.TYPE_NEGATE:
		    		return 20;
		        case EKernel.TYPE_STAR:
		        case EKernel.TYPE_SLASH:
		        case EKernel.TYPE_PERCENT:
		        	return 18;
		        case EKernel.TYPE_PLUS:
		        case EKernel.TYPE_DASH:
		        	return 16;
		        case EKernel.TYPE_DOUBLEGREATER:
		        case EKernel.TYPE_TRIPLEGREATER:
		        case EKernel.TYPE_DOUBLELESS:
		        	return 14;
		        case EKernel.TYPE_GREATER:
		        case EKernel.TYPE_GREATEREQUAL:
		        case EKernel.TYPE_LESS:
		        case EKernel.TYPE_LESSEQUAL:
		        	return 12;
		        case EKernel.TYPE_DOUBLEEQUAL:
		        case EKernel.TYPE_TRIPLEEQUAL:
		        case EKernel.TYPE_NOTEQUAL:
		        case EKernel.TYPE_NOTDOUBLEEQUAL:
		        	return 10;
		        case EKernel.TYPE_AMPERSAND:
		        	return 8;
		        case EKernel.TYPE_CARAT:
		        	return 6;
		        case EKernel.TYPE_PIPE:
		        	return 4;
		        case EKernel.TYPE_DOUBLEAMPERSAND:
		        	return 2;
		        case EKernel.TYPE_DOUBLEPIPE:
		        	return 1;
		    	default:
		    		return 0;
		    }
		}

		// Return true if token type is an operator that is right-associative.
		private boolean isOperatorRightAssociative(int tokenType)
		{
		    switch (tokenType)
		    {
		    	case EKernel.TYPE_ABSOLUTE: 
		    	case EKernel.TYPE_EXCLAMATION: 
		    	case EKernel.TYPE_TILDE: 
		    	case EKernel.TYPE_NEGATE: 
		    	case EKernel.TYPE_DOUBLEPIPE:
		    		return true;
		    	default:
		    		return false;
		    }
		
		}

		// Get node array.
		private ExpressionNode[] getNodeArray(Queue<ExpressionNode> nodeList)
		{
			ExpressionNode[] nodes = new ExpressionNode[nodeList.size()];
			nodeList.toArray(nodes);
			return nodes;
		}
	}
}
