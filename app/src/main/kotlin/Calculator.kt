import java.util.Stack
import kotlin.math.pow
import java.io.File

class Calculator {

    fun toPostfix(expression: String): String {

        val operators = mutableListOf<Char>()
        var postfix = ""
        var prevIsDigit = false
        try {
            for (c in expression) {
                when {
                    c == ' ' -> continue
                    c.isDigit() -> {
                        if (prevIsDigit) postfix += "$c"
                        else postfix += " $c"
                        prevIsDigit = true
                    }

                    c == '.' -> {
                        if (prevIsDigit) postfix += "$c"
                        else throw IllegalArgumentException("Wrong argument")
                    }

                    c == '-' -> {
                        if (!prevIsDigit) postfix += " 0"
                        operators.add(c)
                        prevIsDigit = false
                    }

                    c == '(' -> {
                        operators.add(c)
                        prevIsDigit = false
                    }

                    c == ')' -> {
                        while (operators.last() != '(') {
                            postfix += " " + "${operators.removeLast()}"
                        }
                        operators.removeLast()
                        prevIsDigit = false
                    }

                    else -> {
                        while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(c)) {
                            postfix += " " + "${operators.removeLast()}"
                        }
                        operators.add(c)
                        prevIsDigit = false
                    }
                }
            }
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