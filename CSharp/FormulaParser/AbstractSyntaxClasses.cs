using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using ElaroSolutions.UnitTests.FormulaParser;

namespace ElaroSolutions.FormulaParser
{

    public interface IDataLists
    {
        public double GetDataField(int[] indexes, string name);        //If name is empty string, it must still return a value
    }

    public class BadFormulaException : Exception
    {
        public BadFormulaException(string m) : base(m) { }
    }
    public class UnexpectedVariableException : Exception
    {
        public UnexpectedVariableException(string m) : base(m) { }
    }

    public class ResultBeyondBoundsException : Exception
    {
        public ResultBeyondBoundsException(string m) : base(m) { }
    }

    public class FieldNotFoundException : Exception
    {
        public FieldNotFoundException(string m) : base(m) { }
    }

    enum NodeType
    {
        Simple, Unary, Binary, Ternary
    }


    enum UnaryFunctions
    {
        Sin = 3, Cos, Tan, Asin, Acos, Atan, Sinh, Cosh, Tanh, Log, Ln, Sqrt, Ceil, Floor, Abs, Negate, Undefined
    }
    // sin|cos|tan|asin|acos|atan|sinh|cosh|tanh|log|ln|sqrt|ceil|floor|abs

    enum BinaryFunctions
    {
        Equals, Unequals, Greater, GreaterEquals, Lesser, LesserEquals, Plus, Minus, Times, Divide, Modulo, Exponent, Undefined

    }

    enum TernaryFunctions
    {
        Sum = 18, Mult, Undefined
    }

    public abstract class Node
    {
        public const double PRECISION = 0.000005;
        internal abstract double CalcValue();

        public abstract override string ToString();
        internal abstract NodeType GetNodeType();
    }
        

    internal abstract class LeafNode : Node
    {
        internal abstract override double CalcValue();

        public abstract override string ToString();

        internal override NodeType GetNodeType()
        {
            return NodeType.Simple;
        }
    }

    internal class ValueNode : LeafNode
    {
        private readonly double _value;
        internal ValueNode(double value)
        {
            this._value = value;
        }

        internal override double CalcValue()
        {
            return _value;
        }

        public override string ToString()
        {
            return _value.ToString(CultureInfo.CurrentCulture);
        }
    }

    internal class VariableNode : LeafNode
    {
        private readonly Dictionary<string, double> _variables;
        private readonly string _variable;

        internal VariableNode(string variable, ref Dictionary<string, double> variables)
        {
            _variable = variable;
            this._variables = variables;

        }

        internal override double CalcValue()
        {
            try
            {
                return _variables[_variable];
            }
            catch (KeyNotFoundException)
            {
                throw new UnexpectedVariableException("Uninitialized variable: " + _variable);
            }

        }

        public override string ToString()
        {
            return _variable;
        }
    }

    internal class RandomVariableNode : LeafNode
    {
        readonly Random _random;

        internal RandomVariableNode()
        {
            _random = new Random();
        }

        public override string ToString()
        {
            return "r";
        }

        internal override double CalcValue()
        {
            return _random.NextDouble();
        }
    }

    internal class DataNode : LeafNode
    {
        readonly IDataLists _data;
        internal List<Node> DataIndexes { get; }
        readonly int _dataDepth;
        readonly string _dataFieldName;
        private readonly int[] _indexes;

        internal DataNode(List<Node> dataSelectors, string fieldName, IDataLists data)
        {
            this._data = data;
            this.DataIndexes = dataSelectors;
            this._dataDepth = this.DataIndexes.Count;
            this._dataFieldName = fieldName;
            this._indexes = new int[_dataDepth];
        }

        internal override double CalcValue()
        {
            for (int i = 0; i < _dataDepth; ++i)
            {
                double index = DataIndexes[i].CalcValue();
                _indexes[i] = Double.IsNaN(index)||Double.IsInfinity(index) ? throw new ResultBeyondBoundsException("Formula " + DataIndexes[i].ToString() + " is not always a finite number") : (int)index;
            }
            return (double)_data.GetDataField(_indexes, _dataFieldName);
        }
        public override string ToString()
        {
            string ofThis = "data";
            for (int index = 0; index < DataIndexes.Count; ++index)
            {
                ofThis += "[" + DataIndexes[index].ToString() + "]";
            }
            if (_dataFieldName != "")
            {
                ofThis += ":" + _dataFieldName;
            }
            return ofThis;
        }

