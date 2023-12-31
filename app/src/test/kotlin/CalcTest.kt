import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CalcTest {
    @Test
    fun testAddition() {
        assertEquals(5.0, Calculator.calculate("2 + 3"))
        assertEquals(-1.0, Calculator.calculate("2 + (-3)"))
        assertEquals(5.6, Calculator.calculate("3.5 + 2.1"))
        assertEquals(3.0, Calculator.calculate("+ 3"))
    }

    @Test
    fun testSubtraction() {
        assertEquals(-1.0, Calculator.calculate("2 - 3"))
        assertEquals(5.0, Calculator.calculate("2 - (-3)"))
        assertEquals(-2.0, Calculator.calculate(" 0 - 1 - 1"))
    }

    @Test
    fun testMultiplication() {
        assertEquals(6.0, Calculator.calculate("2 * 3"))
        assertEquals(-6.0, Calculator.calculate("2 * (-3)"))
        assertEquals(0.0, Calculator.calculate("0 * 0"))
    }

    @Test
    fun testDivision() {
        assertEquals(2.0, Calculator.calculate("6 / 3"))
        assertEquals(-2.0, Calculator.calculate("6 / (-3)"))
        assertEquals(-1.6666666666666667, Calculator.calculate("5/(-3)"))
        assertThrows<ArithmeticException> { Calculator.calculate("6 / 0") }
    }

    @Test
    fun testExponentiation() {
        assertEquals(8.0, Calculator.calculate("2 ^ 3"))
        assertEquals(1.0, Calculator.calculate("2^0"))
        assertEquals(0.125, Calculator.calculate("2 ^ (-3)"))
        assertEquals(1.4142135623730951, Calculator.calculate("2 ^ (1/2)"))
        assertThrows<ArithmeticException> {Calculator.calculate("(-2) ^ (1/2)")}
    }

    @Test
    fun testLargeNumbers() {
        assertEquals(0.0009765625, Calculator.calculate("2^(-10)"))
        assertEquals(0.00000095367431640625, Calculator.calculate("2^(-20)"))
        assertEquals(1.0E20, Calculator.calculate("10000000000 ^ 2"))
        assertEquals(1.0E-22, Calculator.calculate("1 / 10000000000000000000000"))
    }

    @Test
    fun testFullValid() {
        assertEquals(3.001953125, Calculator.calculate("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3"))
        assertEquals(-899.5983263598326, Calculator.calculate("(((3 * 2) + 90) / 239) - 30 ^ 2"))
    }

    @Test
    fun testInvalid() {
        assertThrows<IllegalArgumentException> { Calculator.calculate("") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("3 +") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("((3 + 4)^4^4))") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("(3 + 4") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("3 + a") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("1 a 1") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("3 4") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("5/-3") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("(3) (1) (2) 4 + 5") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("(3(1(2))) 4 + 5") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("1 - (-) 1") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("1 + (2) 3 (4) + 5") }
        assertThrows<IllegalArgumentException> { Calculator.calculate("(1) 1 - (*) 1") }
    }
}