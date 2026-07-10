using System;
using System.Collections.Generic;

using ElaroSolutions.FormulaParser;

namespace ElaroSolutions.FormulaParser
{



    public class Parser
    {
        public const int _EOF = 0;
        public const int _variable = 1;
        public const int _data = 2;
        public const int _sin = 3;
        public const int _cos = 4;
        public const int _tan = 5;
        public const int _asin = 6;
        public const int _acos = 7;
        public const int _atan = 8;
        public const int _sinh = 9;
        public const int _cosh = 10;
        public const int _tanh = 11;
        public const int _log = 12;
        public const int _ln = 13;
        public const int _sqrt = 14;
        public const int _ceil = 15;
        public const int _floor = 16;
        public const int _abs = 17;
        public const int _sum = 18;
        public const int _mult = 19;
        public const int _number = 20;
        public const int _comp = 21;
        public const int _plus = 22;
        public const int _minus = 23;
        public const int _muldivmod = 24;
        public const int _exp = 25;
        public const int _comma = 26;
        public const int _leftparen = 27;
        public const int _rightparen = 28;
        public const int _leftbrack = 29;
        public const int _rightbrack = 30;
        public const int _colon = 31;
        public const int maxT = 32;

        const bool _T = true;
        const bool _x = false;
        const int minErrDist = 2;

        public Scanner scanner;
        public Errors errors;

        public Token t;    // last recognized token
        public Token la;   // lookahead token
        int errDist = minErrDist;

        private Dictionary<string, double> variables;
        private readonly IDataLists data;


        public Parser(Scanner scanner, ref Dictionary<string, double> variables, IDataLists data)
        {
            this.scanner = scanner;
            this.variables = variables;
            this.data = data;
            errors = new Errors();
        }

        void SynErr(int n)
        {
            if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
            errDist = 0;
        }

        public void SemErr(string msg)
        {
            if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
            errDist = 0;
        }

        void Get()
        {
            for (; ; )
            {
                t = la;
                la = scanner.Scan();
                if (la.kind <= maxT) { ++errDist; break; }

                la = t;
            }
        }

        void Expect(int n)
        {
            if (la.kind == n) Get(); else { SynErr(n); }
        }

        bool StartOf(int s)
        {
            return set[s, la.kind];
        }

        Node DARFormula()
        {
            Formula(out Node e);
            return e;
        }

        void Formula(out Node e)
        {
            BinaryFunctions op;
            Expression(out e);
            while (la.kind == 21)
            {
                Get();
                switch (t.val)
                {
                    case "=": op = BinaryFunctions.Equals; break;
                    case "!=": op = BinaryFunctions.Unequals; break;
                    case "<": op = BinaryFunctions.Lesser; break;
                    case ">": op = BinaryFunctions.Greater; break;
                    default: op = BinaryFunctions.Undefined; SynErr(21); break;
                }

                Expression(out Node e2);
                e = BinaryNode.BinaryNodeConstructor(e, e2, op);
            }
        }

        void Expression(out Node e)
        {
            BinaryFunctions op;
            Term(out e);
            while (la.kind == 22 || la.kind == 23)
            {
                if (la.kind == 22)
                {
                    Get();
                    op = BinaryFunctions.Plus;
                }
                else
                {
                    Get();
                    op = BinaryFunctions.Minus;
                }
                Term(out Node e2);
                e = BinaryNode.BinaryNodeConstructor(e, e2, op);
            }
        }

        void Term(out Node e)
        {
            BinaryFunctions op;
            Factor(out e);
            while (la.kind == 24)
            {
                Get();
                switch (t.val)
                {
                    case "*": op = BinaryFunctions.Times; break;
                    case "/": op = BinaryFunctions.Divide; break;
                    case "%": op = BinaryFunctions.Modulo; break;
                    default: op = BinaryFunctions.Undefined; SynErr(24); break;
                }

                Factor(out Node e2);
                e = BinaryNode.BinaryNodeConstructor(e, e2, op);
            }
        }

        void Factor(out Node e)
        {
            PossiblyNegatedOperand(out e);
            while (la.kind == 25)
            {
                Get();
                PossiblyNegatedOperand(out Node e2);
                e = new ExponentNode(e, e2);
            }
        }

        void PossiblyNegatedOperand(out Node e)
        {
            bool isNegated = false;
            if (la.kind == 23)
            {
                Get();
                isNegated = true;
            }
            Operand(out e);
            if (isNegated) { e = new NegateNode(e); }
        }

        void Operand(out Node e)
        {
            string countingVariable;
            if (la.kind == 1 || la.kind == 2 || la.kind == 20)
            {
                Quantity(out e);
            }
            else if (StartOf(1))
            {
                Func(out UnaryFunctions op);
                Expect(27);
                Formula(out e);
                Expect(28);
                e = UnaryNode.UnaryNodeConstructor(e, op);
            }
            else if (la.kind == 18 || la.kind == 19)
            {
                Func3(out TernaryFunctions op3);
                Expect(27);
                Expect(1);
                countingVariable = t.val;
                Expect(26);
                Formula(out Node lim);
                Expect(26);
                Formula(out e);
                Expect(28);
                e = TernaryNode.TernaryNodeConstructor(countingVariable, lim, e, ref variables, op3);
            }
            else if (la.kind == 27)
            {
                Get();
                Formula(out e);
                Expect(28);
            }
            else { e = null; SynErr(33); }
        }

