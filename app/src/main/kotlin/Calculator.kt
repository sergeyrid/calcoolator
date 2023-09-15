import java.util.Stack
import kotlin.math.pow

class Calculator {

    private enum class Token {
        OPEN_OR_NOTHING, //предыдущий символ - "(" (не считая пробел), унарный минус или ничего
        NUMBER,            //предыдущий символ - число (без пробела)
        NUMBER_WITH_SPACE,  //предыдущий символ - число+пробел,
        BINARY_OPERATOR,    //предыдущий символ - знак бинарной операции,
        CLOSE       //")"
    }

    fun toPostfix(expression: String): String {

        val operators = mutableListOf<Char>()
        var postfix = ""
        var prevToken: Token = Token.OPEN_OR_NOTHING
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

                    c == '-' -> {
                        if (prevToken == Token.OPEN_OR_NOTHING) postfix += " 0"
                        else if (prevToken == Token.BINARY_OPERATOR) throw IllegalArgumentException("Wrong argument")
                        operators.add(c)
                        prevToken = Token.OPEN_OR_NOTHING
                    }

                    c == '(' -> {
                        operators.add(c)
                        prevToken = Token.OPEN_OR_NOTHING
                        parenthesesCount++
                    }

                    c == ')' -> {
                        while (operators.last() != '(') {
                            postfix += " " + "${operators.removeLast()}"
                        }
                        operators.removeLast()
                        prevToken = Token.CLOSE
                        parenthesesCount--
                    }

                    else -> {
                        if (prevToken == Token.BINARY_OPERATOR) throw IllegalArgumentException("Wrong argument")

                        while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(c)) {
                            postfix += " " + "${operators.removeLast()}"
                        }
                        operators.add(c)
                        prevToken = Token.BINARY_OPERATOR
                    }
                }
            }
            if (parenthesesCount != 0 || prevToken == Token.BINARY_OPERATOR)
                throw IllegalArgumentException("Wrong argument")

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Wrong argument")
        }
        while (operators.isNotEmpty()) {
            postfix += " " + "${operators.removeLast()}"
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


    fun calculateRPN(input: String): Double {
        val stack = Stack<Double>()
        val tokens = input.split(" ")

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
                    "/" -> {
                        if (!(a / b).isInfinite()) stack.push(a / b)
                        else throw ArithmeticException("Illegal operation")
                    }

                    "^" -> stack.push(a.pow(b))
                }
            }
        }
        return stack.pop()
    }
}