# solver
A mathematical expression parser and basic equation solver. This is my first self-directed coding project, with goal of practicing the object-oriented programming and data structures knowledge that I learned in Cornell’s CS 2110 course while also using version control and implementing basic mathematical algorithms. At this time there is no command line interface, so the main way to interact with the program is by modifying the `main` method in InputParser and calling desired methods from SystemSolver with appropriate arguments. 

There are three main methods (more detailed descriptions of input and output types in method specifications):
* `linear_solve`, for solving linear systems with the same number of variables as equations and a unique solution. 
* `linear_solve_general`, for solving arbitrary linear systems. Both this method and `linear_solve` are implemented using row reduction with partial pivoting and back substitution.
* `nonlinear_solve`, for solving nonlinear systems given some starting point near a solution. Implemented using Newton’s method.

The basic setup of the expression node classes was inspired by CS 2110’s fourth coding assignment from Spring 2024, which involved making a simple spreadsheet formula evaluator. 