        void Quantity(out Node e)
        {
            if (la.kind == 2)
            {
                Data(out e);
            }
            else if (la.kind == 1)
            {
                Get();
                e = new VariableNode(t.val, ref variables);
            }
            else if (la.kind == 20)
            {
                Get();
                e = new ValueNode(Double.Parse(t.val));
            }
            else { e = null; SynErr(34); }
        }

        void Func(out UnaryFunctions op)
        {
            switch (la.kind)
            {
                case 3:
                    {
                        Get();
                        break;
                    }
                case 4:
                    {
                        Get();
                        break;
                    }
                case 5:
                    {
                        Get();
                        break;
                    }
                case 6:
                    {
                        Get();
                        break;
                    }
                case 7:
                    {
                        Get();
                        break;
                    }
                case 8:
                    {
                        Get();
                        break;
                    }
                case 9:
                    {
                        Get();
                        break;
                    }
                case 10:
                    {
                        Get();
                        break;
                    }
                case 11:
                    {
                        Get();
                        break;
                    }
                case 12:
                    {
                        Get();
                        break;
                    }
                case 13:
                    {
                        Get();
                        break;
                    }
                case 14:
                    {
                        Get();
                        break;
                    }
                case 15:
                    {
                        Get();
                        break;
                    }
                case 16:
                    {
                        Get();
                        break;
                    }
                case 17:
                    {
                        Get();
                        break;
                    }
                default: SynErr(35); break;
            }
            op = (UnaryFunctions)t.kind;
        }

        void Func3(out TernaryFunctions op)
        {
            if (la.kind == 18)
            {
                Get();
            }
            else if (la.kind == 19)
            {
                Get();
            }
            else SynErr(36);
            op = (TernaryFunctions)t.kind;
        }

        void Data(out Node e)
        {
            List<Node> indexes = new List<Node>();
            string fieldName = "";
            Expect(2);
            Expect(29);
            Formula(out Node e1);
            Expect(30);
            indexes.Add(e1);
            while (la.kind == 29)
            {
                Get();
                Formula(out Node e2);
                Expect(30);
                indexes.Add(e2);
            }
            if (la.kind == 31)
            {
                Get();
                Expect(1);
                fieldName = t.val;
            }
            e = new DataNode(indexes, fieldName, data);

        }



        public Node Parse()
        {
            Node e;
            la = new Token
            {
                val = ""
            };
            Get();
            e = DARFormula();
            Expect(0);
            if (errors.count == 0)
            { return e; }
            else
            { return null; }
        }

        static readonly bool[,] set = {
        {_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
        {_x,_x,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x}

    };
    } // end Parser


    public class Errors
    {
        public int count = 0;                                    // number of errors detected
        public System.IO.TextWriter errorStream = Console.Out;   // error messages go to this stream
        public string errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
        public string errorsString;
        public virtual void SynErr(int line, int col, int n)
        {
            string s = n switch
            {
                0 => "EOL expected",
                1 => "variable expected",
                2 => "data expected",
                3 => "sin expected",
                4 => "cos expected",
                5 => "tan expected",
                6 => "asin expected",
                7 => "acos expected",
                8 => "atan expected",
                9 => "sinh expected",
                10 => "cosh expected",
                11 => "tanh expected",
                12 => "log expected",
                13 => "ln expected",
                14 => "sqrt expected",
                15 => "ceil expected",
                16 => "floor expected",
                17 => "abs expected",
                18 => "sum expected",
                19 => "mult expected",
                20 => "number expected",
                21 => "comp expected",
                22 => "plus expected",
                23 => "minus expected",
                24 => "muldivmod expected",
                25 => "exp expected",
                26 => "comma expected",
                27 => "leftparen expected",
                28 => "rightparen expected",
                29 => "leftbrack expected",
                30 => "rightbrack expected",
                31 => "colon expected",
                32 => "??? expected",
                33 => "invalid Operand",
                34 => "invalid Quantity",
                35 => "invalid Func",
                36 => "invalid Func3",
                _ => "error " + n,
            };
            errorStream.WriteLine(errMsgFormat, line, col, s);
            errorsString = col.ToString() + " : " + s;
            count++;
        }

        public virtual void SemErr(int line, int col, string s)
        {
            errorStream.WriteLine(errMsgFormat, line, col, s);
            count++;
        }

        public virtual void SemErr(string s)
        {
            errorStream.WriteLine(s);
            count++;
        }

        public virtual void Warning(int line, int col, string s)
        {
            errorStream.WriteLine(errMsgFormat, line, col, s);
        }

        public virtual void Warning(string s)
        {
            errorStream.WriteLine(s);
        }
    } // Errors


    public class FatalError : Exception
    {
        public FatalError(string m) : base(m) { }
    }
}