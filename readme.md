Here I made implementation of integer coefficients matrix inversion 

## How it works
* converting integers to bit-vectors in two's complement format
* addition is implemented with FullAdder primitives
* multiplication is implemented with Baugh-Wooley circuit
* Boolean circuits are converted to CNF with [Tseytin transformation](https://en.wikipedia.org/wiki/Tseytin_transformation)
* CNF are solved by Z3 solver
* **Note:** no control of integer overflow, but it could be implemented easily

## Details

### Addition circuit
for 4-bit signed integers

![adder](assets/adder.png)

### Multiplication circuit
for 4-bit signed integers

*I have found typo in original paper, so here is fixed circuit*

![Baugh-Wooley multiplier](assets/baugh-wooley.png)

## Usage

**Note:** `z3` should be available in your `PATH`

Call program with following arguments provided:

```
 -bw,--bitwidth <arg>   bit width of data vectors (8 for example)
 -c,--cnf <arg>         Path to file for output CNF in DIMACS format
 -m,--matrix <arg>      Path to text file with matrix
```

Example
```
--bitwidth 8 --matrix matrix/4x4.txt --cnf out.cnf
```

Gives following output
```
== input ==
2 1 0 1
3 2 0 -2
0 1 1 0
-1 2 3 4

== cnf summary ==
clauses count = 28817
variables count = 28705

== z3 time ==
168 millis

== output ==
6 -5 12 -4
-10 9 -21 7
10 -9 22 -7
-1 1 -3 1
```