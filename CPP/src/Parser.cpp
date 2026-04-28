//
// Created by fldenhezt on 21-03-04.
//

#include "Parser.h"

namespace ElaroSolutions{namespace DARFormula{
    std::vector<Token> ElaroSolutions::DARFormula::Parser::Tokenize(const std::string &formula) {
        std::vector<Token> formulaTokens;
        formulaTokens.reserve(formula.size());
        if(formula.empty())
        {
            formulaTokens.emplace_back("End of Formula",8);
            return formulaTokens;
        }
        for(size_t i=0;i<formula.size();++i)
        {
            char current=formula.at(i);
            if(isdigit(current)||current=='.')
            {
                //Make a number token
                size_t j=1;
                for(j=1;i+j<formula.size() && (isdigit(formula.at(i+j))||formula.at(i+j)=='.');++j)
                {}
                formulaTokens.emplace_back(formula.substr(i,j),0);
                i+=j-1;
            }
            else if(isalpha(current)||current=='_')
            {
                //Make a variable or predefined function token
                size_t j;
                std::string temp;
                for(j=1;i+j<formula.size() && (isalnum(formula.at(i+j))||formula.at(i+j)=='_');++j)
                {}
                temp = formula.substr(i,j);
                //Here be the functions
                if(temp=="sqrt" ||temp=="log" ||temp=="ln" ||temp=="sin" ||
                temp=="cos" ||temp=="tan" ||temp=="sinh" ||temp=="cosh" ||
                temp=="tanh" ||temp=="asin" ||temp=="acos" ||temp=="atan" ||
                temp=="abs" ||temp=="ceil" ||temp=="floor" ||temp=="sum" ||
                temp=="mult"||temp=="data" )
                {
                    formulaTokens.emplace_back(temp,1);
                    //If I want to add levels or types to my tokens, this will be handy
                }
                else //Here be the variables
                {
                    //This does the same thing as the other branch, but see comment in that branch
                    formulaTokens.emplace_back(temp,0);
                }
                i+=j-1;
            }
            else if(current=='=' || current=='!'||current=='>'||current=='<')
            {
                if(current=='!' && formula.at(i+1)=='=')
                {
                    formulaTokens.emplace_back(formula.substr(i,2),2);
                    ++i;
                }
                else
                {
                    formulaTokens.emplace_back(formula.substr(i,1),2);
                }
            }
            else if(current=='+' || current=='-')
            {formulaTokens.emplace_back(formula.substr(i,1),3);}
            else if(current=='*' || current=='/' || current=='%')
            {formulaTokens.emplace_back(formula.substr(i,1),4);}
            else if(current=='^')
            {
                formulaTokens.emplace_back(formula.substr(i,1),5);
            }
            else if(current=='(' || current==')' || current=='['|| current==']')
            {formulaTokens.emplace_back(formula.substr(i,1),6);}
            else if(current==',' || current==':')
            {formulaTokens.emplace_back(formula.substr(i,1),7);}
            else if(current==' ')
            {}
            else {std::string error = "Unexpected Character :";
                error.push_back(current);
                throw UnknownCharacter(error);}
        }
        formulaTokens.emplace_back("End of Formula",8);
        return formulaTokens;
    }

        Node *Parser::Parse(std::vector<Token> tokens, std::unordered_map<std::string, double> *variables) {
            Node* root= nullptr;
            auto it = tokens.begin();
            try{
                parseFormula(root, it, variables);
            } catch (BadFormula &b)
            {
                delete root;
                throw;
            }
            ++it;
            if(it->getLevel()!=8)
            {
                std::string badToken = it->getValue();
                delete root;
                throw BadFormula("Expected end of formula, instead got : "+badToken);
            }
            return root;
        }

        void Parser::parseFormula(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            Node* e2= nullptr;
            BinaryFunctions op = BUndefined;
            parseExpression(e,it,variables);
            while(it->getValue()=="=" ||it->getValue()=="!=" ||it->getValue()=="<" ||it->getValue()==">")
            {
                if(it->getValue()=="=")
                {
                    op = Equals;

                }
                else if(it->getValue()=="!=")
                {
                    op = Unequals;

                }
                else if(it->getValue()=="<")
                {
                    op = Lesser;

                }
                else
                {
                    op = Greater;

                }
                ++it;
                parseExpression(e2,it,variables);
                e= BinaryNode::BinaryNodeConstructor(e,e2,op);
            }
        }

        void Parser::parseExpression(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            Node* e2=nullptr;
            BinaryFunctions op = BUndefined;
            parseTerm(e,it,variables);
            while(it->getValue()=="+" || it->getValue()=="-")
            {
                if(it->getValue()=="+")
                {
                    op = Plus;
                }
                else
                {
                    op = Minus;
                }
                ++it;
                parseTerm(e2,it,variables);
                e = BinaryNode::BinaryNodeConstructor(e,e2,op);
            }

        }

