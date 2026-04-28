#ifndef ElaroSolutions_DARFormula_FORMULA_CPP
#define ElaroSolutions_DARFormula_FORMULA_CPP


#include "Formula.h"

namespace ElaroSolutions { namespace DARFormula {


    Formula::Formula()
    {
        _data = nullptr;
        _root = nullptr;
        _throwsExceptionFromCalcValue = false;
        addVariable("E",2.718281828459045);
        addVariable("PI",3.141592653589793);
        addVariable("PHI",1.618033988749895);
    }

    Formula *Formula::setUpFormula(std::initializer_list<std::string> allowedVariables,
                                   std::initializer_list<std::string> allowedFields, IDataStructure *data) {

        for(const std::string &variable : allowedVariables)
        {
            addVariable(variable,0.0);
        }

        for(const std::string &field: allowedFields)
        {
            addField(field);
        }

        setData(data);
        return this;
    }

    void Formula::checkVariablesAndFields()
    {
            checkNode(_root);
    }

    void Formula::checkNode(Node *n)
    {
        switch(n->getType())
        {
            case Simple:
            {
                if (typeid(*n) == typeid(VariableNode))
                {
                    if(_allowedVariables.find(n->toText())!=_allowedVariables.end())
                    {
                        return;
                    }
                    throw UnexpectedVariable("Unsupported variable : " + (n->toText()));

                }
                else if (typeid(*n) == typeid(DataNode))
                {
                    auto *d = (DataNode *)n;
                    for (auto index:d->getIndexes())
                    {
                        checkNode(index);
                    }
                    if((_allowedFields.empty() && d->getField().empty())||_allowedFields.find(d->getField())!=_allowedFields.end())
                    {
                        return;
                    }
                    throw UnexpectedVariable("Unsupported field : " + d->getField());
                }
            }
            break;
            case Unary:
            {
                checkNode(((UnaryNode *)n)->getOperand());
            }
            break;
            case Binary:
            {
                checkNode(((BinaryNode*)n)->getPreOperand());
                checkNode(((BinaryNode*)n)->getPostOperand());
            }
            break;
            case Ternary:
            {
                checkNode(((TernaryNode*)n)->getLimit());
                if(_allowedVariables.find(((TernaryNode*)n)->getCounter()->toText())!=_allowedVariables.end())
                {
                    throw UnexpectedVariable("Counting variable "+((TernaryNode*)n)->getCounter()->toText()+ " already in use");
                }
                else
                {
                    _allowedVariables.insert(((TernaryNode*)n)->getCounter()->toText());
                }
                checkNode(((TernaryNode*)n)->getFormula());
                _allowedVariables.erase(((TernaryNode*)n)->getCounter()->toText());
            }
            break;
            default:return;
        }

    }

    Formula* Formula::setFormula(const std::string& formula)
    {
        delete _root;

        _root = Parser::Parse(Parser::Tokenize(formula),&_variables);

        checkVariablesAndFields();
        giveDataToDataNodes(_root);
        return this;
    }

    void Formula::addVariable(const std::string& variableName, double initialValue)
    {
        if(_variables.count(variableName)>0)
        {
            updateVariable(variableName,initialValue);
            return;
        }
        _variables.emplace(variableName, initialValue);
        _allowedVariables.emplace(variableName);
    }

    double Formula::getVariableValue(const std::string& variableName)
    {
        return _variables.at(variableName);
    }

    Formula* Formula::updateVariable(const std::string& variableName, double value)
    {
        _variables.at(variableName)=value;
        return this;
    }

    Formula* Formula::addOneToVariable(const std::string& variableName)
    {
        _variables.at(variableName)++;
        return this;
    }

    void Formula::setData(IDataStructure *data) {
        _data =data;
    }

    void Formula::addField(const std::string& fieldName) {
        _allowedFields.insert(fieldName);
    }

    double Formula::calculateValue() {
        double result=0.0;
        try{
            result = _root->calcValue();
        } catch (std::exception &r)
        {
            if (!_throwsExceptionFromCalcValue)
                return NAN;
            else
             throw;
        }
        return result;
    }

    Formula* Formula::enableExceptionsOnCalculateValue() {
        _throwsExceptionFromCalcValue = true;
        return this;
    }

    Formula* Formula::disableExceptionsOnCalculateValue() {
        _throwsExceptionFromCalcValue = false;
        return this;
    }

    bool Formula::exceptionsOnCalculateValueEnabled() const {
        return _throwsExceptionFromCalcValue;
    }

    std::string Formula::formulaToText() {
        return _root->toText();
    }

    Formula::~Formula()
    {
        delete _root;

    }

    Formula* Formula::limitDataIndexQuantity(int quantity)
    {
        limitDataNode(quantity,_root);
        return this;
    }

    void Formula::limitDataNode(int quantity, Node *n)
    {
        if(n->getType()==Simple)
        {
            if(typeid(*n) == typeid(DataNode))
            {
                auto d = (DataNode *)n;
                if(d->getIndexes().size()<quantity)
                    throw BadFormula("Not enough bracketed formulas after data declaration");
                else
                    d->getIndexes().resize(quantity);
            }
            else return;
        }
        else if(n->getType()==Unary)
        {
            limitDataNode(quantity,((UnaryNode *)n)->getOperand());
        }
        else if(n->getType()==Binary)
        {
            limitDataNode(quantity,((BinaryNode *)n)->getPreOperand());
            limitDataNode(quantity,((BinaryNode *)n)->getPostOperand());
        }
        else if(n->getType()==Ternary)
        {
            limitDataNode(quantity,((TernaryNode *)n)->getLimit());
            limitDataNode(quantity,((TernaryNode *)n)->getFormula());
        }
    }

        void Formula::giveDataToDataNodes(Node *n)
        {
            if(typeid(*n) == typeid(DataNode))
            {
                auto d = (DataNode *)n;
                d->setData(_data);
                for(auto indexNode : d->getIndexes())
                {
                    giveDataToDataNodes(indexNode);
                }
            }
            else if(n->getType()==Unary)
            {
                giveDataToDataNodes(((UnaryNode *)n)->getOperand());
            }
            else if(n->getType()==Binary)
            {
                giveDataToDataNodes(((BinaryNode *)n)->getPreOperand());
                giveDataToDataNodes(((BinaryNode *)n)->getPostOperand());
            }
            else if(n->getType()==Ternary)
            {
                giveDataToDataNodes(((TernaryNode *)n)->getLimit());
                giveDataToDataNodes(((TernaryNode *)n)->getFormula());
            }
        }




    } }

#endif
