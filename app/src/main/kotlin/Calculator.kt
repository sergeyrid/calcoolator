import java.util.*
import kotlin.math.pow

object Calculator {

    private enum class Token {
        OPEN_UNARY_NOTHING, //предыдущий символ - "(" (не считая пробел), унарный минус или ничего
        NUMBER,            //предыдущий символ - число (без пробела)
        NUMBER_WITH_SPACE,  //предыдущий символ - число+пробел,
        BINARY_OPERATOR,    //предыдущий символ - знак бинарной операции,
        CLOSE       //")"
    }

    private fun toPostfix(expression: String): String {

        val operators = mutableListOf<Char>()
        var postfix = ""
        var prevToken: Token = Token.OPEN_UNARY_NOTHING
        var parenthesesCount = 0
        try {
            for (c in expression) {
                when {
                    c == ' ' -> {
                        if (prevToken == Token.NUMBER) prevToken = Token.NUMBER_WITH_SPACE
                    }

                    c.isDigit() -> {
                        postfix += when (prevToken) {
                            Token.NUMBER -> "$c"
                            Token.NUMBER_WITH_SPACE -> throw IllegalArgumentException("Wrong argument")
                            else -> " $c"
                        }
                        prevToken = Token.NUMBER
                    }

                    c == '.' -> {
                        if (prevToken == Token.NUMBER) postfix += "$c"
                        else throw IllegalArgumentException("Wrong argument")
                    }

                    c == '(' -> {
                        operators.add(c)
                        prevToken = Token.OPEN_UNARY_NOTHING
                        parenthesesCount++
                    }

                    c == ')' -> {
                        if (prevToken == Token.OPEN_UNARY_NOTHING || prevToken == Token.BINARY_OPERATOR)
                            throw IllegalArgumentException("Wrong argument")
                        if (parenthesesCount == 0)
                            throw IllegalArgumentException("Wrong argument")
                        while (operators.last() != '(') {
                            postfix += " ${operators.removeLast()}"
                        }
                        operators.removeLast()
                        prevToken = Token.CLOSE
                        parenthesesCount--
                    }

                    c =='-' || c == '+' || c == '^' || c == '*' || c == '/' -> {
                        if (prevToken == Token.BINARY_OPERATOR) throw IllegalArgumentException("Wrong argument")
                        if ((c == '-' || c == '+') && prevToken == Token.OPEN_UNARY_NOTHING) {
                            postfix += " 0"
                            operators.add(c)
                            prevToken = Token.OPEN_UNARY_NOTHING
                            continue
                        }
                        if (prevToken == Token.OPEN_UNARY_NOTHING) throw IllegalArgumentException("Wrong argument")

                        while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(c)) {
                            postfix += " ${operators.removeLast()}"
                        }
                        operators.add(c)
                        prevToken = Token.BINARY_OPERATOR
                    }

                    else -> throw IllegalArgumentException("Wrong argument")
                }
            }
            if (parenthesesCount != 0 || prevToken == Token.BINARY_OPERATOR)
                throw IllegalArgumentException("Wrong argument")

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Wrong argument")
        }
        while (operators.isNotEmpty()) {
            postfix += " ${operators.removeLast()}"
        }
        return postfix.trim()
    }

    private fun precedence(operator: Char): Int {
        return when (operator) {
            '+', '-' -> 1
            '*', '/' -> 2
            '^' -> 3
            '(', ')' -> 0
            else -> throw IllegalArgumentException("Wrong argument")
        }
    }


    private fun calculateRPN(input: String): Double {

        val stack = Stack<Double>()
        val tokens = input.split(" ")

        try {
            for (token in tokens) {
                if (token.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                    stack.push(token.toDouble())
                } else {
                    val b = stack.pop()
                    val a = stack.pop()

                    when (token) {
                        "+" -> stack.push(a + b)
                        "-" -> stack.push(a - b)
                        "*" -> stack.push(a * b)
                        "/" -> stack.push(a / b)
                        "^" -> stack.push(a.pow(b))
                    }
                }
            }
        } catch (e: EmptyStackException) {
            throw IllegalArgumentException("Wrong argument")
        }
        val result = stack.pop()
        if (!stack.empty()) throw IllegalArgumentException("Wrong argument")
        if (result.isInfinite() || result.isNaN()) throw ArithmeticException("Illegal operation")

        return result
    }

    fun calculate(expression: String): Double {
        return calculateRPN(toPostfix(expression))
    }
}