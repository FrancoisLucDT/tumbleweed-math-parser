#ifndef ElaroSolutions_DARFormula_NODES_CPP
#define ElaroSolutions_DARFormula_NODES_CPP

#include "Nodes.h"

#include <utility>

namespace ElaroSolutions { namespace DARFormula {

    JavalikeRandomNumberGenerator * JavalikeRandomNumberGenerator::_r = nullptr;

    double JavalikeRandomNumberGenerator::generateNumber()
    {
        return (double)(*getJRNG())()/ (double) (JavalikeRandomNumberGenerator::max()+1.0);
    }

    JavalikeRandomNumberGenerator *JavalikeRandomNumberGenerator::getJRNG() {
        if(_r == nullptr)
        {
            _r = new JavalikeRandomNumberGenerator();
        }
        return _r;
    }

    Node::~Node() = default;

    NodeType SimpleNode::getType()
    {
        return Simple;
    }

    SimpleNode::~SimpleNode() = default;

    ValueNode::ValueNode(double value)
    {
        _value = value;
    }

    double ValueNode::calcValue()
    {
        return _value;
    }

    std::string ValueNode::toText()
    {
        return std::to_string(_value);
    }

        ValueNode::~ValueNode()
        = default;

        VariableNode::VariableNode(std::string variable, std::unordered_map<std::string, double> *variables)
    {
        _variable = std::move(variable);
        _variables = variables;
    }

    double VariableNode::calcValue()
    {
        double value=NAN;
            try
            {
                value = _variables->at(_variable);
            }
            catch(std::out_of_range&)
            {
                std::string error= std::string().append("Variable ").append(_variable).
                        append(" has no value");
                throw UninitializedVariable(error);
            }

        return value;
    }

    std::string VariableNode::toText()
    {
        return _variable;
    }

    VariableNode::~VariableNode()
    {
        _variables = nullptr;
    }

        std::unordered_map<std::string, double> *VariableNode::getVariables()
        {
            return _variables;
        }

        DataNode::DataNode(std::vector<ElaroSolutions::DARFormula::Node*> indexes, std::string field)
    {
        _data = nullptr;
        _indexes = std::move(indexes);
        _field = std::move(field);
        _indexTable = new int[_indexes.size()];
    }

    void DataNode::setData(IDataStructure *data)
    {
        _data = data;
    }

    double DataNode::calcValue()
    {
        unsigned int indexQuantity = _indexes.size();
        double value= NAN;

        for(unsigned int i=0;i<indexQuantity;++i)
        {
            _indexTable[i]=(int)std::round(_indexes.at(i)->calcValue());
        }
        value=_data->getValueAt(_indexTable,_field);

        return value;
    }

    std::string DataNode::toText()
    {
        std::string result="data";
        for(auto & _indexe : _indexes)
        {
            result.append("["+_indexe->toText()+"]");
        }
        if(!_field.empty())
        {
            result.append(":"+_field);
        }
        return result;
    }

    std::vector<ElaroSolutions::DARFormula::Node*> DataNode::getIndexes()
    {
        return _indexes;
    }

    std::string DataNode::getField()
    {
        return _field;
    }

    DataNode::~DataNode()
    {
            _data = nullptr;
        for(auto & _index : _indexes)
        {
            delete _index;
        }
        delete[] _indexTable;
    }

    NodeType UnaryNode::getType() {
        return Unary;
    }

    Node *UnaryNode::UnaryNodeConstructor(Node *operand, UnaryFunctions op)
    {
        UnaryNode * construct= nullptr;
        switch(op)
        {
            case Sin: construct = new SinNode(operand);
                break;
            case Cos: construct = new CosNode(operand);
                break;
            case Tan: construct = new TanNode(operand);
                break;
            case Asin: construct = new AsinNode(operand);
                break;
            case Acos: construct = new AcosNode(operand);
                break;
            case Atan: construct = new AtanNode(operand);
                break;
            case Sinh: construct = new SinhNode(operand);
                break;
            case Cosh: construct = new CoshNode(operand);
                break;
            case Tanh: construct = new TanhNode(operand);
                break;
            case Log:  construct = new LogNode(operand);
                break;
            case Ln:  construct = new LnNode(operand);
                break;
            case Sqrt: construct = new SqrtNode(operand);
                break;
            case Ceil: construct = new CeilNode(operand);
                break;
            case Floor: construct = new FloorNode(operand);
                break;
            case Abs:  construct = new AbsNode(operand);
                break;
            case Negate:  construct = new NegateNode(operand);
                break;
            case UUndefined: throw std::runtime_error("Unsupported Unary Node construction");
                break;
        }
        return construct;
    }

        Node *UnaryNode::getOperand()
        {
            return _operand;
        }

        UnaryNode::UnaryNode(Node *operand)
        {
            _operand = operand;
        }

        UnaryNode::~UnaryNode()
        {
            delete _operand;
        }

