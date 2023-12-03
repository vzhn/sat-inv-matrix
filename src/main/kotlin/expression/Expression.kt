package expression

sealed class Expression
class Product(val es: List<Expression>): Expression()
class Sum(val es: List<Expression>): Expression()
class Literal(val n: Int): Expression()