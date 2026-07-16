package dev.fldt.tumbleweed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author François Luc Denhez-Teuton
 *         Formula serves to store a formula defined by the user, a map
 *         of updatable variables, and a flag indicating whether to catch
 *         arithmetic exceptions or to return a default value.
 */

public class Formula {
	private Map<String, Double> variables;

	private FormulaTree formula;

	private int indexQuantity;
	private IDataStructure data;

	private boolean catchesArithmeticExceptions;
	// If catchesArithmeticExceptions is set to true, arithmetic exceptions 
	// will be caught and the calculation will return 0 instead. This should
	// be done when end-users don't need to think about avoiding these 
	// cases.

	/*
	 * BEGINNING OF INNER CLASSES
	 *
	 * Defining the Node classes here allows them to access the variables
	 * data structure.
	 */

	/**
	 * @author François Luc Denhez-Teuton
	 * 
	 *         An abstract class representing an element of the formula
	 */

	public interface INode 
	{
		public double calcValue() throws UnexpectedVariableException;

		public boolean invalidateCache(Set<String> updatedVariables);

		@Override
		public String toString();
	}

	protected class VariableNode implements INode 
	{	
		private String variable;
		private double cache;

		/**
		 * 
		 * @param variable
		 * 
		 *                 Sets the object to return the value of a registered variable,
		 *                 the
		 *                 named constants pi, e, and phi, or, if set to 'r', a random
		 *                 number
		 *                 between 0 and 1 exclusive
		 */

		public VariableNode(String variable) {
			this.variable = variable;
			this.cache = Double.NaN;
		}


		/**
		 * @return double
		 * 
		 * Returns either Euler's constant, pi, the value of the corresponding 
		 * variable stored in the "variables" Map, or the result of a 
		 * Math.random() call
		 * 
		 * @throws UnexpectedVariableException, if the variable was not found
		 *                                      in the Map
		 */

		@Override
		public double calcValue() throws UnexpectedVariableException 
		{
			if (variable.equals("r"))
				return Math.random();

			if (Double.isNaN(cache))
			{
				switch (variable)
				{
					case "PI":
						cache = Math.PI; break;
					case "E":
						cache = Math.E; break;
					default:
						Double val = variables.get(variable);
						if (val != null)
							cache = val;
						else
							throw new UnexpectedVariableException("Variable : " + variable);
				}
			}
			return cache;
				
		}

		/**
		 * 
		 * @return String
		 * 
		 * returns a String representation of the variable
		 * 
		*/

		@Override
		public String toString() {
			return variable;
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			if (updatedVariables.contains(variable))
			{
				cache = Double.NaN;
				return true;
			}
			else
				return false;
		}


	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 *         A FormulaNode representing a numeric value 
	 */

	protected class ValueNode implements INode {
		private double value;

		/**
		 * 
		 * @param value
		 * 
	     * Sets the value returned by calcValue() 
		 */

		public ValueNode(double value) {
			this.value = value;
		}

		/**
		 * 
		 * @return double
		 * 
		 *  returns the value stored in ValueNode
		 * 
		 */

		@Override
		public double calcValue() {
			return value;
		}

		@Override
		public String toString() {
			return "" + value;
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			return false;
		}

	}

	/* 
	private class DataNode implements INode
	{
		ArrayList<INode> indexFormulas;
		double cache;

		public DataNode(INode[] indexFormulas)
		{
			for (int rank = 0; rank < indexQuantity; rank++)
			{
				this.indexFormulas.add(indexFormulas[rank]);
			}

			cache = Double.NaN;
		}

		public List<INode> getIndexFormulas()
		{
			return indexFormulas;
		}

		@Override
		public double calcValue() throws UnexpectedVariableException
		{
			if (Double.isNaN(cache))
			{
				int[] indices = new int[indexQuantity];
			
				for (int rank = 0; rank < indexQuantity; rank++) 
				{
					indices[rank] = (int) indexFormulas.get(rank).calcValue();
				}

				cache = data.getDataAtIndices(indices, indexQuantity);
			}
				
			return cache;
		}

		@Override
		public String toString()
		{
			StringBuilder result = new StringBuilder("data[");

			for (int rank = 0; rank < indexQuantity; rank++)
			{
				result.append(indexFormulas.get(rank).toString() + "]");

				if (rank < indexQuantity - 1)
				{
					result.append("[");
				}
			}

			return result.toString();
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			boolean cacheIsInvalid = false;

			for (int rank = 0; rank < indexQuantity; rank++) 
			{
				cacheIsInvalid = indexFormulas.get(rank).invalidateCache(updatedVariables);
			}

			if (cacheIsInvalid)
				cache = Double.NaN;

			return cacheIsInvalid;		
		}
	}
	*/

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 *         An enumeration of all the unary functions available to the user.
	 */

