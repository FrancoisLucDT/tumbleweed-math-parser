//
// Created by fldenhezt on 21-03-04.
//

#ifndef RUNTIMEFORMULA_IN_C____GCC__PARSER_H
#define RUNTIMEFORMULA_IN_C____GCC__PARSER_H

#include <vector>
#include <iterator>
#include "Token.h"
#include "Nodes.h"

namespace ElaroSolutions{namespace DARFormula{

    class Parser {
        static void parseFormula(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseExpression(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseTerm(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseFactor(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parsePossiblyNegatedOperand(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseOperand(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseQuantity(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
        static void parseData(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables);
    public:
        static std::vector<Token> Tokenize(const std::string &formula);
        static Node* Parse(std::vector<Token> tokens, std::unordered_map<std::string, double> *variables);
    };

}}



#endif //RUNTIMEFORMULA_IN_C____GCC__PARSER_H
