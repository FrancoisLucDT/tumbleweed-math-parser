//
// Created by fldenhezt on 21-03-07.
//

#ifndef RUNTIMEFORMULA_IN_C____GCC__TOKEN_H
#define RUNTIMEFORMULA_IN_C____GCC__TOKEN_H

#include <string>
#include <cwchar>

#include "Nodes.h"



namespace ElaroSolutions{
    namespace DARFormula{

        class Token {
            int _level;
            std::string _value;
            UnaryFunctions _function;

        public:
            explicit Token(std::string value, int level);
            Token(const Token& old);
            const std::string &getValue() const;
            int getLevel() const;
            UnaryFunctions getFunction() const;
        };




    }
}



#endif //RUNTIMEFORMULA_IN_C____GCC__TOKEN_H