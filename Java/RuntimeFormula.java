package darformula;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 
 * @author François Luc Denhez-Teuton
 * 
 * RuntimeFormula serves to store a runtime-defined formula (a formula defined
 * by the user) in a form adequate for quick calculations with variables. A 
 * Map is used to store the values for these variables in the RuntimeFormula.
 *   
 */

public class RuntimeFormula 
{
	private Map<String, Double> variables;

	private FormulaTree formula;
	private boolean catchesArithmeticExceptions; 	
	// If catchesArithmeticExceptions is true, the cases where the operators would 
	// throw an arithmetic exception will instead return 0, so that the end-
	// users don't have to think about avoiding these cases. 
	
	/*
	 * BEGINNING OF CLASSES
	 * PUBLIC FUNCTIONS START AFTER "RuntimeFormula()"
	 * */
	
	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 * 
	 * An abstract class representing an element of the formula
	 */
	
	abstract public class FormulaElement{
		
		public FormulaElement(){super();}
		
		abstract public double calcValue() throws UnexpectedVariableException;
		abstract public String toString();
	}
	
	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 * A FormulaElement representing a numeric value or variable
	 */
	
	public class SimpleElement extends FormulaElement
	{
		private double value;
		private String variable;
		
		/**
		 * 
		 * @param value
		 * 
		 * Sets the object to return a numeric value when calcValue() is called
		 */
		
		public SimpleElement(double value)
		{
			super();
			this.value=value;
			this.variable="";
		}
		
		/**
		 * 
		 * @param variable
		 * 
		 * Sets the object to return the value of a registered variable, the 
		 * named constants pi, e, and phi, or, if set to 'r', a random number
		 * between 0 and 1 exclusive
		 */
		
		public SimpleElement(String variable)
		{
			super();
			this.variable=variable;
		}
		
		/**
		 * @return double 
		 * 
		 * Returns either the value of the constant given at construction, the 
		 * value of the corresponding variable stored in the "variables" Map, or 
		 * returns the result of a Math.random() call
		 * 
		 * @throws UnexpectedVariableException, if the variable was not found
		 * in the Map
		 */
		
		public double calcValue() throws UnexpectedVariableException
		{
			if(variable.isEmpty())
				return value;
			if(variable.equals("r"))
				return Math.random();
			if(variable.toLowerCase().equals("pi"))
				return Math.PI;
			if(variable.toLowerCase.equals("e"))
				{return Math.E;}
			Double val=variables.get(variable);
			if(val!=null)
				return val;
			else throw new UnexpectedVariableException("Variable : "+variable.toString());
		}
		
		public String toString()
		{
			if(variable.isEmpty())
				return ""+value;	
			return variable;
		}
		
	}
	
	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 * A FormulaElement for storing unary operators like basic functions and
	 * the negative operator
	 *  
	 */
	public class UnaryElement extends FormulaElement
	{
		private Character operator;
		private FormulaElement operand;
		
		public UnaryElement(Character operator, FormulaElement operand)
		{
			super();
			this.operator=operator;
			this.operand=operand;
		}
		
		
		/**
		 * @return double, value of the operand transformed by the operator
		 * 
		 * @throws RuntimeException if the operator Character is not assigned 
		 * to a operation
		 */
		public double calcValue() throws UnexpectedVariableException
		{
			Double cache;
			switch(operator)
			{
			case '-':return -operand.calcValue();
			case 'x':
				cache=operand.calcValue();
				if(cache < 0 && catchesArithmeticExceptions)
					return 0;
				return Math.sqrt(cache);
			case 'l':
				cache=operand.calcValue();
				if(cache <= 0 && catchesArithmeticExceptions)
				{
					return 0;
				}
				return Math.log10(cache);
			case 'e':
				cache=operand.calcValue();
				if(cache <= 0 && catchesArithmeticExceptions)
				{
					return 0;
				}
				return Math.log(cache);
			case 's':return Math.sin(operand.calcValue());
			case 'c':return Math.cos(operand.calcValue());
			case 't':
				cache=operand.calcValue();
				if(Math.cos(cache)==0 && catchesArithmeticExceptions)
					return 0;
				return Math.tan(cache);
			case 'h':return Math.sinh(operand.calcValue());
			case 'o':return Math.cosh(operand.calcValue());
			case 'n':return Math.tanh(operand.calcValue());
			case 'a':
				cache=operand.calcValue();
				if((cache>1 || cache<-1) && catchesArithmeticExceptions)
					return 0;
				return Math.asin(operand.calcValue());
			case 'q':
				cache=operand.calcValue();
				if((cache>1 || cache<-1) && catchesArithmeticExceptions)
					return 0;
				return Math.acos(operand.calcValue());
			case 'u':return Math.atan(operand.calcValue());
			case 'i':return Math.abs(operand.calcValue());
			case 'k':return Math.ceil(operand.calcValue());
			case 'f':return Math.floor(operand.calcValue());
			case 'r':
				cache=operand.calcValue();
				if((cache>12 || cache<0) && catchesArithmeticExceptions)
					return 0;
				else
				{
					double result=1;
					for(double cacheL=Math.floor(cache);cacheL>1;cacheL--)
					{
						result*=cacheL;
					}
					return result;
				}
			default: throw new RuntimeException("Unexpected Unary operator. Please contact the developer(s). Bad "+ formula.toString());
			}
			
		}
		