        Node *BinaryNode::BinaryNodeConstructor(Node *preoperand, Node *postoperand, BinaryFunctions op) {
        BinaryNode* construct = nullptr;
        switch(op)
        {
            case Equals: construct = new EqualsNode(preoperand, postoperand);
                break;
            case Unequals: construct = new UnequalsNode(preoperand,postoperand);
                break;
            case Greater: construct = new GreaterNode(preoperand,postoperand);
                break;
            case Lesser: construct = new LesserNode(preoperand,postoperand);
                break;
            case Plus: construct = new PlusNode(preoperand,postoperand);
                break;
            case Minus: construct = new MinusNode(preoperand,postoperand);
                break;
            case Times: construct = new TimesNode(preoperand,postoperand);
                break;
            case Divide: construct = new DivideNode(preoperand,postoperand);
                break;
            case Modulo: construct = new ModuloNode(preoperand,postoperand);
                break;
            case Exponent: construct = new ExponentNode(preoperand,postoperand);
                break;
            case BUndefined:
                throw std::runtime_error("Unsupported Binary Node construction");
        }
        return construct;
    }

    NodeType BinaryNode::getType() {
        return Binary;
    }

        Node *BinaryNode::getPreOperand()
        {
            return _preoperand;
        }

        Node *BinaryNode::getPostOperand()
        {
            return _postoperand;
        }

        BinaryNode::BinaryNode(Node* preoperand, Node* postoperand)
        {
            _preoperand = preoperand;
            _postoperand = postoperand;
        }

        BinaryNode::~BinaryNode()
        {
            delete _preoperand;
            delete _postoperand;
        }

        NodeType TernaryNode::getType() {
        return Ternary;
    }

    Node *TernaryNode::TernaryNodeConstructor(const std::string& countingVariable, Node *limit, Node *formula, TernaryFunctions op, std::unordered_map<std::string,double> *variables) {
        TernaryNode * construct = nullptr;
        switch(op)
        {
            case Mult:
                construct = new MultNode(countingVariable,limit,formula,variables);
                break;
            case Sum:
                construct = new SumNode(countingVariable,limit,formula,variables);
                break;
            case TUndefined:
                throw std::runtime_error("Unsupported Ternary Node construction");
        }
        return construct;
    }

    VariableNode *TernaryNode::getCounter()
    {
        return _counter;
    }

        Node *TernaryNode::getLimit()
        {
            return _limit;
        }

        Node *TernaryNode::getFormula()
        {
            return _formula;
        }

        TernaryNode::TernaryNode(const std::string &countingVariable, Node *limit, Node *formula,
                                 std::unordered_map<std::string, double> *variables)
        {
            if(variables->count(countingVariable)>0)
                throw BadFormula("Invalid counting variable: "+countingVariable+" already in use");
            _counter = new VariableNode(countingVariable,variables);
            _limit = limit;
            _formula = formula;
        }

        TernaryNode::~TernaryNode()
        {
            delete _limit;
            delete _counter;
            delete _formula;
        }

        std::string RandomVariableNode::toText() {
        return std::string("r");
    }

    double RandomVariableNode::calcValue() {
        return _rng->generateNumber();
    }

    RandomVariableNode::RandomVariableNode() {
        _rng = JavalikeRandomNumberGenerator::getJRNG();
    }

    RandomVariableNode::~RandomVariableNode()
    {
            _rng = nullptr;
    }
        double SinNode::calcValue()
        {
            return sin(_operand->calcValue());
        }

        std::string SinNode::toText()
        {
            return std::string("sin("+_operand->toText()+")");
        }

        double CosNode::calcValue()
        {
            return cos(_operand->calcValue());
        }

        std::string CosNode::toText()
        {
            return std::string("cos("+_operand->toText()+")");
        }

        double TanNode::calcValue()
        {
            return tan(_operand->calcValue());
        }

        std::string TanNode::toText()
        {
            return std::string("tan("+_operand->toText()+")");
        }

        double AsinNode::calcValue()
        {
            return asin(_operand->calcValue());
        }

        std::string AsinNode::toText()
        {
            return std::string("asin("+_operand->toText()+")");
        }

        double AcosNode::calcValue()
        {
            return acos(_operand->calcValue());
        }

        std::string AcosNode::toText()
        {
            return std::string("acos("+_operand->toText()+")");
        }

        double AtanNode::calcValue()
        {
            return atan(_operand->calcValue());
        }

        std::string AtanNode::toText()
        {
            return std::string("atan("+_operand->toText()+")");
        }

        double SinhNode::calcValue()
        {
            return sinh(_operand->calcValue());
        }

        std::string SinhNode::toText()
        {
            return std::string("sinh("+_operand->toText()+")");
        }

        double CoshNode::calcValue()
        {
            return cosh(_operand->calcValue());
        }

        std::string CoshNode::toText()
        {
            return std::string("cosh("+_operand->toText()+")");
        }

        double TanhNode::calcValue()
        {
            return tanh(_operand->calcValue());
        }

        std::string TanhNode::toText()
        {
            return std::string("tanh("+_operand->toText()+")");
        }

        double LogNode::calcValue()
        {
            return log10(_operand->calcValue());
        }

