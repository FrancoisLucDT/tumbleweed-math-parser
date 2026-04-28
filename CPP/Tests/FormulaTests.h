//
// Created by fldenhezt on 21-07-26.
//

#ifndef FORMULAPARSERCPPTESTS_FORMULATESTS_H
#define FORMULAPARSERCPPTESTS_FORMULATESTS_H

#include <cppunit/extensions/HelperMacros.h>
#include <cppunit/SourceLine.h>
#include "../src/Formula.h"
#include "MockDataStructure.h"

namespace ElaroSolutions::DARFormula::Tests{
    class FormulaTests : public CppUnit::TestFixture{
    CPPUNIT_TEST_SUITE(FormulaTests);

            CPPUNIT_TEST(testSimple);

            CPPUNIT_TEST(testUnary);

            CPPUNIT_TEST(testBinary);

            CPPUNIT_TEST(testTernary);
            CPPUNIT_TEST(testData);
        CPPUNIT_TEST_SUITE_END();
        Formula formula;
        MockDataStructure data;
    public:

        void testSimple();
        void testUnary();
        void testBinary();
        void testTernary();
        void testData();

    };

}



#endif //FORMULAPARSERCPPTESTS_FORMULATESTS_H