		/**
		 * @return a String that preserves the priority of the operand subtree
		 */
		public String toString()
		{
			switch(operator)
			{
			case '-':return "-"+"("+operand.toString()+")";
			case 'x':return "sqrt"+"("+operand.toString()+")";
			case 'l':return "log"+"("+operand.toString()+")";
			case 'e':return "ln"+"("+operand.toString()+")";
			case 's':return "sin"+"("+operand.toString()+")";
			case 'c':return "cos"+"("+operand.toString()+")";
			case 't':return "tan"+"("+operand.toString()+")";
			case 'h':return "sinh"+"("+operand.toString()+")";
			case 'o':return "cosh"+"("+operand.toString()+")";
			case 'n':return "tanh"+"("+operand.toString()+")";
			case 'a':return "asin"+"("+operand.toString()+")";
			case 'q':return "acos"+"("+operand.toString()+")";
			case 'u':return "atan"+"("+operand.toString()+")";
			case 'i':return "abs"+"("+operand.toString()+")";
			case 'k':return "ceil"+"("+operand.toString()+")";
			case 'f': return "floor"+"("+operand.toString()+")";
			case 'r': return "!"+"("+operand.toString()+")";
			default: return operator + "("+ operand.toString()+")";
			}
		}
	}
	
	
	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 * A FormulaElement for binary operators including the comparison operators,
	 * which return 1 if satisfied and 0 if not
	 */
	public class BinaryElement extends FormulaElement
	{
		private Character operator;
		private FormulaElement operand1, operand2;
		
		public BinaryElement(Character operator, FormulaElement operand1, FormulaElement operand2)
		{
			super();
			this.operator=operator;
			this.operand1=operand1;
			this.operand2=operand2;
		}
		
		
		/**
		 *
		 * 
		 * The way equality works, since we are working with doubles, is that the difference
		 * between the two numbers must be less than 10E-5 to be considered equal
		 */
		public double calcValue() throws UnexpectedVariableException
		{
			Double cache1, cache2;
			switch(operator)
			{
			case '+':return operand1.calcValue()+operand2.calcValue();
			case '-':return operand1.calcValue()-operand2.calcValue();
			case '*':return operand1.calcValue()*operand2.calcValue();
			case '/':if((cache1=operand2.calcValue())==0 && catchesArithmeticExceptions)
					{return 0;}
					return operand1.calcValue()/cache1;
			case '%':if((cache1=operand2.calcValue())==0 && catchesArithmeticExceptions)
					{return 0;}
					return (operand1.calcValue()%cache1 + cache1)%cache1;
			case '^':cache1=Math.pow(operand1.calcValue(), operand2.calcValue());
					if(cache1.isNaN() && catchesArithmeticExceptions)
					{return 0;}
					return cache1;
			// To avoid rounding errors due to saving as double, equality means "being close by 10^-5"
			case '=':if(Math.abs(operand1.calcValue()-operand2.calcValue())<0.00001)return 1; else return 0;
			case '!':if(Math.abs(operand1.calcValue()-operand2.calcValue())>0.00001)return 1; else return 0;
			case '>':if(Math.abs((cache1=operand1.calcValue())-(cache2=operand2.calcValue()))>0.00001 && cache1>cache2)return 1; else return 0;
			case '<':if(Math.abs((cache1=operand1.calcValue())-(cache2=operand2.calcValue()))>0.00001 && cache1<cache2)return 1; else return 0;
			default: throw new RuntimeException("Unexpected Binary operator. Please contact the developer(s). Bad "+ formula.toString());
			}
			
		}
		
