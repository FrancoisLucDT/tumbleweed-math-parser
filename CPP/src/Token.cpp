//
// Created by fldenhezt on 21-03-07.
//

#include "Token.h"


ElaroSolutions::DARFormula::Token::Token(std::string value, int level) {
    _value = std::move(value);
    _level = level;
    _function = UUndefined;
    if(_level == 1)
    {
        if(_value == "sin" )
        {
            _function = Sin;
            return;
        }
        if(_value == "cos" )
        {
            _function = Cos;
            return;
        }
        if(_value == "tan" )
        {
            _function = Tan;
            return;
        }
        if(_value == "asin" )
        {
            _function = Asin;
            return;
        }
        if(_value == "acos" )
        {
            _function = Acos;
            return;
        }
        if(_value == "atan" )
        {
            _function = Atan;
            return;
        }
        if(_value == "sinh" )
        {
            _function = Sinh;
            return;
        }
        if(_value == "cosh" )
        {
            _function = Cosh;
            return;
        }
        if(_value == "tanh" )
        {
            _function = Tanh;
            return;
        }
        if(_value == "log" )
        {
            _function = Log;
            return;
        }
        if(_value == "ln" )
        {
            _function = Ln;
            return;
        }
        if(_value == "sqrt" )
        {
            _function = Sqrt;
            return;
        }
        if(_value == "ceil" )
        {
            _function = Ceil;
            return;
        }
        if(_value == "floor" )
        {
            _function = Floor;
            return;
        }
        if(_value == "abs" )
        {
            _function = Abs;
            return;
        }
    }
}

const std::string &ElaroSolutions::DARFormula::Token::getValue() const {
    return _value;
}

int ElaroSolutions::DARFormula::Token::getLevel() const {
    return _level;
}

ElaroSolutions::DARFormula::UnaryFunctions ElaroSolutions::DARFormula::Token::getFunction() const {
    return _function;
}

ElaroSolutions::DARFormula::Token::Token(const ElaroSolutions::DARFormula::Token &old) {
    this->_function = old._function;
    this->_value = old._value;
    this->_level = old._level;
}
