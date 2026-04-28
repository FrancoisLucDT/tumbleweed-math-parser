//
// Created by fldenhezt on 21-05-31.
//

#ifndef RUNTIMEFORMULA_IN_C____GCC__MOCKDATASTRUCTURE_H
#define RUNTIMEFORMULA_IN_C____GCC__MOCKDATASTRUCTURE_H


#include "IDataStructure.h"
#include <vector>

class MockDataStructure : public ElaroSolutions::DARFormula::IDataStructure {

    std::vector<std::vector<int>> _someData;
    std::vector<std::vector<std::pair<std::string, int>>> _namedData;
public:
    MockDataStructure();

    double getValueAt(int *indexes, std::string field) override;

    ~MockDataStructure();
};


#endif //RUNTIMEFORMULA_IN_C____GCC__MOCKDATASTRUCTURE_H
