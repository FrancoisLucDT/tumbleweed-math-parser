//
// Created by fldenhezt on 21-08-06.
//

#include "Formula.h"
#include "MockDataStructure.h"

int main(int argc, char **argv)
{
    if(argc == 1)
    {
        printf("I am for testing purposes only. Please don't invoke me by hand.");
        return 0;
    }
    ElaroSolutions::DARFormula::Formula formula;
    auto* data = new MockDataStructure();
    bool goodCalculation;

    if(argc == 2)
    {
        try{
            formula.setFormula(argv[1]);
        } catch (std::exception &e)
        {
            printf("%s", e.what());
            delete data;
            return 1;
        }
    }

    if(argc == 3)
    {
        try{
            formula.setFormula(argv[1]);
        } catch (std::exception &e)
        {
            printf("%s", e.what());
            delete data;
            return 1;
        }
        try {
            goodCalculation = (formula.calculateValue() == std::stod(argv[2]));
            delete data;
            return !goodCalculation;
        } catch (std::exception &e)
        {
            printf("%s",e.what());
            delete data;
            return 1;
        }
    }

    if(argc == 4)
    {
        std::string variables = std::string(argv[3]);
        size_t separatorIndex = variables.find('=');
        std::string variable = variables.substr(0,separatorIndex);
        double value = std::stod(variables.substr(separatorIndex+1));
        formula.setUpFormula({variable},{"","pitch"},data);
        formula.updateVariable(variable,value);
        try{
            formula.setFormula(argv[1]);
        } catch (std::exception &e)
        {
            printf("%s", e.what());
            delete data;
            return 1;
        }
        try {
            goodCalculation = (formula.calculateValue() == std::stod(argv[2]));
            delete data;
            return !goodCalculation;
        } catch (std::exception &e)
        {
            printf("%s",e.what());
            delete data;
            return 1;
        }
    }

    delete data;
    return 0;

}