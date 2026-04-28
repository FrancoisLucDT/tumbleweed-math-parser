cmake_minimum_required(VERSION 3.19)
project(FormulaParserCPP)
include(CTest)

set(CMAKE_CXX_STANDARD 14)

include_directories(src)

add_library(FormulaParser SHARED
        src/Exceptions.h
        src/Formula.cpp
        src/Formula.h
        src/IDataStructure.h
        src/Nodes.cpp
        src/Nodes.h
        src/Parser.cpp
        src/Parser.h
        src/Token.cpp
        src/Token.h
        )

add_executable(FormulaParserTests
        src/Exceptions.h
        src/Formula.cpp
        src/Formula.h
        src/IDataStructure.h
        src/Nodes.cpp
        src/Nodes.h
        src/Parser.cpp
        src/Parser.h
        src/Token.cpp
        src/Token.h
        src/MockDataStructure.cpp
        src/MockDataStructure.h src/tests.cpp)

if(BUILD_TESTING)
    add_test(test_number FormulaParserTests 3 3)
    add_test(test_variable FormulaParserTests x 3 x=3)
    add_test(test_random FormulaParserTests r)
endif()