        public string FieldToString()
        {
            return _dataFieldName;
        }
    }

    internal abstract class UnaryNode : Node
    {
        internal Node operand;
        internal UnaryFunctions function;

        internal override double CalcValue()
        {
            return function switch
            {
                UnaryFunctions.Sin => Math.Sin(operand.CalcValue()),
                UnaryFunctions.Cos => Math.Cos(operand.CalcValue()),
                UnaryFunctions.Tan => Math.Tan(operand.CalcValue()),
                UnaryFunctions.Asin => Math.Asin(operand.CalcValue()),
                UnaryFunctions.Acos => Math.Acos(operand.CalcValue()),
                UnaryFunctions.Atan => Math.Atan(operand.CalcValue()),
                UnaryFunctions.Sinh => Math.Sinh(operand.CalcValue()),
                UnaryFunctions.Cosh => Math.Cosh(operand.CalcValue()),
                UnaryFunctions.Tanh => Math.Tanh(operand.CalcValue()),
                UnaryFunctions.Abs => Math.Abs(operand.CalcValue()),
                UnaryFunctions.Ceil => Math.Ceiling(operand.CalcValue()),
                UnaryFunctions.Floor => Math.Floor(operand.CalcValue()),
                UnaryFunctions.Ln => Math.Log(operand.CalcValue()),
                UnaryFunctions.Log => Math.Log10(operand.CalcValue()),
                UnaryFunctions.Sqrt => Math.Sqrt(operand.CalcValue()),
                UnaryFunctions.Negate => -(operand.CalcValue()),
                _ => throw new Exception("Unsupported unary function: " + function)
            };
        }
        internal static UnaryNode UnaryNodeConstructor(Node operand, UnaryFunctions op)
        {
            return op switch
            {
                UnaryFunctions.Sin => new SinNode(operand),
                UnaryFunctions.Cos => new CosNode(operand),
                UnaryFunctions.Tan => new TanNode(operand),
                UnaryFunctions.Asin => new AsinNode(operand),
                UnaryFunctions.Acos => new AcosNode(operand),
                UnaryFunctions.Atan => new AtanNode(operand),
                UnaryFunctions.Sinh => new SinhNode(operand),
                UnaryFunctions.Cosh => new CoshNode(operand),
                UnaryFunctions.Tanh => new TanhNode(operand),
                UnaryFunctions.Abs => new AbsNode(operand),
                UnaryFunctions.Ceil => new CeilNode(operand),
                UnaryFunctions.Floor => new FloorNode(operand),
                UnaryFunctions.Ln => new LnNode(operand),
                UnaryFunctions.Log => new LogNode(operand),
                UnaryFunctions.Sqrt => new SqrtNode(operand),
                UnaryFunctions.Negate => new NegateNode(operand),
                _ => throw new Exception("Unsupported unary function: " + op),
            };
        }

        internal override NodeType GetNodeType()
        {
            return NodeType.Unary;
        }
    }

    internal class SinNode : UnaryNode
    {
        internal SinNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Sin;
        }

        /*internal override double CalcValue()
        {
            return Math.Sin(operand.CalcValue());
        }*/

