//
// Created by fldenhezt on 21-05-31.
//

#include "MockDataStructure.h"

MockDataStructure::MockDataStructure() {
    _someData.emplace_back(std::vector<int>());
    _someData.emplace_back(std::vector<int>());
    _someData.emplace_back(std::vector<int>());
    int array[10] = {0, 1, 5, 9, 7, 23, 51, 72, 11, 99};
    for (int &i : array) {
        _someData.at(0).push_back(i);
    }
    for (int i = 9; i >= 0; --i) {
        _someData.at(1).push_back(array[i]);
    }

    _namedData.emplace_back(std::vector<std::pair<std::string, int>>());
    _namedData.emplace_back(std::vector<std::pair<std::string, int>>());
    std::pair<std::string, int> values[10] = {{"pitch", 3},
                                              {"pitch", 5},
                                              {"pitch", 8},
                                              {"pitch", 1},
                                              {"pitch", 2},
                                              {"pitch", 56},
                                              {"pitch", 12},
                                              {"pitch", 43},
                                              {"pitch", 65},
                                              {"pitch", 90}};
    for (auto &i : values) {
        _namedData.at(0).push_back(i);
    }
}

double MockDataStructure::getValueAt(int *indexes, std::string field) {
    int index0 = indexes[0];
    int index1 = indexes[1];
    double result = 0.0;
    if (field.empty()) {
        try { result = _someData.at(index0).at(index1); }
        catch (std::out_of_range &) {
            throw ElaroSolutions::DARFormula::UnreachableDataPoint("indexes refer to nonexistent data");
        }
        return result;
    } else {
        return _namedData.at(index0).at(index1).first == field ? _namedData.at(index0).at(index1).second :
               throw ElaroSolutions::DARFormula::UnreachableDataPoint("field not implemented");
    }
}

MockDataStructure::~MockDataStructure() {
    for (auto n : _namedData) {
        n.clear();
    }
    for (auto s: _someData) {
        s.clear();
    }
    _namedData.clear();
    _someData.clear();

}
