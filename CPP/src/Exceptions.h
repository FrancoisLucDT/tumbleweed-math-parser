#ifndef ElaroSolutions_DARFormula_EXCEPTIONS_H
#define ElaroSolutions_DARFormula_EXCEPTIONS_H

#include <stdexcept>

namespace ElaroSolutions { namespace DARFormula{
    class UnexpectedVariable : public std::runtime_error {
        public:
        explicit UnexpectedVariable(const std::string& what_arg) : runtime_error(
                reinterpret_cast<const char *>(what_arg.c_str())){}
    };

    class BadFormula: public std::runtime_error {
        public:
        explicit BadFormula(const std::string& what_arg) : runtime_error(
                reinterpret_cast<const char *>(what_arg.c_str())){}
    };

    class UninitializedVariable : public std::out_of_range {
        public:
        explicit UninitializedVariable(const std::string& what_arg) : out_of_range(
                reinterpret_cast<const char *>(what_arg.c_str())){}
    };

    class UnknownCharacter : public std::runtime_error {
        public:
        explicit UnknownCharacter(const std::string& what_arg) : std::runtime_error(
            reinterpret_cast<const char *>(what_arg.c_str())
            ){}
    };

class DivisionByZero : public std::runtime_error {
public:
    explicit DivisionByZero() : std::runtime_error("Division by zero"){}
};
} }

#endif