        std::string LogNode::toText()
        {
            return std::string("log("+_operand->toText()+")");
        }

        double LnNode::calcValue()
        {
            return log(_operand->calcValue());
        }

        std::string LnNode::toText()
        {
            return std::string("ln("+_operand->toText()+")");
        }

        double SqrtNode::calcValue()
        {
            return sqrt(_operand->calcValue());
        }

        std::string SqrtNode::toText()
        {
            return std::string("sqrt("+_operand->toText()+")");
        }

        double CeilNode::calcValue()
        {
            return ceil(_operand->calcValue());
        }

        std::string CeilNode::toText()
        {
            return std::string("ceil("+_operand->toText()+")");
        }

        double FloorNode::calcValue()
        {
            return floor(_operand->calcValue());
        }

        std::string FloorNode::toText()
        {
            return std::string("floor("+_operand->toText()+")");
        }

        double AbsNode::calcValue()
        {
            return fabs(_operand->calcValue());
        }

        std::string AbsNode::toText()
        {
            return std::string("abs("+_operand->toText()+")");
        }

        double NegateNode::calcValue()
        {
            return -_operand->calcValue();
        }

        std::string NegateNode::toText()
        {
            return std::string("-("+_operand->toText()+")");
        }

        double SumNode::calcValue()
        {
            double lim = _limit->calcValue();
            double result = 0.0;
            _counter->getVariables()->emplace(_counter->toText(),0.0);
            for(int i=1;i<=lim;i++)
            {
                _counter->getVariables()->at(_counter->toText())=(double)i;
                result += _formula->calcValue();
            }
            _counter->getVariables()->erase(_counter->toText());
            return result;
        }

        std::string SumNode::toText()
        {
            return std::string("sum("+_counter->toText()+","+_limit->toText()+","+_formula->toText()+")");
        }

        double MultNode::calcValue()
        {
            double lim = _limit->calcValue();
            double result = 1.0;
            _counter->getVariables()->emplace(_counter->toText(),0.0);
            for(int i=1;i<=lim;i++)
            {
                _counter->getVariables()->at(_counter->toText())=(double)i;
                result *= _formula->calcValue();
            }
            _counter->getVariables()->erase(_counter->toText());
            return result;
        }

        std::string MultNode::toText()
        {
            return std::string("mult("+_counter->toText()+","+_limit->toText()+","+_formula->toText()+")");
        }

        double EqualsNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            if(fabs(cache_pre-cache_post)<PRECISION)
                return 1.0;
            else return 0.0;
        }

        std::string EqualsNode::toText()
        {
            return std::string("("+_preoperand->toText()+"="+_postoperand->toText()+")");
        }

        double UnequalsNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            if(fabs(cache_pre-cache_post)<PRECISION)
                return 0.0;
            else return 1.0;
        }

        std::string UnequalsNode::toText()
        {
            return std::string("("+_preoperand->toText()+"!="+_postoperand->toText()+")");
        }

        double GreaterNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            if(fabs(cache_pre-cache_post)<PRECISION)
                return 0.0;
            else return cache_pre>cache_post?1.0:0.0;
        }

        std::string GreaterNode::toText()
        {
            return std::string("("+_preoperand->toText()+">"+_postoperand->toText()+")");
        }

        double LesserNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            if(fabs(cache_pre-cache_post)<PRECISION)
                return 0.0;
            else return cache_pre<cache_post?1.0:0.0;
        }

        std::string LesserNode::toText()
        {
            return std::string("("+_preoperand->toText()+">"+_postoperand->toText()+")");
        }

        double PlusNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return cache_pre+cache_post;
        }

        std::string PlusNode::toText()
        {
            return std::string("("+_preoperand->toText()+"+"+_postoperand->toText()+")");
        }

        double MinusNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return cache_pre-cache_post<PRECISION?0.0:cache_pre-cache_post;
        }

        std::string MinusNode::toText()
        {
            return std::string("("+_preoperand->toText()+"-"+_postoperand->toText()+")");
        }

        double TimesNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return cache_pre<PRECISION?0.0:cache_pre*cache_post;
        }

        std::string TimesNode::toText()
        {
            return std::string("("+_preoperand->toText()+"*"+_postoperand->toText()+")");
        }

        double DivideNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return cache_post<PRECISION?throw DivisionByZero():cache_pre/cache_post;
        }

        std::string DivideNode::toText()
        {
            return std::string("("+_preoperand->toText()+"/"+_postoperand->toText()+")");
        }

        double ModuloNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return std::fmod(cache_pre,cache_post);
        }

        std::string ModuloNode::toText()
        {
            return std::string("("+_preoperand->toText()+"%"+_postoperand->toText()+")");
        }

        double ExponentNode::calcValue()
        {
            double cache_pre = _preoperand->calcValue();
            double cache_post = _postoperand->calcValue();
            return pow(cache_pre,cache_post);
        }

        std::string ExponentNode::toText()
        {
            return std::string("("+_preoperand->toText()+"^"+_postoperand->toText()+")");
        }
    } }

#endif
