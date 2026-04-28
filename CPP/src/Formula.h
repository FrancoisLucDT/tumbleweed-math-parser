#ifndef ElaroSolutions_DARFormula_FORMULA_H
#define ElaroSolutions_DARFormula_FORMULA_H

#include "Nodes.h"
#include "Parser.h"
#include "Exceptions.h"

#include <typeinfo>
#include <set>
#include <locale>
#include <codecvt>

namespace ElaroSolutions { namespace DARFormula {

    class Formula
    {
        std::unordered_map<std::string ,double> _variables;
        std::set<std::string> _allowedVariables;
        std::set<std::string> _allowedFields;
        IDataStructure *_data;
        Node *_root;
        bool _throwsExceptionFromCalcValue;

        void checkVariablesAndFields() noexcept(false);
        void checkNode(Node *n) noexcept(false);
        void limitDataNode(int quantity, Node *n);
        void giveDataToDataNodes(Node *n);
        void setData(IDataStructure* data);

        public:
        Formula();
        Formula* setUpFormula(std::initializer_list<std::string> allowedVariables,
                              std::initializer_list<std::string> allowedFields,
                              IDataStructure* data);
        Formula* setFormula(const std::string& formula) noexcept(false);
        void addVariable(const std::string& variableName, double initialValue);
        double getVariableValue(const std::string& variableName) noexcept(false);
        Formula* updateVariable(const std::string& variableName, double value);
        Formula* addOneToVariable(const std::string& variableName);
        Formula* limitDataIndexQuantity(int quantity);
        void addField(const std::string& fieldName);
        double calculateValue();
        Formula* enableExceptionsOnCalculateValue();
        Formula* disableExceptionsOnCalculateValue();
        bool exceptionsOnCalculateValueEnabled() const;
        std::string formulaToText();
        ~Formula();
    };
} }

#endif
