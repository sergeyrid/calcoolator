import java.util.Stack
import kotlin.math.pow
import java.io.File

class Calculator {
    
fun toPostfix(expression: String): String {
    
    val operators = mutableListOf<Char>()
    var postfix = ""
    var prevIsDigit = false
    
    for (c in expression) {
        
        when {
            c == ' ' -> continue
            c.isDigit() -> {
                if (prevIsDigit) postfix += "$c"
                else postfix += " " + "$c"
                prevIsDigit = true
            }
            c == '.' -> {
                if (prevIsDigit ) postfix += "$c"
                else throw IllegalArgumentException("Wrong argument")
            }
            c == '-' -> {
                if (!prevIsDigit) postfix += " 0"
                operators.add(c)

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
    
    while (operators.isNotEmpty()) {
        postfix += " " + "${operators.removeLast()}"
    }
    return postfix.trim()
   
    
   
}

fun precedence(operator: Char): Int {
    return when (operator) {
        '+', '-' -> 1
        '*', '/' -> 2
        '^' -> 3
        else -> 0
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
                    if (b != 0.0) stack.push(a / b)
                    else throw ArithmeticException("Division by zero")
                }
                "^" -> stack.push(a.pow(b))
            }
        }
    } 
    

    return stack.pop()
}

fun readFromFile(filePath: String): String {
    val file = File(filePath)
    val content = StringBuilder()

    
    file.forEachLine { line ->
        content.append(line).append("\n")}
        
    

    return content.toString()
}

fun writeToFile(filePath: String, content: String) {
    val file = File(filePath)
    file.writeText(content)
}
}
fun main(){
    try{
   val calculator = Calculator()
    //val expression = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"
    //val expression = "(1 + 2) * 4 + 3.3"
    val expression = "(1 + 2) * 4 + 3.3 + 4/0"
	//val expression = calculator.readFromFile("itest.txt")
    //val expression = "-2 + -3 * 4"
    val postfix = calculator.toPostfix(expression)
    //println(postfix) // "3 4 2 * 1 5 - 2 ^ 3 ^ / +"
    val result = calculator.calculateRPN(postfix)
    println(expression + " = " + result )
    //calculator.writeToFile("otest.txt",expression + " = " + result )
    
  } catch (e: Exception) {
        println(e)
    }
    
}