		public String toString()
		{
			switch(operator)
			{
			case '+':return "("+operand1.toString()+"+"+operand2.toString()+")";
			case '-':return "("+operand1.toString()+"-"+operand2.toString()+")";
			case '*':return "("+operand1.toString()+"*"+operand2.toString()+")";
			case '/':return "("+operand1.toString()+"/"+operand2.toString()+")";
			case '%':return "("+operand1.toString()+"%"+operand2.toString()+")";
			case '^':return "("+operand1.toString()+"^"+operand2.toString()+")";
			case '=':return "("+operand1.toString()+"="+operand2.toString()+")";
			case '!':return "("+operand1.toString()+"!="+operand2.toString()+")";
			case '>':return "("+operand1.toString()+">"+operand2.toString()+")";
			case '<':return "("+operand1.toString()+"<"+operand2.toString()+")";
			default: return "("+operand1.toString()+operator+operand2.toString()+")";
			}
		}
	}

	public class TernaryElement extends FormulaElement
    {
        private Character op;
        private SimpleElement var;
        private FormulaElement limit, operand;

        TernaryElement(Character operator, SimpleElement countingVar, FormulaElement limit, FormulaElement operand)
        {
            op=operator;
            var=countingVar;
            this.limit=limit;
            this.operand=operand;
            if(var.equals(new SimpleElement("r")))
                throw new UnexpectedVariableException("Incorrect variable assigned as the counting variable. Don't use 'r'");
            else if(!var.isVariable)
                throw new UnexpectedVariableException("Assigned number instead of variable");
        }

        public String toString()
        {
            switch(op)
            {
                case 's':return "sum("+var+","+limit.toString()+","+operand.toString()+")";
                case 'm':return "mult("+var+","+limit.toString()+","+operand.toString()+")";
                default:return op+"("+var+","+limit.toString()+","+operand.toString()+")";
            }
        }

        public double calcValue()
        {
            double lim=limit.calcValue();

            switch(op)
            {
                case 's':{
                    double result=0;
                    for (double i = 1; i <= lim; i++) {
                        putVar(var.toString(), i);
                        result+=operand.calcValue();
                    }
                    variables.remove(var.toString());
                    return result;}
                case 'm':{
                    double result=1;
                    for (double i = 1; i <= lim; i++) {
                        putVar(var.toString(), i);
                        result*=operand.calcValue();
                    }
                    variables.remove(var.toString());
                    return result;}
                default: throw new RuntimeException("Unexpected Ternary Operator. Please contact the developer. Bad "+formula.toString());
            }
        }
    }
	
	/**
	 * 
	 * @author François Luc Denhez-Teuton
	 *
	 * The tree representing the formula. Made up of FormulaElements.
	 */
	private class FormulaTree
	{
		FormulaElement root;
		
		/**
		 * @param root The root of the already completed tree
		 * Initialized either by passing the root of an already completed tree (use
		 *  only for UT) or by passing a list of FormulaTokens in Reverse Polish
		 *   Notation
		 */ 
		public FormulaTree(FormulaElement root)
		{
			this.root=root;
		}
		
