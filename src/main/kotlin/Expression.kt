data class Variable(val index: UInt)

sealed class Expression
data class Or(val i1: Variable, val i2: Variable, val o2: Variable): Expression()
data class And(val i1: Variable, val i2: Variable, val o2: Variable): Expression()
data class Xor(val i1: Variable, val i2: Variable, val o2: Variable): Expression()
data class Not(val i: Variable, val o: Variable): Expression()
data class Literal(val v: Variable, val isPositive: Boolean): Expression()