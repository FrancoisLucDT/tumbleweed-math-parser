#ifndef ElaroSolutions_DARFormula_IDATASTRUCTURE_H
#define ElaroSolutions_DARFormula_IDATASTRUCTURE_H

#include <string>
#include <stdexcept>

namespace ElaroSolutions { namespace DARFormula
{
    class IDataStructure
    {
        public:
        virtual double getValueAt(int indexes[], std::string field)=0;
        virtual ~IDataStructure()=0;
    };

    class UnreachableDataPoint : public std::out_of_range
    {
    public:
        explicit UnreachableDataPoint(const std::string& what_arg) : std::out_of_range(
                reinterpret_cast<const char *>(what_arg.c_str())){}
    };
} } // namespace ElaroSolutions::DARFormula

#endif