	protected enum UnaryFunctions {
		NEGATE, ABS,
		SQRT,
		LN, LOG10,
		SIN, COS, TAN,
		ASIN, ACOS, ATAN,
		SINH, COSH, TANH,
		CEIL, FLOOR,
		FACTORIAL
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 *         UnaryNode: A FormulaNode for storing a unary operator and its
	 *         operand.
	 * 
	 */
	protected class UnaryNode implements INode {
		private UnaryFunctions operator;
		private INode operand;
		private double cache;

		public UnaryNode(UnaryFunctions operator, INode operand) {
			super();
			this.operator = operator;
			this.operand = operand;
			this.cache = Double.NaN;
		}

		// public INode getOperand()
		// {
		// 	return operand;
		// }

		/**
		 * @return double, value of the operand transformed by the operator
		 * 
		 * @throws RuntimeException if the operator is not assigned
		 *                          to a operation
		 */

		@Override
		public double calcValue() throws UnexpectedVariableException {
			if (Double.isNaN(cache))
			{
				switch (operator) 
				{
					case NEGATE:
						cache = -operand.calcValue(); break;
					case SQRT:
						cache = operand.calcValue();
						if (cache < 0 && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = Math.sqrt(cache); 
						break;
					case LOG10:
						cache = operand.calcValue();
						if (cache <= 0 && catchesArithmeticExceptions) 
							cache = 0;
						else
							cache = Math.log10(cache);
						break;
					case LN:
						cache = operand.calcValue();
						if (cache <= 0 && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = Math.log(cache);
						break;
					case SIN:
						cache = Math.sin(operand.calcValue()); break;
					case COS:
						cache = Math.cos(operand.calcValue()); break;
					case TAN:
						cache = operand.calcValue();
						if (Math.cos(cache) == 0 && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = Math.tan(cache);
						break;
					case SINH:
						cache = Math.sinh(operand.calcValue()); break;
					case COSH:
						cache = Math.cosh(operand.calcValue()); break;
					case TANH:
						cache = Math.tanh(operand.calcValue()); break;
					case ASIN:
						cache = operand.calcValue();
						if ((cache > 1 || cache < -1) && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = Math.asin(operand.calcValue());
						break;
					case ACOS:
						cache = operand.calcValue();
						if ((cache > 1 || cache < -1) && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = Math.acos(operand.calcValue());
						break;
					case ATAN:
						cache = Math.atan(operand.calcValue()); break;
					case ABS:
						cache = Math.abs(operand.calcValue()); break;
					case CEIL:
						cache = Math.ceil(operand.calcValue()); break;
					case FLOOR:
						cache = Math.floor(operand.calcValue()); break;
					case FACTORIAL:
						cache = operand.calcValue();
						if ((cache > 12 || cache < 0) && catchesArithmeticExceptions)
							cache = 0;
						else {
							double result = 1;
							for (double cacheL = Math.floor(cache); cacheL > 1; cacheL--) {
								result *= cacheL;
							}
							cache = result;
						}
					default:
						throw new RuntimeException("Unexpected unary operator in formula: " + formula.toString());
				}
			}
			return cache;
		}

		/**
		 * @return a String that preserves the priority of the operand subtree
		 */

		@Override
		public String toString() {
			switch (operator) {
				case NEGATE:
					return "-" + "(" + operand.toString() + ")";
				case SQRT:
					return "sqrt" + "(" + operand.toString() + ")";
				case LOG10:
					return "log" + "(" + operand.toString() + ")";
				case LN:
					return "ln" + "(" + operand.toString() + ")";
				case SIN:
					return "sin" + "(" + operand.toString() + ")";
				case COS:
					return "cos" + "(" + operand.toString() + ")";
				case TAN:
					return "tan" + "(" + operand.toString() + ")";
				case SINH:
					return "sinh" + "(" + operand.toString() + ")";
				case COSH:
					return "cosh" + "(" + operand.toString() + ")";
				case TANH:
					return "tanh" + "(" + operand.toString() + ")";
				case ASIN:
					return "asin" + "(" + operand.toString() + ")";
				case ACOS:
					return "acos" + "(" + operand.toString() + ")";
				case ATAN:
					return "atan" + "(" + operand.toString() + ")";
				case ABS:
					return "abs" + "(" + operand.toString() + ")";
				case CEIL:
					return "ceil" + "(" + operand.toString() + ")";
				case FLOOR:
					return "floor" + "(" + operand.toString() + ")";
				case FACTORIAL:
					return "!" + "(" + operand.toString() + ")";
				default:
					return operator + "(" + operand.toString() + ")";
			}
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			boolean cacheIsInvalid = operand.invalidateCache(updatedVariables);

			if(cacheIsInvalid)
			{
				cache = Double.NaN;
			}

			return cacheIsInvalid;
				
		}
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 *         An enumeration of all the binary operators available to the user.
	 */

	protected enum BinaryFunctions {
		EQUALS, UNEQUALS,
		GREATER, GREATEREQUALS, LESSER, LESSEREQUALS,
		PLUS, MINUS,
		MULTIPLY, DIVIDE, MODULO,
		EXPONENT
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 *         BinaryNode: A FormulaNode containing a binary operator and its two
	 *         operands.
	 */
	protected class BinaryNode implements INode {
		private BinaryFunctions operator;
		private INode operand1, operand2;
		private double cache;

		public BinaryNode(BinaryFunctions operator, INode operand1, INode operand2) {
			super();
			this.operator = operator;
			this.operand1 = operand1;
			this.operand2 = operand2;
			this.cache = Double.NaN;
		}

		/**
		 * The minimum difference between 2 numbers for them to be considered
		 * different according to the comparison operators.
		 */

		double PRECISION = 0.00001;

		/**
		 * @return a Double containing the result of the binary operation
		 * 
		 */

		@Override
		public double calcValue() throws UnexpectedVariableException 
		{
			if (Double.isNaN(cache))
			{
				Double cache1, cache2;
				switch (operator) {
					case PLUS:
						cache = operand1.calcValue() + operand2.calcValue(); 
						break;
					case MINUS:
						cache = operand1.calcValue() - operand2.calcValue();
						break;
					case MULTIPLY:
						cache = operand1.calcValue() * operand2.calcValue();
						break;
					case DIVIDE:
						if ((cache1 = operand2.calcValue()) == 0 && 
						        catchesArithmeticExceptions)
							cache = 0;
						else
							cache = operand1.calcValue() / cache1;
						break;
					case MODULO:
						if ((cache1 = operand2.calcValue()) == 0 && 
								catchesArithmeticExceptions)
							cache = 0;
						else
							cache = (operand1.calcValue() % cache1 + cache1) % cache1;
						break;
					case EXPONENT:
						cache1 = Math.pow(operand1.calcValue(), operand2.calcValue());
						if (cache1.isNaN() && catchesArithmeticExceptions)
							cache = 0;
						else
							cache = cache1;
						break;
					// To avoid rounding errors due to saving as double, equality means "being close
					// by 10^-5"
					case EQUALS:
						if (Math.abs(operand1.calcValue() - operand2.calcValue()) < PRECISION)
							cache = 1;
						else
							cache = 0;
						break;
					case UNEQUALS:
						if (Math.abs(operand1.calcValue() - operand2.calcValue()) > PRECISION)
							cache = 1;
						else
							cache = 0;
						break;
					case GREATER:
						if (Math.abs((cache1 = operand1.calcValue()) - 
						             (cache2 = operand2.calcValue())) > PRECISION &&
									  cache1 > cache2)
							cache = 1;
						else
							cache = 0;
						break;
					case LESSER:
						if (Math.abs((cache1 = operand1.calcValue()) - (cache2 = operand2.calcValue())) > PRECISION
								&& cache1 < cache2)
							cache = 1;
						else
							cache = 0;
						break;
					default:
						throw new RuntimeException("Unexpected binary operator in formula: " + formula.toString());
				}
			}
			return cache;
		}

		@Override
		public String toString() {
			switch (operator) {
				case PLUS:
					return "(" + operand1.toString() + "+" + operand2.toString() + ")";
				case MINUS:
					return "(" + operand1.toString() + "-" + operand2.toString() + ")";
				case MULTIPLY:
					return "(" + operand1.toString() + "*" + operand2.toString() + ")";
				case DIVIDE:
					return "(" + operand1.toString() + "/" + operand2.toString() + ")";
				case MODULO:
					return "(" + operand1.toString() + "%" + operand2.toString() + ")";
				case EXPONENT:
					return "(" + operand1.toString() + "^" + operand2.toString() + ")";
				case EQUALS:
					return "(" + operand1.toString() + "=" + operand2.toString() + ")";
				case UNEQUALS:
					return "(" + operand1.toString() + "!=" + operand2.toString() + ")";
				case GREATER:
					return "(" + operand1.toString() + ">" + operand2.toString() + ")";
				case LESSER:
					return "(" + operand1.toString() + "<" + operand2.toString() + ")";
				default:
					return "(" + operand1.toString() + operator + operand2.toString() + ")";
			}
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			boolean cacheIsInvalid;

			cacheIsInvalid = operand1.invalidateCache(updatedVariables) ||
			                  operand2.invalidateCache(updatedVariables);

			if(cacheIsInvalid)
				cache = Double.NaN;

			return cacheIsInvalid;
		}
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 *         An enumeration of all the ternary operators available to the user.
	 */

	protected enum TernaryFunctions {
		SUM, PRODUCT
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 *         TernaryNode: A FormulaNode for storing ternary operators and their
	 *         operands: a counting variable, a limit (as an expression), and the
	 *         expression to be evaluated.
	 * 
	 */

	protected class TernaryNode implements INode {
		private TernaryFunctions op;
		private VariableNode var;
		private INode limit, operand;
		private double cache;

		TernaryNode(TernaryFunctions operator, VariableNode countingVar, INode limit, INode operand) {
			if (countingVar.toString().equals("r"))
				throw new UnexpectedVariableException(
						"Cannot assign random variable 'r' as the counting variable.");
			op = operator;
			var = countingVar;
			this.limit = limit;
			this.operand = operand;
			this.cache = Double.NaN;
		}

		@Override
		public double calcValue() {

			if(Double.isNaN(cache))
			{
				double lim = limit.calcValue();

				switch (op) 
				{
					case SUM: {
						double result = 0;
						for (double i = 1; i <= lim; i++) {
							putVar(var.toString(), i);
							result += operand.calcValue();
						}
						variables.remove(var.toString());
						cache = result;
					}
					case PRODUCT: {
						double result = 1;
						for (double i = 1; i <= lim; i++) {
							putVar(var.toString(), i);
							result *= operand.calcValue();
						}
						variables.remove(var.toString());
						cache = result;
					}
					default:
						throw new RuntimeException("Unexpected ternary operator in formula: " + formula.toString());
				}
			}

			return cache;
			
		}

		@Override
		public String toString() {
			switch (op) {
				case SUM:
					return "sum(" + var + "," + limit.toString() + "," + operand.toString() + ")";
				case PRODUCT:
					return "mult(" + var + "," + limit.toString() + "," + operand.toString() + ")";
				default:
					return op + "(" + var + "," + limit.toString() + "," + operand.toString() + ")";
			}
		}

		@Override
		public boolean invalidateCache(Set<String> updatedVariables)
		{
			boolean cacheIsInvalid;

			cacheIsInvalid = limit.invalidateCache(updatedVariables) || 
		                        operand.invalidateCache(updatedVariables);

			if(cacheIsInvalid)
				cache = Double.NaN;

			return cacheIsInvalid;
		}
	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 *         FormulaTree: Class responsible for storing and building the formula
	 *         abstract syntax tree.
	 */
	private class FormulaTree {
		INode root;

		/**
		 * @param root The root Node of a completed tree
		 *             Copy Constructor.
		 */
		public FormulaTree(INode root) {
			this.root = root;
		}

		/**
		 * @param formula FormulaTokens in Reverse Polish Notation
		 *                Constructor.
		 */
		public FormulaTree(FormulaTokens formula) {
			Stack<INode> treeStack = new Stack<>();

			for (Token e : formula) {
				switch (e.level) {
					case 0:
						Character c = e.element.charAt(0);

						if (c == '.' || Character.isDigit(c)) 
						{
							treeStack.push(new ValueNode(Double.parseDouble(e.element)));
						} 
						// else if (e.element.equals("data")) 
						// {
							
						// }
						else 
						{
							treeStack.push(new VariableNode(e.element));
						}

						break;

					case 1:
					case 2:
					case 3:
					case 4:
						INode operand1, operand2;
						operand2 = treeStack.pop();
						operand1 = treeStack.pop();

						switch (e.element) {
							case "!=":
								treeStack.push(new BinaryNode(BinaryFunctions.UNEQUALS, operand1, operand2));
								break;
							case "=":
								treeStack.push(new BinaryNode(BinaryFunctions.EQUALS, operand1, operand2));
								break;
							case ">":
								treeStack.push(new BinaryNode(BinaryFunctions.GREATER, operand1, operand2));
								break;
							case "<":
								treeStack.push(new BinaryNode(BinaryFunctions.LESSER, operand1, operand2));
								break;
							case "+":
								treeStack.push(new BinaryNode(BinaryFunctions.PLUS, operand1, operand2));
								break;
							case "-":
								treeStack.push(new BinaryNode(BinaryFunctions.MINUS, operand1, operand2));
								break;
							case "*":
								treeStack.push(new BinaryNode(BinaryFunctions.MULTIPLY, operand1, operand2));
								break;
							case "/":
								treeStack.push(new BinaryNode(BinaryFunctions.DIVIDE, operand1, operand2));
								break;
							case "%":
								treeStack.push(new BinaryNode(BinaryFunctions.MODULO, operand1, operand2));
								break;
							case "^":
								treeStack.push(new BinaryNode(BinaryFunctions.EXPONENT, operand1, operand2));
								break;
							default:
								throw new UnexpectedTokenException();
						}
						break;

					case 5:
					case 6:
						INode operand, limit, var;
						switch (e.element) {
							case "--":
								treeStack.push(new UnaryNode(UnaryFunctions.NEGATE, treeStack.pop()));
								break;
							case "sqrt":
								treeStack.push(new UnaryNode(UnaryFunctions.SQRT, treeStack.pop()));
								break;
							case "log":
								treeStack.push(new UnaryNode(UnaryFunctions.LOG10, treeStack.pop()));
								break;
							case "ln":
								treeStack.push(new UnaryNode(UnaryFunctions.LN, treeStack.pop()));
								break;
							case "sin":
								treeStack.push(new UnaryNode(UnaryFunctions.SIN, treeStack.pop()));
								break;
							case "cos":
								treeStack.push(new UnaryNode(UnaryFunctions.COS, treeStack.pop()));
								break;
							case "tan":
								treeStack.push(new UnaryNode(UnaryFunctions.TAN, treeStack.pop()));
								break;
							case "sinh":
								treeStack.push(new UnaryNode(UnaryFunctions.SINH, treeStack.pop()));
								break;
							case "cosh":
								treeStack.push(new UnaryNode(UnaryFunctions.COSH, treeStack.pop()));
								break;
							case "tanh":
								treeStack.push(new UnaryNode(UnaryFunctions.TANH, treeStack.pop()));
								break;
							case "asin":
								treeStack.push(new UnaryNode(UnaryFunctions.ASIN, treeStack.pop()));
								break;
							case "acos":
								treeStack.push(new UnaryNode(UnaryFunctions.ACOS, treeStack.pop()));
								break;
							case "atan":
								treeStack.push(new UnaryNode(UnaryFunctions.ATAN, treeStack.pop()));
								break;
							case "abs":
								treeStack.push(new UnaryNode(UnaryFunctions.ABS, treeStack.pop()));
								break;
							case "ceil":
								treeStack.push(new UnaryNode(UnaryFunctions.CEIL, treeStack.pop()));
								break;
							case "floor":
								treeStack.push(new UnaryNode(UnaryFunctions.FLOOR, treeStack.pop()));
								break;
							case "!":
								treeStack.push(new UnaryNode(UnaryFunctions.FACTORIAL, treeStack.pop()));
								break;
							case "sum":
								operand = treeStack.pop();
								limit = treeStack.pop();
								var = treeStack.pop();
								treeStack.push(new TernaryNode(TernaryFunctions.SUM, (VariableNode) var, limit, operand));
								break;
							case "mult":
								operand = treeStack.pop();
								limit = treeStack.pop();
								var = treeStack.pop();
								treeStack.push(
										new TernaryNode(TernaryFunctions.PRODUCT, (VariableNode) var, limit, operand));
								break;
							default:
								throw new UnexpectedTokenException();
						}
						break;
					default:
						throw new UnexpectedTokenException();
				}

			}

			this.root = treeStack.pop();
			if (!treeStack.isEmpty())
				throw new RuntimeException("" + treeStack.pop()
						+ " Non-empty stack at end of abstract syntax tree creation. This indicates a bug. Please contact the developer(s).");
		}

		public double calcValue() throws UnexpectedVariableException {
			return root.calcValue();
		}

		@Override
		public String toString() {
			return root.toString();
		}
	}

	/**
	 * @author François Luc Denhez-Teuton
	 * 
	 *         Token: class for an annotated String representation of
	 *         an element of the formula
	 */

	static public class Token {
		String element;
		int level; // Level of arithmetic precedence. 0 is last, 6 is first, 7 is punctuation

		public Token(String element, int level) {
			this.element = element;
			this.level = level;
		}

		public Token(String element) {
			this.element = element;
			switch (element) {
				case "!=":
				case "=":
				case ">":
				case "<":
					level = 1;
					break;
				case "+":
				case "-":
					level = 2;
					break;
				case "*":
				case "/":
				case "%":
					level = 3;
					break;
				case "^":
					level = 4;
					break;
				case "--":
					level = 5;
					break;
				case "sqrt":
				case "log":
				case "ln":
				case "sin":
				case "cos":
				case "tan":
				case "sinh":
				case "cosh":
				case "tanh":
				case "asin":
				case "acos":
				case "atan":
				case "abs":
				case "ceil":
				case "floor":
				case "!":
				case "sum":
				case "mult":
					level = 6;
					break;
				case "(":
				case ")":
				case ",":
					level = 7;
					break;
				default:
					level = 0;
			}
		}

		@Override
		public String toString() {
			return element + "," + level;
		}

		public String elementToString() {
			return element;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null ||
					!obj.getClass().getName().equals(this.getClass().getName()))
				return false;
			return (((Token) obj).element.equals(this.element) &&
					((Token) obj).level == this.level);

		}

	}

	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 *         FormulaTokens: Class for representing a list of the annotated
	 *         elements
	 *         of a formula in String form.
	 */

	public static class FormulaTokens extends ArrayList<Token> {
		private static final long serialVersionUID = 56L;

		public FormulaTokens() {
			super();
		}

		@Override
		public boolean equals(Object o) {

			// Checking if same class
			if (!(o != null &&
					o.getClass().getName().equals(this.getClass().getName())))
				return false;

			// Checking if same size
			if (((FormulaTokens) o).size() != this.size())
				return false;

			// Checking if all elements are the same
			for (int i = 0; i < this.size(); i++) {
				if (!(((FormulaTokens) o).get(i).equals(this.get(i))))
					return false;
			}

			return true;
		}

		@Override
		public String toString() {
			String tokensString = "";
			for (Token e : this)
				tokensString += e.toString() + "\n";
			return tokensString;
		}

		/*
		 * TABLE OF ACCEPTABLE TOKENS
		 * Numbers: [0-9]*.?[0-9]*
		 * Functions: "sqrt", "log", "ln", "sin", "cos", "tan", "sinh", "cosh", "tanh",
		 * "asin", "acos", "atan", "abs", "ceil", "floor", "sum", "mult"
		 * Variables: [A-Za-z]*[A-Za-z0-9]*
		 * Operators: "!", "!=", "=", ">", ">=", "<", "<=", "+", "-", "*", "/", "%", "^"
		 * Punctuation: "(", ")", ","
		 */

		static public FormulaTokens Tokenize(String formula)
				throws UnexpectedCharacterException, UnexpectedEOLException {
			FormulaTokens formulaTokens = new FormulaTokens();

			if (formula.length() == 0) {
				throw new UnexpectedEOLException("Formula is empty");
			}
			for (int index = 0; index < formula.length(); index++) {
				Character currentCharacter = formula.charAt(index);
				if (Character.isDigit(currentCharacter) || currentCharacter == '.') {
					int indexOffset;
					for (indexOffset = 1; index + indexOffset < formula.length() &&
							(Character.isDigit(formula.charAt(index + indexOffset))
									|| formula.charAt(index + indexOffset) == '.'); indexOffset++) {
					}

					formulaTokens.add(new Token(formula.substring(index, index + indexOffset), 0));
					index += indexOffset - 1;
				} else if (Character.isAlphabetic(currentCharacter)) {
					int indexOffset;
					
					for (indexOffset = 1; index + indexOffset < formula.length() &&
							Character.isAlphabetic(formula.charAt(index + indexOffset)); indexOffset++) 
					{}
	
					String buffer = formula.substring(index, index + indexOffset);
					String bufferLowercase = buffer.toLowerCase();
					
					if (bufferLowercase.equals("sqrt") || bufferLowercase.equals("log") ||
							bufferLowercase.equals("ln") || bufferLowercase.equals("sin") ||
							bufferLowercase.equals("cos") || bufferLowercase.equals("tan") ||
							bufferLowercase.equals("sinh") || bufferLowercase.equals("cosh") ||
							bufferLowercase.equals("tanh") || bufferLowercase.equals("asin") ||
							bufferLowercase.equals("acos") || bufferLowercase.equals("atan") ||
							bufferLowercase.equals("abs") || bufferLowercase.equals("ceil") ||
							bufferLowercase.equals("floor") || bufferLowercase.equals("sum") ||
							bufferLowercase.equals("mult")) {
						formulaTokens.add(new Token(bufferLowercase, 6));
						index += indexOffset - 1;
					} else {
						int temporaryIndex = index + indexOffset;
						for (indexOffset = 0; temporaryIndex + indexOffset < formula.length() &&
								(Character.isAlphabetic(formula.charAt(temporaryIndex + indexOffset)) ||
										Character.isDigit(
												formula.charAt(temporaryIndex + indexOffset))); indexOffset++) {
						}

						buffer = buffer.concat(formula.substring(temporaryIndex, temporaryIndex + indexOffset));
						formulaTokens.add(new Token(buffer, 0));
						index = temporaryIndex + indexOffset - 1;
					}
				} else if (currentCharacter == '!') {
					if (formula.charAt(index + 1) == '=') {
						formulaTokens.add(new Token("!=", 1));
						index++;
					} else {
						formulaTokens.add(new Token(currentCharacter.toString(), 6));
					}
				} else if (currentCharacter == '=') {
					formulaTokens.add(new Token(currentCharacter.toString(), 1));
				} else if (currentCharacter == '>' || currentCharacter == '<') {
					if (formula.charAt(index + 1) == '=') {
						formulaTokens.add(new Token(currentCharacter.toString() + '=', 1));
						index++;
					} else {
						formulaTokens.add(new Token(currentCharacter.toString(), 1));
					}
				} else if (currentCharacter == '+' || currentCharacter == '-') {
					formulaTokens.add(new Token(currentCharacter.toString(), 2));
				} else if (currentCharacter == '*' || currentCharacter == '/' || currentCharacter == '%') {
					formulaTokens.add(new Token(currentCharacter.toString(), 3));
				} else if (currentCharacter == '^') {
					formulaTokens.add(new Token(currentCharacter.toString(), 4));
				} else if (currentCharacter == '(' || currentCharacter == ')') {
					formulaTokens.add(new Token(currentCharacter.toString(), 7));
				} else if (currentCharacter == ',')
					formulaTokens.add(new Token(currentCharacter.toString(), 7));
				else if (currentCharacter == ' ') {
				} else {
					throw new UnexpectedCharacterException("Unexpected Character : " + currentCharacter.toString());
				}
			}

			return formulaTokens;
		}

		/*
		 * Formula grammar in Backus-Naur Form,
		 * (using regular expression notation in expressions to shorten
		 *  the text, and the ^ symbol to indicate the number of 
		 * 	required repetitions)
		 *
		 * <S> ::= <P> (<B> <P>)*
		 * <P> ::= <V> | "(" <S> ")" | "-" <P> | <F> "(" <S> ")" |
		 * 		   <T> "(" <E>*<A>* "," <S> "," <S> ")" | 
		 * 		   "data"("[" <S> "]")^indexQuantity 
		 * <B> ::= "=" | "!=" | ">" | "<" | "+" | "-" | "*" | "/" | "^"
		 * <F> ::= "log" | "ln" | "sin" | "cos" | "tan" | "sinh" | "cosh" |
		 * "tanh" | "asin" | "acos" | "atan" | "sqrt"| "ceil" | "floor" |
		 * "abs"
		 * <T> ::= "sum" | "mult"
		 * <V> ::= <E>*<A>* | <N>*.?<N>*
		 * <E> ::= [a-z|A-Z]
		 * <N> ::= [0-9]
		 * <A> ::= [a-z|A-Z|0-9]
		 */

		private void expect(Token testee, Token expectedToken)
				throws UnexpectedTokenException, UnevenParenthesesException {
			if (testee.equals(expectedToken)) {
			} else if (expectedToken.equals(new Token(")", 7)))
				throw new UnevenParenthesesException("Number of parentheses is uneven");
			else
				throw new UnexpectedTokenException("Unexpected symbol : Expected " + expectedToken.elementToString()
						+ ", got " + testee.elementToString());

		}

		private Token s(Iterator<Token> iterator) throws UnexpectedTokenException, UnevenParenthesesException {
			p(iterator);
			Token next = (iterator.hasNext() ? iterator.next() : new Token("End of Line", 8));
			while (iterator.hasNext()
					&& (next.level != 0 && next.level != 5 && next.level != 6 && next.level != 7 && next.level != 8)) {
				p(iterator);
				next = (iterator.hasNext() ? iterator.next() : new Token("End of Line", 8));
			}
			return next;
		}

		private void p(Iterator<Token> iterator) throws UnexpectedTokenException, UnevenParenthesesException {
			Token next;
			if (iterator.hasNext())
				next = iterator.next();
			else
				throw new UnexpectedEOLException("Unexpected end of formula");
			if (next.level == 0) {
			} else if (next.element.equals("sum") || next.element.equals("mult")) {
				if (iterator.hasNext())
					next = iterator.next();
				else
					throw new UnexpectedEOLException();
				expect(next, new Token("(", 7));
				if (iterator.hasNext())
					next = iterator.next();
				else
					throw new UnexpectedEOLException();
				if (next.level != 0)
					throw new UnexpectedTokenException(
							"Unexpected symbol: Expected a variable, got " + next.elementToString());
				if (iterator.hasNext())
					next = iterator.next();
				else
					throw new UnexpectedEOLException();
				expect(next, new Token(","));
				next = s(iterator);
				expect(next, new Token(","));
				next = s(iterator);
				expect(next, new Token(")"));
			} else if (next.element.equals("(")) {
				next = s(iterator);
				if (!(next.equals(new Token(")"))))
					throw new UnevenParenthesesException("Open parenthese not closed");
			} else if (next.element.equals("-")) {
				next.element = "--";
				next.level = 5;
				p(iterator);
			} else if (next.level == 6) {
				if (iterator.hasNext())
					next = iterator.next();
				else
					throw new UnexpectedEOLException();
				expect(next, new Token("(", 7));
				next = s(iterator);
				expect(next, new Token(")", 7));
			} else
				throw new UnexpectedTokenException("Unexpected symbol : " + next.elementToString());
		}

		public FormulaTokens checkFormula()
				throws UnexpectedTokenException, UnevenParenthesesException, UnexpectedEOLException {
			Iterator<Token> iter = this.iterator();
			Token next = s(iter);
			if (!next.equals(new Token("End of Line", 8)))
				throw new UnexpectedTokenException("Unexpected symbol : " + next.toString());
			return this;
		}

		public FormulaTokens checkVariables(String[] expectedVariables) throws UnexpectedVariableException {
			loop: for (Token t : this) {
				if (t.level == 0) {
					for (String expectedVariable : expectedVariables) {
						if (t.element.equals(expectedVariable) || t.element.matches("[0-9]*.?[0-9]*")
								|| (t.element.toLowerCase()).equals("e") || (t.element.toLowerCase()).equals("pi")
								|| t.element.equals("r"))
							continue loop;
					}
					throw new UnexpectedVariableException(t.elementToString());
				}
			}
			return this;
		}

		public FormulaTokens makePostFix() {
			FormulaTokens result = new FormulaTokens();
			Stack<Token> operatorStack = new Stack<>();

			for (Token e : this) {
				if (e.level == 0) {
					result.add(e);
				} else if (operatorStack.empty()) {
					operatorStack.add(e);
				} else if (e.element.equals(")")) {
					while (!operatorStack.empty() &&
							!operatorStack.peek().element.equals("(")) {
						result.add(operatorStack.pop());
					}
					operatorStack.pop();
				} else if (operatorStack.peek().level < e.level) {
					operatorStack.add(e);
				} else {
					while (!operatorStack.empty() && operatorStack.peek().level >= e.level
							&& operatorStack.peek().level < 7) {
						result.add(operatorStack.pop());
					}
					operatorStack.add(e);
				}
			}

			while (!operatorStack.empty()) {
				result.add(operatorStack.pop());
			}

			return result;
		}
	}

	/*
	 * BEGINNING OF PUBLIC FUNCTIONS
	 */
	public Formula() {
		variables = new HashMap<>();
		catchesArithmeticExceptions = true;
	}

	public Formula(INode root, Map<String, Double> variables) {
		this.variables = variables;
		formula = new FormulaTree(root);
		catchesArithmeticExceptions = true;
	}

	public Map<String, Double> getAllVars() {
		return variables;
	}

	public void putVar(String variable, double value) {
		variables.put(variable, value);
	}

	public double getVar(String variable) {
		return variables.get(variable);
	}

	public void setFormula(String formula, String[] expectedVariables) throws UnexpectedCharacterException,
			UnexpectedTokenException, UnevenParenthesesException, UnexpectedVariableException, UnexpectedEOLException {
		this.formula = new FormulaTree(
				FormulaTokens.Tokenize(formula).checkFormula().checkVariables(expectedVariables).makePostFix());
	}

	public void checkFormula(String formula, String[] expectedVariables) throws UnexpectedCharacterException,
			UnexpectedTokenException, UnevenParenthesesException, UnexpectedVariableException, UnexpectedEOLException {
		FormulaTokens.Tokenize(formula).checkFormula().checkVariables(expectedVariables);
	}

	public double calcValue() throws UnexpectedVariableException {
		return formula.calcValue();
	}

	public int calcValueInt() throws UnexpectedVariableException {
		return (int) calcValue();
	}

	public void setCatchesArithmeticExceptions(boolean catchExceptions) {
		catchesArithmeticExceptions = catchExceptions;

	}

	@Override
	public String toString() {
		String formulaString;
		if (formula == null)
			formulaString = "null";
		else
			formulaString = formula.toString();
		return formulaString + "\n" + variables.entrySet().toString();
	}

	public String formulaToString() {
		String formulaString;
		if (formula == null)
			formulaString = "null";
		else
			formulaString = formula.toString();
		return formulaString;
	}

}
