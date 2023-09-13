import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable

class DBTest {
    @Test
    fun calcStringTest() {
        val calcRaw1 = CalculationRaw("2 + 3", "5", true)
        val calcRaw2 = CalculationRaw("abcd", "efgh", true)
        val calcRaw3 = CalculationRaw("3 / 0", "Cannot divide by zero", false)

        assertEquals(calcRaw1.toString(), "Calculation: 2 + 3 = 5")
        assertEquals(calcRaw2.toString(), "Calculation: abcd = efgh")
        assertEquals(calcRaw3.toString(), "Calculation FAILED: 3 / 0 -> Cannot divide by zero")
    }

    @Test
    fun sampleTest() {
        DBOperator.initDB("test1")

        try {
            for (i in 1..5) {
                DBOperator.addCalculation("$i + 5", "${i + 5}", true)
            }
            val calcs = DBOperator
                .getAllCalculations()
                .reversed()
            assertAll((1..5)
                .map { i ->
                    Executable {
                        assertEquals(calcs[i - 1].expr, "$i + 5")
                        assertEquals(calcs[i - 1].res, "${i + 5}")
                        assertTrue(calcs[i - 1].succ)
                    }
                })
        } finally {
            DBOperator.deleteDB("test1")
        }
    }
}