data class Variable(val index: UInt)

sealed class Gate
data class And(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Nand(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Or(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Nor(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Not(val i: Variable, val o: Variable): Gate()
data class Xor(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Xnor(val i1: Variable, val i2: Variable, val o2: Variable): Gate()
data class Literal(val v: Variable, val isPositive: Boolean): Gate()
