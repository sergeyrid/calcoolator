import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import kotlin.random.Random

class DBTest {
    @Test
    fun calcStringTest() {
        val calcRaw1 = CalculationRaw("2 + 3", "5", true)
        val calcRaw2 = CalculationRaw("(((3 × 2) + 90) ÷ 239) − 30 ^ 2", "−899.5983263598326", true)
        val calcRaw3 = CalculationRaw("3 / 0", "Cannot divide by zero", false)

        assertEquals(calcRaw1.toString(), "Calculation: 2 + 3 = 5")
        assertEquals(calcRaw2.toString(), "Calculation: (((3 × 2) + 90) ÷ 239) − 30 ^ 2 = −899.5983263598326")
        assertEquals(calcRaw3.toString(), "Calculation FAILED: 3 / 0 -> Cannot divide by zero")
    }

    @Test
    fun removeExtensionTest() {
        assertEquals(DBOperator.extractFileName("sqlquery.sql"), "sqlquery")
        assertEquals(DBOperator.extractFileName("sqlquery"), "sqlquery")
        assertEquals(DBOperator.extractFileName("database.mv.db"), "database")
        assertEquals(DBOperator.extractFileName("database"), "database")
        assertEquals(DBOperator.extractFileName("database.trace.db"), "database")
        assertEquals(DBOperator.extractFileName("./data/database.mv.db"), "database")
        assertEquals(DBOperator.extractFileName("./data/database"), "database")
        assertEquals(DBOperator.extractFileName("./sql/sqlquery.sql"), "sqlquery")
    }

    // Также тестирует реакцию на ненужное расширение файла базы
    @Test
    fun sampleTest() {
        DBOperator.initDB("test1.mv.db")

        try {
            for (i in 1..10) {
                if (i % 2 == 0)
                    DBOperator.addCalculation("$i + 1", "${i + 1}", true)
                else DBOperator.addCalculation("$i / 0", "Cannot divide by zero", false)
            }
            val calcs = DBOperator
                .getAllCalculations() // !! Не разворачиваем
            assertAll((1..10)
                .map { i ->
                    Executable {
                        assertEquals(calcs[i - 1].expr,
                            (11 - i).toString() + if (i % 2 != 0) " + 1" else " / 0")
                        // Поскольку они возвратились в обратном порядке,
                        // то теперь нечётные вычисления будут good
                        assertEquals(calcs[i - 1].res,
                            if (i % 2 != 0) "${11 - i + 1}" else "Cannot divide by zero")
                        assertEquals(calcs[i - 1].succ, i % 2 != 0)

                        assertEquals(calcs[i - 1].toString(),
                            if (i % 2 != 0) "Calculation: ${11 - i} + 1 = ${11 - i + 1}"
                            else "Calculation FAILED: ${11 - i} / 0 -> Cannot divide by zero")
                    }
                })
        } finally {
            DBOperator.deleteDB("test1.mv.db")
        }
    }

    @Test
    fun clearTest() {
        DBOperator.initDB("test2")

        try {
            assertEquals(DBOperator.getAllCalculations(), listOf<CalculationRaw>())

            for (i in 1..5) {
                DBOperator.addCalculation("$i + 5", "${i + 5}", true)
            }
            val calcs = DBOperator
                .getAllCalculations()
                .reversed() // !! Разворачиваем их
            assertAll((1..5)
                .map { i ->
                    Executable {
                        assertEquals(calcs[i - 1].expr, "$i + 5")
                        assertEquals(calcs[i - 1].res, (i + 5).toString())
                        assertTrue(calcs[i - 1].succ)
                    }
                })

            DBOperator.clear()
            assertEquals(DBOperator.getAllCalculations(), listOf<CalculationRaw>())
        } finally {
            DBOperator.deleteDB("test2")
        }
    }

    @Test
    fun segmentTest() {
        DBOperator.initDB("test3")

        try {
            DBOperator.addCalculations((1..20)
                .map { CalculationRaw("${20 - it} + 0", "${20 - it}", true) })

            fun testSegment(l: Int, r: Int) =
                assertEquals(DBOperator.getCalculations(l, r),
                    (l until r).map {
                        CalculationRaw("$it + 0", "$it", true)
                    })

            testSegment(2, 5)
            testSegment(10, 20)
            testSegment(0, 18)

            for (i in 1..50) {
                val left = Random.nextInt(0, 19)
                val right = Random.nextInt(left, 20)
                testSegment(left, right)
            }
        } finally {
            DBOperator.deleteDB("test3")
        }
    }

    @Test
    fun longTest() {
        DBOperator.initDB("test4")
        val num = 250

        try {
            for (k in 0 until num) {
                DBOperator.addCalculation(CalculationRaw(
                    (0..65535)
                        .map { i -> (kotlin.math.abs(((i + k) xor 12345) * 239 + 30) % 26 + 'a'.code).toChar() }
                        .joinToString(""),
                    (0..65535)
                        .map { i -> (kotlin.math.abs(((i * k) or 192837465) * 123 + 45678) % 26 + 'A'.code).toChar() }
                        .joinToString(""),
                    true))
            }

            val n = Random.nextInt(0, num - 1)
            val calc = DBOperator.getCalculations(num - 1 - n, num - n).first()

            for (i in 0..65535) {
                assertEquals(calc.expr[i], (kotlin.math.abs(((i + n) xor 12345) * 239 + 30) % 26 + 'a'.code).toChar())
                assertEquals(calc.res[i], (kotlin.math.abs(((i * n) or 192837465) * 123 + 45678) % 26 + 'A'.code).toChar())
            }
        } finally {
            DBOperator.deleteDB("test4")
        }
    }

    @Test
    fun manyTest() {
        DBOperator.initDB("test5")
        val num = 200000

        try {
            DBOperator.addCalculations(
                (1..num)
                    .map { CalculationRaw("(-${num - it}) ^ (1/2)", "Can't raise a negative number to a non-integer power", false) }
            )

            val left = Random.nextInt(0, num - 1)
            val right = Random.nextInt(left, num)
            assertEquals(DBOperator.getCalculations(left, right),
                (left until right).map {
                    CalculationRaw("(${-it}) ^ (1/2)", "Can't raise a negative number to a non-integer power", false)
                })
        } finally {
            DBOperator.deleteDB("test5")
        }
    }

    @Test
    fun executeSQLTest() {
        DBOperator.connect("test6") // база создана
        try {
            // схема базы пока не задана
            assertThrows<ExposedSQLException> {
                DBOperator.addCalculation(CalculationRaw("2 + 3", "5", true))
            }

            DBOperator.executeSQL(DBOperator.schemaQueryFileName)
            assertDoesNotThrow {
                DBOperator.addCalculation(CalculationRaw("2 + 3", "5", true))
            }
        } finally {
            DBOperator.deleteDB("test6")
        }
    }

    // Тестирует функции connectOrCreate и doesDBExist,
    // а также реакцию на ненужное расширение
    @Test
    fun dbCreatingTest() {
        assertFalse(DBOperator.doesDBExist("test7.mv.db"))
        assertFalse(DBOperator.doesDBExist("test7"))
        DBOperator.connectOrCreate("test7.mv.db")
        try {
            assertTrue(DBOperator.doesDBExist("test7.mv.db"))
            assertTrue(DBOperator.doesDBExist("test7"))
            assertDoesNotThrow {
                DBOperator.addCalculation(CalculationRaw("2 + 3", "5", true))
            }
        } finally {
            DBOperator.deleteDB("test7.mv.db")
        }
    }
}