        public override string ToString()
        {
            return "sin(" + operand.ToString() + ")";
        }
    }
    internal class CosNode : UnaryNode
    {
        internal CosNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Cos;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Cos(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "cos(" + operand.ToString() + ")";
        }
    }
    internal class TanNode : UnaryNode
    {
        internal TanNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Tan;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Tan(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "tan(" + operand.ToString() + ")";
        }
    }
    internal class AsinNode : UnaryNode
    {
        internal AsinNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Asin;
        }


        // internal override double CalcValue()
        // {
        //     return Math.Asin(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "asin(" + operand.ToString() + ")";
        }
    }
    internal class AcosNode : UnaryNode
    {
        internal AcosNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Acos;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Acos(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "acos(" + operand.ToString() + ")";
        }
    }
    internal class AtanNode : UnaryNode
    {
        internal AtanNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Atan;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Atan(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "atan(" + operand.ToString() + ")";
        }
    }
    internal class SinhNode : UnaryNode
    {
        internal SinhNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Sinh;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Sinh(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "sinh(" + operand.ToString() + ")";
        }
    }
    internal class CoshNode : UnaryNode
    {
        internal CoshNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Cosh;
        }


        // internal override double CalcValue()
        // {
        //     return Math.Cosh(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "cosh(" + operand.ToString() + ")";
        }
    }
    internal class TanhNode : UnaryNode
    {
        internal TanhNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Tanh;

        }


        // internal override double CalcValue()
        // {
        //     return Math.Tanh(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "tanh(" + operand.ToString() + ")";
        }
    }
    internal class CeilNode : UnaryNode
    {
        internal CeilNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Ceil;
        }


        // internal override double CalcValue()
        // {
        //     return Math.Ceiling(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "ceil(" + operand.ToString() + ")";
        }
    }
    internal class FloorNode : UnaryNode
    {
        internal FloorNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Floor;

        }


        // internal override double CalcValue()
        // {
        //     return Math.Floor(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "floor(" + operand.ToString() + ")";
        }
    }
    internal class LogNode : UnaryNode
    {
        internal LogNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Log;
        }


        // internal override double CalcValue()
        // {
        //     return Math.Log10(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "log(" + operand.ToString() + ")";
        }
    }
    internal class LnNode : UnaryNode
    {
        internal LnNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Ln;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Log(operand.CalcValue(), Math.E);
        // }

        public override string ToString()
        {
            return "ln(" + operand.ToString() + ")";
        }
    }
    internal class AbsNode : UnaryNode
    {
        internal AbsNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Abs;

        }


        // internal override double CalcValue()
        // {
        //     return Math.Abs(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "abs(" + operand.ToString() + ")";
        }

    }
    internal class SqrtNode : UnaryNode
    {
        internal SqrtNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Sqrt;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Sqrt(operand.CalcValue());
        // }

        public override string ToString()
        {
            return "sqrt(" + operand.ToString() + ")";
        }
    }
    internal class NegateNode : UnaryNode
    {
        internal NegateNode(Node operand)
        {
            this.operand = operand;
            this.function = UnaryFunctions.Negate;
        }

        // internal override double CalcValue()
        // {
        //     return -operand.CalcValue();
        // }

        public override string ToString()
        {
            return "-(" + operand.ToString() + ")";
        }
    }

    internal abstract class BinaryNode : Node
    {
        internal Node preoperand, postoperand;
        internal BinaryFunctions function;

        internal override double CalcValue()
        {
            double result;
            switch(function)
            {
                case BinaryFunctions.Equals: 
                {
                    double cachepre = preoperand.CalcValue(), 
                        cachepost = postoperand.CalcValue();
                    if (Double.IsNaN(cachepre) || Double.IsNaN(cachepost))
                        return 0;
                    return Math.Abs(this.preoperand.CalcValue() - this.postoperand.CalcValue()) < PRECISION ? 1 : 0;
                };break;
                case BinaryFunctions.Unequals: 
                {
                    double cachepre = preoperand.CalcValue(), 
                        cachepost = postoperand.CalcValue();
                    if (Double.IsNaN(cachepre) || Double.IsNaN(cachepost))
                        return 1;
                    return Math.Abs(cachepre - cachepost) < PRECISION ? 0 : 1;
                };break;
                case BinaryFunctions.Greater: 
                {
                    double cachepre = this.preoperand.CalcValue(), 
                        cachepost = this.postoperand.CalcValue();
                    return (Math.Abs(cachepre - cachepost)) > PRECISION ? (cachepre > cachepost ? 1 : 0) : 0;
                };break;
                case BinaryFunctions.GreaterEquals:
                {
                    double cachepre = this.preoperand.CalcValue(), 
                        cachepost = this.postoperand.CalcValue();
                    return (Math.Abs(cachepre - cachepost)) > PRECISION ? 
                                    (cachepre > cachepost ? 1 : 0) : 1;
                };break;
                case BinaryFunctions.Lesser: 
                {
                    double cachepre = this.preoperand.CalcValue(), 
                        cachepost = this.postoperand.CalcValue();
                    return (Math.Abs(cachepre - cachepost)) > PRECISION ? 
                                    (cachepre < cachepost ? 1 : 0) : 0;
                };break;
                case BinaryFunctions.LesserEquals:
                {
                    double cachepre = this.preoperand.CalcValue(), 
                        cachepost = this.postoperand.CalcValue();
                    return (Math.Abs(cachepre - cachepost)) > PRECISION ? 
                                    (cachepre < cachepost ? 1 : 0) : 1;
                };break;
                case BinaryFunctions.Exponent: 
                {
                    return Math.Pow(this.preoperand.CalcValue(), this.postoperand.CalcValue());
                };break;
                case BinaryFunctions.Plus: 
                {
                    return this.preoperand.CalcValue() + this.postoperand.CalcValue();
                };break;
                case BinaryFunctions.Minus:
                {
                    return this.preoperand.CalcValue() - this.postoperand.CalcValue();
                };break;
                case BinaryFunctions.Times: 
                {
                    return this.preoperand.CalcValue() * this.postoperand.CalcValue();
                };break;
                case BinaryFunctions.Divide: 
                {
                    return this.preoperand.CalcValue() / this.postoperand.CalcValue();
                };break;
                case BinaryFunctions.Modulo: 
                {
                    return this.preoperand.CalcValue() % this.postoperand.CalcValue();
                };break;
                default: throw new Exception("Unsupported binary operation: " + function);
            };
        }

        public abstract override string ToString();

        internal static BinaryNode BinaryNodeConstructor(Node pre, Node post, BinaryFunctions op)
        {
            return op switch
            {
                BinaryFunctions.Equals => new EqualsNode(pre, post),
                BinaryFunctions.Unequals => new UnequalsNode(pre, post),
                BinaryFunctions.Greater => new GreaterNode(pre, post),
                BinaryFunctions.Lesser => new LesserNode(pre, post),
                BinaryFunctions.Exponent => new ExponentNode(pre, post),
                BinaryFunctions.Plus => new PlusNode(pre, post),
                BinaryFunctions.Minus => new MinusNode(pre, post),
                BinaryFunctions.Times => new TimesNode(pre, post),
                BinaryFunctions.Divide => new DivideNode(pre, post),
                BinaryFunctions.Modulo => new ModuloNode(pre, post),
                _ => throw new Exception("Unsupported binary operation: " + op),
            };
        }

        internal override NodeType GetNodeType()
        {
            return NodeType.Binary;
        }
    }

    internal class EqualsNode : BinaryNode
    {
        internal EqualsNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     double cachepre = preoperand.CalcValue(), cachepost = postoperand.CalcValue();
        //     if (Double.IsNaN(cachepre) || Double.IsNaN(cachepost))
        //     { return 0; }
        //     return Math.Abs(this.preoperand.CalcValue() - this.postoperand.CalcValue()) < PRECISION ? 1 : 0;
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "=" + this.postoperand.ToString() + ")";
        }
    }

    internal class UnequalsNode : BinaryNode
    {
        internal UnequalsNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     double cachepre = preoperand.CalcValue(), cachepost = postoperand.CalcValue();
        //     if (Double.IsNaN(cachepre) || Double.IsNaN(cachepost))
        //     { return 1; }
        //     return Math.Abs(cachepre - cachepost) < PRECISION ? 0 : 1;
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "!=" + this.postoperand.ToString() + ")";
        }
    }

    internal class GreaterNode : BinaryNode
    {
        internal GreaterNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     double cachepre = this.preoperand.CalcValue(), cachepost = this.postoperand.CalcValue();

        //     return (Math.Abs(cachepre - cachepost)) > PRECISION ? (cachepre > cachepost ? 1 : 0) : 0;
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + ">" + this.postoperand.ToString() + ")";
        }
    }

    internal class LesserNode : BinaryNode
    {
        internal LesserNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     double cachepre = this.preoperand.CalcValue(), cachepost = this.postoperand.CalcValue();

        //     return (Math.Abs(cachepre - cachepost)) > PRECISION ? (cachepre < cachepost ? 1 : 0) : 0;
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "<" + this.postoperand.ToString() + ")";
        }
    }

    internal class PlusNode : BinaryNode
    {
        internal PlusNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return this.preoperand.CalcValue() + this.postoperand.CalcValue();
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "+" + this.postoperand.ToString() + ")";
        }
    }

    internal class MinusNode : BinaryNode
    {
        internal MinusNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return this.preoperand.CalcValue() - this.postoperand.CalcValue();
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "-" + this.postoperand.ToString() + ")";
        }
    }

    internal class TimesNode : BinaryNode
    {
        internal TimesNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return this.preoperand.CalcValue() * this.postoperand.CalcValue();
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "*" + this.postoperand.ToString() + ")";
        }
    }

    internal class DivideNode : BinaryNode
    {
        internal DivideNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return this.preoperand.CalcValue() / this.postoperand.CalcValue();
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "/" + this.postoperand.ToString() + ")";
        }
    }

    internal class ModuloNode : BinaryNode
    {
        internal ModuloNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return this.preoperand.CalcValue() % this.postoperand.CalcValue();
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "%" + this.postoperand.ToString() + ")";
        }
    }

    internal class ExponentNode : BinaryNode
    {
        internal ExponentNode(Node pre, Node post)
        {
            this.preoperand = pre;
            this.postoperand = post;
        }

        // internal override double CalcValue()
        // {
        //     return Math.Pow(this.preoperand.CalcValue(), this.postoperand.CalcValue());
        // }

        public override string ToString()
        {
            return "(" + this.preoperand.ToString() + "^" + this.postoperand.ToString() + ")";
        }
    }

    internal abstract class TernaryNode : Node
    {
        protected Dictionary<string, double> variables;
        private VariableNode counter;
        private Node limit, formula;

        private TernaryFunctions function;

        internal override double CalcValue()
        {
            switch (function)
            {
                case TernaryFunctions.Sum:
                {
                    double lim = limit.CalcValue();
                    double result = 0;
                    for (double i = 1; i <= lim; ++i)
                    {
                        this.variables[counter.ToString()] = i;
                        result += formula.CalcValue();
                    }
                    variables.Remove(this.counter.ToString());
                    return result;
                }; break;
                case TernaryFunctions.Mult:
                {
                    double lim = this.limit.CalcValue();
                    double result = 1;
                    for (double i = 1; i <= lim; ++i)
                    {
                        this.variables[this.counter.ToString()] = i;
                        result *= formula.CalcValue();
                    }
                    variables.Remove(this.counter.ToString());
                    return result;
                }; break;
                default: throw new Exception("Unsupported ternary operation: "+ function);
            }
        }
        public abstract override string ToString();

        internal TernaryNode(string countingVarName, Node limit, Node formula, ref Dictionary<string, double> variables)
        {
            if (variables.ContainsKey(countingVarName))
            {
                throw new UnexpectedVariableException("Invalid counter variable: " + countingVarName + " already in use");
            }
            counter = new VariableNode(countingVarName, ref variables);
            this.variables = variables;
            this.limit = limit;
            this.formula = formula;
        }

        internal static TernaryNode TernaryNodeConstructor(string counter, Node limit, Node formula, ref Dictionary<string, double> variables, TernaryFunctions op)
        {
            return op switch
            {
                TernaryFunctions.Mult => new MultNode(counter, limit, formula, ref variables),
                TernaryFunctions.Sum => new SumNode(counter, limit, formula, ref variables),
                _ => throw new Exception("Unsupported Ternary Operation " + op),
            };
        }

        internal override NodeType GetNodeType()
        {
            return NodeType.Ternary;
        }

    }

    internal class SumNode : TernaryNode
    {
        internal SumNode(string counter, Node limit, Node formula, ref Dictionary<string, double> variables)
        : base(counter, limit, formula, ref variables)
        { }

        // internal override double CalcValue()
        // {
        //     double lim = limit.CalcValue();
        //     double result = 0;
        //     for (double i = 1; i <= lim; ++i)
        //     {
        //         this.variables[counter.ToString()] = i;
        //         result += formula.CalcValue();
        //     }
        //     variables.Remove(this.counter.ToString());
        //     return result;
        // }

        public override string ToString()
        {
            return "sum(" + counter.ToString() + "," + this.limit.ToString() + "," + this.formula.ToString() + ")";
        }

    }


    internal class MultNode : TernaryNode
    {
        internal MultNode(string counter, Node limit, Node formula, ref Dictionary<string, double> variables)
        : base(counter, limit, formula, ref variables)
        { }

        // internal override double CalcValue()
        // {
        //     double lim = this.limit.CalcValue();
        //     double result = 1;
        //     for (double i = 1; i <= lim; ++i)
        //     {
        //         this.variables[this.counter.ToString()] = i;
        //         result *= formula.CalcValue();
        //     }
        //     variables.Remove(this.counter.ToString());
        //     return result;
        // }

        public override string ToString()
        {
            return "mult(" + counter.ToString() + "," + this.limit.ToString() + "," + this.formula.ToString() + ")";
        }

    }
}