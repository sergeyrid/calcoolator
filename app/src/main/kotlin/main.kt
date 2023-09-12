fun main() {
    DBOperator.connect("test")
    for (i in (1..100)) {
        DBOperator.addCalculation(CalculationRaw("test$i", "$i", false))
    }

    DBOperator.getAllCalculations()
        .map { "Calculation: ${it.expr} = ${it.res} ${if (it.succ) 'O' else 'X'}" }
        .forEach { println(it) }
}
