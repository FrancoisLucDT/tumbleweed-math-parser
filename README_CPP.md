# Formula Parser

This library is intended to be used as part of a digital asset creation program (either as an internal element or as part of a plugin). It provides the ability for a user to use mathematical formulas to define a part, or the whole, of the asset's data points (such as notes in a MIDI file, vertex positions in a 3D model, joint positions and/or bone orientations in an animation, etc). 

The functionality is provided in the Formula class. It can parse a given string containing a mathematical expression into a calculable in-memory AST, keep track of the variables available to that expression and their values, update those values, and calculate that expression using them. The goal is to be able to evaluate that expression for many different values as fast as possible.

## Features

**Expanded set of binary arithmetic operators**: Adds comparative operators. A comparative operator gives 1 if the comparison is true and 0 if it isn't. It has the lowest operator priority; it is calculated after additions and substractions.
    
   *Examples*:
     1>1 equals 0. 1>0 equals 1. 3>1+2 equals 0, but (3>1)+2 equals 3.

**Access to an indexable numeric data structure through the data[] operator**: A Formula can refer to operands from a referenced data structure (whose class inherits IDataStructure) using the data[] operator. It will read the value stored at the index(es) equal to the value(s) of the expression(s) contained within the brackets. 

  *Examples*:
    data[3+5] would try to read the number stored at index 8 in the referenced data structure. data[3+5][10/2] would try to read the  
  

**Configurable variable set**: The developer using this library can define which variables are

### Full operator list

data[]

sin(), cos(), tan(), asin(), acos(), atan(), sinh(), cosh(), tanh(), log(), ln(), sqrt(), ceil(), floor(), abs(), -()

=, !=, <, >, +, -, *, /, %, ^

sum(), mult()

## To do

Implement an option to calculate values without going through function reference pointers.