		/**
		 * @param formula FormulaTokens in RPN
		 * Initialized either by passing the root of an already completed tree (use
		 *  only for UT) or by passing a list of FormulaTokens already in Reverse 
		 *  Polish Notation
		 */ 
		public FormulaTree(FormulaTokens formula)
		{
			Stack<FormulaElement> treeStack = new Stack<FormulaElement>();
			
			for(Token e: formula)
			{
				if(e.level==0)
				{
					Character c=e.element.charAt(0);
					if(c=='.' || Character.isDigit(c))
					{
						treeStack.push(new SimpleElement(Double.parseDouble(e.element)));
					}
					else
					{
						treeStack.push(new SimpleElement(e.element));
					}
				}
				
				else if(e.level==1)
				{
					if(e.element.equals("!="))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('!', operand1,operand2));
					}
					else if(e.element.equals("="))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('=', operand1,operand2));
					}
					else if(e.element.equals(">"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('>', operand1,operand2));
					}
					else if(e.element.equals("<"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('<', operand1,operand2));
					}
				}
				else if(e.level==2)
				{
					if(e.element.equals("+"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('+', operand1,operand2));
					}
					else if(e.element.equals("-"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('-', operand1,operand2));
					}
				}
				else if(e.level==3)
				{
					if(e.element.equals("*"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('*', operand1,operand2));
					}
					else if(e.element.equals("/"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('/', operand1,operand2));
					}
					else if(e.element.equals("%"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('%', operand1,operand2));
					}
				}
				else if(e.level==4)
				{
					if(e.element.equals("^"))
					{
						FormulaElement operand1,operand2;
						operand2=treeStack.pop();
						operand1=treeStack.pop();
						treeStack.push(new BinaryElement('^', operand1,operand2));
					}
				}
				else if(e.level==6)
				{
					if(e.element.equals("sqrt"))
						treeStack.push(new UnaryElement('x',treeStack.pop()));
					else if(e.element.equals("log"))
							treeStack.push(new UnaryElement('l',treeStack.pop()));
					else if(e.element.equals("ln"))
							treeStack.push(new UnaryElement('e',treeStack.pop()));
					else if(e.element.equals("sin"))
							treeStack.push(new UnaryElement('s',treeStack.pop()));
					else if(e.element.equals("cos"))
							treeStack.push(new UnaryElement('c',treeStack.pop()));
					else if(e.element.equals("tan"))
							treeStack.push(new UnaryElement('t',treeStack.pop()));
					else if(e.element.equals("sinh"))
							treeStack.push(new UnaryElement('h',treeStack.pop()));
					else if(e.element.equals("cosh"))
							treeStack.push(new UnaryElement('o',treeStack.pop()));
					else if(e.element.equals("tanh"))
							treeStack.push(new UnaryElement('n',treeStack.pop()));
					else if(e.element.equals("asin"))
							treeStack.push(new UnaryElement('a',treeStack.pop()));
					else if(e.element.equals("acos"))
							treeStack.push(new UnaryElement('q',treeStack.pop()));
					else if(e.element.equals("atan"))
							treeStack.push(new UnaryElement('u',treeStack.pop()));
					else if(e.element.equals("abs"))
							treeStack.push(new UnaryElement('i',treeStack.pop()));
					else if(e.element.equals("ceil"))
							treeStack.push(new UnaryElement('k',treeStack.pop()));
					else if(e.element.equals("floor"))
							treeStack.push(new UnaryElement('f',treeStack.pop()));
					else if(e.element.equals("!"))
						treeStack.push(new UnaryElement('r',treeStack.pop()));
                    else if(e.element.equals("sum"))
                    {
                        FormulaElement operand, limit, var;
                        operand=treeStack.pop();
                        limit=treeStack.pop();
                        var=treeStack.pop();
                        treeStack.push(new TernaryElement('s',(SimpleElement)var,limit,operand ));
                    }
                    else if(e.element.equals("mult"))
                    {
                        FormulaElement operand, limit, var;
                        operand=treeStack.pop();
                        limit=treeStack.pop();
                        var=treeStack.pop();
                        treeStack.push(new TernaryElement('m',(SimpleElement)var,limit,operand ));
                    }
				}
				else if(e.level==5)
				{
					if(e.element.equals("--"))
						treeStack.push(new UnaryElement('-',treeStack.pop()));
				}
			}
			
			this.root=treeStack.pop();
			if(!treeStack.isEmpty())
				throw new RuntimeException(""+treeStack.pop()+" Non-empty stack at end of abstract syntax tree creation. Please contact the developer(s).");
		}
		
		public double calcValue() throws UnexpectedVariableException
		{
			return root.calcValue();	
		}
		
		public String toString()
		{
			return root.toString();
		}
	}
	
	static public class Token
	{
		String element;
		int level;
		
		public Token(String element, int level)
		{
			this.element=element;
			this.level=level;
		}
		
		public Token(String element)
		{
			this.element=element;
			switch(element)
			{
			case "!=":case "=":case ">":case "<": 
				level=1;break;
			case "+": case "-":
				level=2;break;
			case "*": case "/": case "%":
				level=3;break;
			case "^":
				level=4;break;
			case "--":
				level=5;break;
			case "sqrt" : case "log" : case "ln" :  case "sin" :  case "cos": case "tan" : case "sinh" : case "cosh" : case "tanh" : case "asin" : case "acos" : case "atan" : case "abs" : case "ceil" : case "floor": case "!":case "sum":case "mult":
				level=6;break;
			case "(": case ")":case ",":
				level=7;break;
			default:level=0;
			}
		}
		
		@Override
		public String toString()
		{
			return element+","+level;
		}
		
		public String elementToString()
		{
			return element;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj==null || !obj.getClass().getName().equals(this.getClass().getName()))
				return false;
			return(((Token)obj).element.equals(this.element) && ((Token)obj).level==this.level);
				
		}
		
		
	}
	
	public static class FormulaTokens extends ArrayList<Token> implements List<Token>
	{
		private static final long serialVersionUID = 56L;
		public FormulaTokens()
		{
			super();
		}
		
		@Override
		public boolean equals(Object o) {
			
			
			if(!(o!=null && o.getClass().getName().equals(this.getClass().getName())))
				{return false;}
			if(((FormulaTokens)o).size()!=this.size())
				return false;
			int i;
			for(i=0;i<this.size();i++)
			{
				if(!(((FormulaTokens)o).get(i).equals(this.get(i))))
					return false;
				
			}
			if(i==this.size())
				return true;
			return false;
		}
		
		@Override
		public String toString()
		{
			String tokensString="";
			for(Token e:this)
				tokensString+=e.toString()+"\n";
			return tokensString;
		}

		
		static public FormulaTokens Tokenize(String formula) throws UnexpectedCharacterException, UnexpectedEOLException
		{
			FormulaTokens formulaTokens= new FormulaTokens();
			
			if(formula.length()==0)
			{
				throw new UnexpectedEOLException("Formula is empty");
			}
			for(int i=0;i<formula.length();i++)
			{
				Character current=formula.charAt(i);
				if(Character.isDigit(current)|| current=='.')
				{
					int j;
					for(j=1;i+j<formula.length() && (Character.isDigit(formula.charAt(i+j))|| formula.charAt(i+j)=='.');j++)
					{
					}
					formulaTokens.add(new Token(formula.substring(i, i+j), 0));
					i+=j-1;
				}
				else if(Character.isAlphabetic(current))
				{
					int j;
					for(j=1;i+j<formula.length() && Character.isAlphabetic(formula.charAt(i+j));j++)
					{
					}
					String temp=formula.substring(i, i+j);
					String tempFunc=temp.toLowerCase();
					if(tempFunc.equals("sqrt") ||tempFunc.equals("log") ||tempFunc.equals("ln") || tempFunc.equals("sin") || tempFunc.equals("cos")||tempFunc.equals("tan") ||tempFunc.equals("sinh") ||tempFunc.equals("cosh") ||tempFunc.equals("tanh") ||tempFunc.equals("asin") ||tempFunc.equals("acos") ||tempFunc.equals("atan") ||tempFunc.equals("abs") ||tempFunc.equals("ceil") ||tempFunc.equals("floor")|| tempFunc.equals("sum")||tempFunc.equals("mult"))
					{
						formulaTokens.add(new Token(tempFunc,6));
						i+=j-1;
					}
					else
					{
						int k=i+j;
						for(j=0;k+j<formula.length() && (Character.isAlphabetic(formula.charAt(k+j))||Character.isDigit(formula.charAt(k+j)));j++)
						{
						}
						temp=temp.concat(formula.substring(k,k+j));
						formulaTokens.add(new Token(temp, 0));
						i=k+j-1;
					}
				}
				else if(current=='=' || current=='!' || current=='>' || current=='<')
				{
					if(current=='!' && formula.charAt(i+1)=='=')
					{
						formulaTokens.add(new Token("!=",1));
						i++;
					}
					else if(current=='!'){
						formulaTokens.add(new Token(current.toString(),6));
					}
					else
					{
						formulaTokens.add(new Token(current.toString(),1));
					}
				}
				else if(current=='+' || current=='-')
				{
					formulaTokens.add(new Token(current.toString(),2));
				}
				else if(current=='*' || current=='/' || current=='%')
				{
					formulaTokens.add(new Token(current.toString(),3));
				}
				else if(current=='^')
				{
					formulaTokens.add(new Token(current.toString(),4));
				}
				else if(current=='(' || current==')')
				{
					formulaTokens.add(new Token(current.toString(),7));
				}
                else if(current==',')
                    formulaTokens.add(new Token(current.toString(),7));
				else if(current==' ')
				{
				}
				else
				{
					throw new UnexpectedCharacterException("Unexpected Character : "+current.toString());
				}
			}
			
			return formulaTokens;
		}
		
		//S-> P (B P)*
			//P-> V | "(" S ")" | U P | F "(" S ")" | T "(" E*N*E* "," S "," S ")"
			//B->"=" | "!=" | ">" | "<" | "+" | "-" | "*" | "/" | "^"
			//U-> "-" 
			//F->"log" | "ln" | "sin" | "cos" | "tan" | "sinh" | "cosh" | "tanh" | "asin" | "acos" | "atan" | "sqrt"| "ceil" | "floor"| "abs"
			//T->"sum" | "mult"
            //V->E*N*E* � F | N*.?N*
			//E->[a-z|A-Z]
			//N->[0-9]
			
			private void expect(Token testee, Token e) throws UnexpectedTokenException, UnevenParenthesesException
			{
				if(testee.equals(e))
					return;
				else if(e.equals(new Token(")",7)))
					throw new UnevenParenthesesException("Number of parentheses is uneven");
				else
					throw new UnexpectedTokenException("Unexpected symbol : Expected "+e.elementToString()+", got "+testee.elementToString());
						
			}
			
			private Token s(Iterator<Token> iterator) throws UnexpectedTokenException, UnevenParenthesesException
			{
				p(iterator);
				Token next=(iterator.hasNext()?iterator.next():new Token("End of Line",8));
				while(iterator.hasNext() && (next.level!=0 && next.level!=5 && next.level!=6 && next.level!=7 && next.level!=8))
				{
					p(iterator);
					next=(iterator.hasNext()?iterator.next():new Token("End of Line",8));
				}
				return next;
			}
			
			private void p(Iterator<Token> iterator) throws UnexpectedTokenException, UnevenParenthesesException
			{
				Token next;
				if(iterator.hasNext())
					next=iterator.next();
				else 
					throw new UnexpectedEOLException("Unexpected end of formula");
				if(next.level==0)
					return;
                else if(next.element.equals("sum")||next.element.equals("mult"))
                {
                    if(iterator.hasNext())
                        next=iterator.next();
                    else
                        throw new UnexpectedEOLException();
                    expect(next, new Token("(",7));
                    if(iterator.hasNext())
                        next=iterator.next();
                    else
                        throw new UnexpectedEOLException();
                    if(next.level!=0)
                        throw new UnexpectedTokenException("Unexpected symbol: Expected a variable, got "+next.elementToString());
                    if(iterator.hasNext())
                        next=iterator.next();
                    else
                        throw new UnexpectedEOLException();
                    expect(next,new Token(","));
                    next=s(iterator);
                    expect(next,new Token(","));
                    next=s(iterator);
                    expect(next,new Token(")"));
                    return;

                }
				else if(next.element.equals("("))
				{
					next=s(iterator);
					if(!(next.equals(new Token(")"))))
						throw new UnevenParenthesesException("Open parenthese not closed");
					return;
				}
				else if(next.element.equals("-"))
				{
					next.element="--";
					next.level=5;
					p(iterator);
				}
				else if(next.level==6)
				{
					if(iterator.hasNext())
						next=iterator.next();
					else 
						throw new UnexpectedEOLException();
					expect(next,new Token("(",7));
					next=s(iterator);
					expect(next, new Token(")",7));
				}
				else throw new UnexpectedTokenException("Unexpected symbol : "+next.elementToString());
			}
			
			
			public FormulaTokens checkFormula() throws UnexpectedTokenException, UnevenParenthesesException, UnexpectedEOLException
			{
				Iterator<Token> iter= this.iterator();
				Token next=s(iter);
				if(!next.equals(new Token("End of Line",8)))
					throw new UnexpectedTokenException("Unexpected symbol : "+next.toString());
				return this;
			}
			
			public FormulaTokens checkVariables(String[] expectedVariables) throws UnexpectedVariableException
			{
				loop: for(Token t:this)
				{
					if(t.level==0)
					{
						for(int i=0;i<expectedVariables.length;i++)
						{
							if(t.element.equals(expectedVariables[i]) || t.element.matches("[0-9]*.?[0-9]*") || (t.element.toLowerCase()).equals("e") || (t.element.toLowerCase()).equals("pi") || t.element.equals("r"))
							{
								continue loop;
							}
						}
						throw new UnexpectedVariableException(t.elementToString());
					}
				}
				return this;
			}
			
			public FormulaTokens makePostFix()
			{
				FormulaTokens result=new FormulaTokens();
				Stack<Token> operatorStack = new Stack<Token>();
				
				
				for(Token e : this)
				{
					if(e.level==0)
					{
						result.add(e);
					}
					else if(operatorStack.empty())
					{
						operatorStack.add(e);
					}
					else if(e.element.equals(")"))
					{
						while(!operatorStack.empty()&&!operatorStack.peek().element.equals("("))
						{
							result.add(operatorStack.pop());
						}
						operatorStack.pop();
					}
					else if(operatorStack.peek().level<e.level)
					{
						operatorStack.add(e);
					}
					else 
					{
						while(!operatorStack.empty()&&operatorStack.peek().level>=e.level&&operatorStack.peek().level<7)
						{
							result.add(operatorStack.pop());
						}
					operatorStack.add(e);
					}
				}

				while(!operatorStack.empty())
				{
					result.add(operatorStack.pop());
				}
				
				
				return result;
			}
	}
	
	/*
	 * BEGINNING OF PUBLIC FUNCTIONS
	 * */
	public RuntimeFormula()
	{
		variables=new HashMap<String, Double>();
		catchesArithmeticExceptions=true;
	}
	
	public RuntimeFormula(FormulaElement root, Map<String, Double> variables)
	{
		this.variables=variables;
		formula=new FormulaTree(root);
		catchesArithmeticExceptions=true;
	}
	
	public Map<String, Double> getAllVars()
	{
		return variables;
	}
	
	public void putVar(String variable, double value)
	{
		variables.put(variable,value);
	}
	
	public double getVar(String variable)
	{
		return variables.get(variable);
	}
	
	public void setFormula(String formula, String[] expectedVariables) throws UnexpectedCharacterException, UnexpectedTokenException, UnevenParenthesesException, UnexpectedVariableException, UnexpectedEOLException
	{
		this.formula= new FormulaTree(FormulaTokens.Tokenize(formula).checkFormula().checkVariables(expectedVariables).makePostFix());
	}
	
	public void checkFormula(String formula, String[] expectedVariables) throws UnexpectedCharacterException, UnexpectedTokenException, UnevenParenthesesException, UnexpectedVariableException, UnexpectedEOLException
	{
		FormulaTokens.Tokenize(formula).checkFormula().checkVariables(expectedVariables);
	}
	
	public double calcValue() throws UnexpectedVariableException
	{
		return formula.calcValue();
	}
	
	public int calcValueInt() throws UnexpectedVariableException
	{
		return (int)calcValue();
	}

	public String toString()
	{
		String formulaString;
		if(formula==null)
			formulaString="null";
		else
			formulaString=formula.toString();
		return formulaString+"\n"+variables.entrySet().toString();
	}
	
	public String formulaToString()
	{
		String formulaString;
		if(formula==null)
			formulaString="null";
		else
			formulaString=formula.toString();
		return formulaString;
	}
	
	public void setCatchesArithmeticExceptions(boolean noExceptionMode)
	{
		catchesArithmeticExceptions=noExceptionMode;
		System.out.println(noExceptionMode?"Operations causing numeric exceptions will return 0":"Operations causing numeric exceptions will throw them");
	}
}