        void Parser::parseTerm(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            Node* e2=nullptr;
            BinaryFunctions op = BUndefined;
            parseFactor(e,it,variables);
            while(it->getValue()=="*" || it->getValue()=="/" || it->getValue()=="%")
            {
                if(it->getValue()=="*")
                {
                    op = Times;
                }
                else if(it->getValue()=="/")
                {
                    op = Divide;
                }
                else
                {
                    op = Modulo;
                }
                ++it;
                parseFactor(e2,it,variables);
                e = BinaryNode::BinaryNodeConstructor(e,e2,op);
            }

        }

        void Parser::parseFactor(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            Node* e2 = nullptr;
            parsePossiblyNegatedOperand(e,it,variables);
            while(it->getValue()=="^")
            {
                ++it;
                parsePossiblyNegatedOperand(e2,it,variables);
                e= BinaryNode::BinaryNodeConstructor(e,e2,Exponent);
            }

        }

        void Parser::parsePossiblyNegatedOperand(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            bool isNegated = false;
            if(it->getValue()=="-")
            {
                isNegated=true;
                ++it;
            }
            parseOperand(e,it,variables);
            if(isNegated)
            {
                e=UnaryNode::UnaryNodeConstructor(e,Negate);
            }

        }

        void Parser::parseOperand(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            if(it->getLevel()==0 || it->getValue()=="data")
            {
                parseQuantity(e,it,variables);
            }
            else if(it->getValue()=="sum" || it->getValue()=="mult" )
            {
                TernaryFunctions op = TUndefined;
                std::string countingVariable;
                Node* limit = nullptr;
                if(it->getValue()=="sum")
                {
                    op = Sum;
                }
                else
                {
                    op = Mult;
                }
                ++it;
                if(it->getValue()!="(")
                    throw BadFormula("Missing left parentheses after ternary function declaration");
                ++it;
                if (it->getLevel() == 0 && isdigit(it->getValue().at(0)))
                    throw BadFormula( "Number instead of variable in ternary function");
                countingVariable = it->getValue();
                ++it;
                if(it->getValue()!=",")
                    throw BadFormula("Missing comma between counting variable and limit expression");
                ++it;
                parseFormula(limit,it,variables);
                ++it;
                if(it->getValue()!=",")
                    throw BadFormula("Missing comma between limit expression and evaluated expression");
                ++it;
                parseFormula(e,it,variables);
                ++it;
                if(it->getValue()!=")")
                    throw BadFormula("Missing right parentheses after evaluated expression");
                e = TernaryNode::TernaryNodeConstructor(countingVariable,limit,e,op,variables);
            }
            else if(it->getLevel()==1)
            {
                UnaryFunctions op = it->getFunction();
                ++it;
                if(it->getValue()!="(")
                    throw BadFormula("Missing left parentheses after function declaration");
                parseFormula(e,it,variables);
                ++it;
                if(it->getValue()!=")")
                    throw BadFormula("Missing right parentheses after function declaration");

                e = UnaryNode::UnaryNodeConstructor(e,op);
            }
            else if(it->getValue()=="(")
            {
                ++it;
                parseFormula(e,it,variables);
                ++it;
                if(it->getValue()!=")")
                    throw BadFormula("Missing right parentheses");
            }
            else
                throw BadFormula("Unexpected token");
        }

        void Parser::parseQuantity(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
            if(it->getValue()=="data")
                parseData(e,it,variables);
            else if(isdigit(it->getValue().at(0)) || it->getValue().at(0)=='.')
            {
                e = new ValueNode(std::stod( it->getValue(), nullptr));
            }
            else if(it->getValue()=="r")
            {
                e = new RandomVariableNode();
            }
            else
            {
                e = new VariableNode(it->getValue(),variables);
            }
        }


        void Parser::parseData(Node *&e, __gnu_cxx::__normal_iterator<Token *, std::vector<Token, std::allocator<Token>>> &it, std::unordered_map<std::string, double> *variables) {
        std::vector<Node *> indexes;
        std::string fieldName;
        Node * i= nullptr;
        ++it;
        if(it->getValue()!="[")
            throw BadFormula("Missing left bracket after data declaration");
        ++it;
        parseFormula(i,it,variables);
        ++it;
        if(it->getValue()!="]")
            throw BadFormula("Missing right bracket after data declaration");
        indexes.emplace_back(i);
        //Example: data[0][0][0] : we are now on the first ']', so the next token must be evaluated without incrementing
        //the iterator, because no parseSomething function must consume more than the tokens it needs
        while(it[1].getValue()=="[")
        {
            ++it;
            ++it;
            parseFormula(i,it,variables);
            ++it;
            if(it->getValue()!="]")
                throw BadFormula("Missing right bracket after data declaration");
            indexes.emplace_back(i);
        }
        if(it[1].getValue()==":")
        {
            ++it;
            ++it;
            fieldName = it->getValue();
        }
        e = new DataNode(indexes,fieldName);
        }


    }}

