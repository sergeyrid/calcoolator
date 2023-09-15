import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CalcTest {
    @Test
    fun testToPostfixValid() {
        val calculator = Calculator()

        // Test case 1: Valid expression with single digit numbers
        assertEquals("3 4 +", calculator.toPostfix("3 + 4"))

        // Test case 2: Valid expression with multi-digit numbers
        assertEquals("34 56 *", calculator.toPostfix("34 * 56"))

        // Test case 3: Valid expression with decimal numbers
        assertEquals("3.5 2.1 +", calculator.toPostfix("3.5 + 2.1"))

        // Test case 4: Valid expression with parentheses
        assertEquals("3 4 + 5 *", calculator.toPostfix("(3 + 4) * 5"))

        // Test case 5: Valid expression with exponentiation
        assertEquals("2 3 ^", calculator.toPostfix("2 ^ 3"))

        // Test case 6: Valid full expression
        assertEquals("3 4 2 * 1 5 - 2 ^ 3 ^ / +", calculator.toPostfix("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"))

        // Test case 7: Valid expression with negative numbers
        assertEquals("0 3 -", calculator.toPostfix("-3"))

        // Test case 8: Valid full expression
        assertEquals("3 2 * 90 + 239 / 30 2 ^ -", calculator.toPostfix("(((3 * 2) + 90) / 239) - 30 ^ 2"))

        // Test case 7: Valid expression with negative numbers
        assertEquals("5 0 3 - /", calculator.toPostfix("5/(-3)"))
    }

    @Test
    fun testToPostfixInvalid() {
        val calculator = Calculator()
        // Test case 1: Invalid expression with missing operand
        assertThrows<IllegalArgumentException> { calculator.toPostfix("3 +") }

        // Test case 2: Invalid expression with mismatched parentheses
        assertThrows<IllegalArgumentException> { calculator.toPostfix("(3 + 4") }

        // Test case 3: Invalid expression with invalid characters
        assertThrows<IllegalArgumentException> { calculator.toPostfix("3 + a") }

        // Test case 4: Invalid expression with two numbers
        assertThrows<IllegalArgumentException> { calculator.toPostfix("3 4") }

        // Test case 5: Invalid expression with unary minus
        assertThrows<IllegalArgumentException> { calculator.toPostfix("5/-3") }
    }

    @Test
    fun testCalculateRPNValid() {
        val calculator = Calculator()

        // Test case 1: Valid RPN expression with single digit numbers
        assertEquals(7.0, calculator.calculateRPN("3 4 +"))

        // Test case 2: Valid RPN expression with multi-digit numbers
        assertEquals(1904.0, calculator.calculateRPN("34 56 *"))

        // Test case 3: Valid RPN expression with decimal numbers
        assertEquals(5.6, calculator.calculateRPN("3.5 2.1 +"))

        // Test case 4: Valid RPN expression with parentheses
        assertEquals(35.0, calculator.calculateRPN("3 4 + 5 *"))

        // Test case 5: Valid RPN expression with exponentiation
        assertEquals(8.0, calculator.calculateRPN("2 3 ^"))

        // Test case 6: Valid RPN full expression
        assertEquals(3.001953125, calculator.calculateRPN("3 4 2 * 1 5 - 2 ^ 3 ^ / +"))

        //Test case 7: Valid RPN full expression
        assertEquals(-899.5983263598326, calculator.calculateRPN("3 2 * 90 + 239 / 30 2 ^ -"))

        // Test case 8: Valid RPN expression with negative numbers
        assertEquals(-3.0, calculator.calculateRPN("0 3 -"))

        // Test case 9: Valid RPN expression with negative numbers
        assertEquals(-1.6666666666666667, calculator.calculateRPN("5 0 3 - /"))

    }

    @Test
    fun testCalculateRPNInvalid() {
        val calculator = Calculator()
        // Test case 1: Invalid RPN expression with division by zero
        assertThrows<ArithmeticException> { calculator.calculateRPN("3 0 /") }

    }
}