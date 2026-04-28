//
// Created by fldenhezt on 21-07-26.
//

#include "FormulaTests.h"
namespace ElaroSolutions::DARFormula::Tests{

    void FormulaTests::testSimple() {
        formula.setFormula("10");
        CPPUNIT_ASSERT(formula.calculateValue() == 10.0);
        CPPUNIT_ASSERT_THROW(formula.setFormula("10.a"),BadFormula);
        formula.setFormula(".05");
        CPPUNIT_ASSERT(formula.calculateValue()==0.05);
        std::string vars[] = {"Yay"};
        formula.setUpFormula(vars, nullptr, nullptr);
        formula.updateVariable("Yay",56.0);
        formula.setFormula("Yay");
        CPPUNIT_ASSERT(formula.calculateValue()==56.0);
        CPPUNIT_ASSERT_THROW(formula.setFormula("yay"),UnexpectedVariable);
        formula.setFormula("r");
        CPPUNIT_ASSERT_NO_THROW(formula.calculateValue());

    }

    void FormulaTests::testUnary() {
        formula.setFormula("-10");
        CPPUNIT_ASSERT(formula.calculateValue()== -10.0);
        formula.setFormula("-sin(10)");
        CPPUNIT_ASSERT(formula.calculateValue()== -sin(10.0));
    }

    void FormulaTests::testBinary() {
        formula.setFormula("6-1>2+2*2");
        CPPUNIT_ASSERT(formula.calculateValue()==0);
    }

    void FormulaTests::testTernary() {

    }

    void FormulaTests::testData() {

    }

}

