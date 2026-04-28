cmake_minimum_required(VERSION 3.19)
project(FormulaParserCPPTests)

set(CMAKE_CXX_STANDARD 14)

include_directories(src)

add_executable(FormulaParserCPPTests
        ../src/Exceptions.h
        ../src/Formula.cpp
        ../src/Formula.h
        ../src/IDataStructure.h
        ../src/Nodes.cpp
        ../src/Nodes.h
        ../src/Parser.cpp
        ../src/Parser.h
        ../src/Token.cpp
        ../src/Token.h
        FormulaTests.h
        FormulaTests.cpp
        MockDataStructure.h
        MockDataStructure.cpp
        Main.cpp Main.cpp)