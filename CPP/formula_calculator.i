%module(directors="1") formula_calculator
%{
#include "src/Exceptions.h"
#include "src/IDataStructure.h"
#include "src/Token.h"
#include "src/Parser.h"
#include "src/Nodes.h"
#include "src/Formula.h"
%}

#define PRECISION 0.000005

%feature("director");

class Formula
{
    public:
    Formula();
    ~Formula();
    Formula* setUpFormula(std::initializer_list<std::string> allowedVariables, std::initializer_list<std::string> allowedFields, IDataStructure *data);
    Formula* setFormula(const std::string& formula);
    Formula* addVariable(const std::string& variableName, double initialValue);
    double getVariableValue(const std::string& variableName);
    Formula* updateVariable(const std::string& variableName, double value);
    Formula* addOneToVariable(const std::string& variableName);
    Formula* limitDataIndexQuantity(int quantity);
    Formula* addField(const std::string& fieldName);
    double calculateValue();
    Formula* enableExceptionsOnCalculateValue();
    Formula* disableExceptionsOnCalculateValue();
    bool exceptionsOnCalculateValueEnabled() const;
    std::string formulaToText();
    
};

class IDataStructure
{
    public:
    virtual double getValueAt(int indexes[], std::string field)=0;
    virtual ~IDataStructure()=0;
};