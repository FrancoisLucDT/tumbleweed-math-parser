using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

using ElaroSolutions.FormulaParser;


namespace ElaroSolutions.FormulaParser
{
    public class Formula
    {
        protected Dictionary<string, double> Variables;
        private readonly HashSet<string> _allowedVariables;
        private readonly HashSet<string> _allowedFields;
        private IDataLists _data;
        private Parser _parser;
        private Scanner _scanner;
        private Node _root;
        public string SyntaxError { get; private set; }
        public Boolean ThrowsExceptionsFromCalcValue { get; set; }

        public Formula()
        {
            Variables = new Dictionary<string, double>();
            _allowedFields = new HashSet<string>();
            _allowedVariables = new HashSet<string>() { "r" };
            ThrowsExceptionsFromCalcValue = false;
            _root = null;
            AddVariable("E", Math.E);
            AddVariable("PI", Math.PI);
            AddVariable("PHI", (1.0 + Math.Sqrt(5.0)) / 2.0);
        }

        public void SetUpFormula(IEnumerable<string> allowedVariables, IEnumerable<string> allowedFields, IDataLists data)
        {
            foreach (var variable in allowedVariables)
            {
                AddVariable(variable, 0.0);
            }

            foreach (var field in allowedFields)
            {
                AddField(field);
            }

            SetData(data);
        }

        public void SetData(IDataLists data)
        {
            this._data = data;
        }

        public void AddVariable(string variableName, double initialValue)
        {
            _allowedVariables.Add(variableName);
            Variables.Add(variableName, initialValue); 
        }

        public void AddField(string fieldName)
        {
            _allowedFields.Add(fieldName);
        }

        public virtual void SetFormula(string formula)
        {
            _root = null;
            SyntaxError = "";
            byte[] formulaInBytes = Encoding.UTF8.GetBytes(formula);
            MemoryStream formulaStream = new(formulaInBytes);
            _scanner = new Scanner(formulaStream);
            _parser = new Parser(_scanner, ref Variables, _data);
            _root = _parser.Parse();
            if (_root != null)
            {
                try { this.CheckVariablesAndFields(); }
                catch (UnexpectedVariableException u)
                { SyntaxError = u.Message; }

            }
            else SyntaxError = _parser.errors.errorsString;
        }

        private void CheckNode(Node n)
        {
            if (n is VariableNode node1)
            {
                if (_allowedVariables.Any(variable => String.Compare(node1.ToString(), variable, StringComparison.Ordinal) == 0))
                {
                    return;
                }
                throw new UnexpectedVariableException("Unrecognized variable: " + n.ToString());
            }
            else if (n is DataNode node)
            {
                foreach (Node n1 in node.DataIndexes)
                {
                    CheckNode(n1);
                }

                if (_allowedFields.Any(field =>
                    String.Compare(node.FieldToString(), field, StringComparison.Ordinal) == 0))
                {
                    return;
                }

                throw new UnexpectedVariableException("Unsupported field: " + node.FieldToString());
            }
            else if (n is UnaryNode unaryNode)
            {
                CheckNode(unaryNode.operand);
                return;
            }
            else if (n is BinaryNode binaryNode)
            {
                CheckNode(binaryNode.preoperand);
                CheckNode(binaryNode.postoperand);
                return;
            }
            else if (n is TernaryNode ternaryNode)
            {
                if (_allowedVariables.Contains(ternaryNode.counter.ToString()))
                {
                    throw new UnexpectedVariableException("Unusable counting variable: " + ternaryNode.counter.ToString() + " already in use");
                }
                else
                {
                    CheckNode(ternaryNode.limit);
                    _allowedVariables.Add(ternaryNode.counter.ToString());
                    CheckNode(ternaryNode.formula);
                    _allowedVariables.Remove(ternaryNode.counter.ToString());
                    return;
                }

            }

        }

        private void CheckVariablesAndFields()
        {
            CheckNode(_root);
        }

        public void UpdateVariable(string varName, double value)
        {
            Variables[varName] = value;
        }

        public double CalculateValue()
        {
            double result;
            if (SyntaxError.Length > 0)
            { throw new InvalidOperationException(); }
            try
            {
                result = _root.CalcValue();
            }
            catch (Exception)
            {
                if (ThrowsExceptionsFromCalcValue)
                {
                    throw;
                }
                else
                {
                    return double.NaN;
                }

            }
            if (Double.IsFinite(result))
                return result;
            else return double.NaN;
        }

        public string FormulaToString()
        {
            return _root.Equals(null) ? "" : _root.ToString();
        }
    }



}