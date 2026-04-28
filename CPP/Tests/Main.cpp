//
// Created by fldenhezt on 21-07-26.
//
#include <cppunit/TestResult.h>
#include "FormulaTests.h"

int main()
{
    CppUnit::TestResult t;
    ElaroSolutions::DARFormula::Tests::FormulaTests::suite()->run(&t);

}