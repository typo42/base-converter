import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.system.exitProcess

fun main() {
    while (true) {
        radix()
    }
}

val digits = ('0'..'9').joinToString("")
val letters = ('a'..'z').joinToString("")
val validRadix = digits + letters

fun radix() {
    print("\nEnter two numbers in format: {source base} {target base} (To quit type /exit) ")
    val userInput = readLine()!!.toString()
    if (userInput == "/exit") exitProcess(0)
    val (sourceBase, targetBase) = userInput.split(" ").map { it.toBigInteger() }
    convert(sourceBase, targetBase)
}

fun toDigitsIntArray(number: String): IntArray {
    val digitsCharArray = number.toCharArray()
    val digitsIntArray = IntArray(digitsCharArray.size)
    for (i in digitsCharArray.indices) {
        digitsIntArray[i] = validRadix.indexOf(digitsCharArray[i])
    }
    return digitsIntArray
}

fun convertToDecimal(number: String, sourceBase: BigInteger): BigInteger {
    val reversedDigitsIntArray = toDigitsIntArray(number).reversedArray()
    var decimal = BigInteger.ZERO
    for (i in reversedDigitsIntArray.indices) {
        decimal += reversedDigitsIntArray[i].toBigInteger() * sourceBase.pow(i)
    }
    return decimal
}

fun convertWhole(number: String, sourceBase: BigInteger, targetBase: BigInteger): String {
    var decimal = convertToDecimal(number, sourceBase)
    var digits = ""
    if (decimal.toInt() == 0) digits = "0"
    while (decimal >= BigInteger.ONE) {
        val remainder = decimal % targetBase
        digits += validRadix[remainder.toInt()]
        decimal /= targetBase
    }
    return digits.reversed()
}

fun convertFraction(fraction: String, sourceBase: BigInteger, targetBase: BigInteger): String {
    val fractionDigitsIntArray = toDigitsIntArray(fraction)
    val fractionToDecimal = mutableListOf<Double>()
    var n = -1
    fractionDigitsIntArray.forEach { fractionToDecimal += it.toDouble() * sourceBase.toDouble().pow(n--) }
    var decimalFraction = fractionToDecimal.sum().toBigDecimal()
    var digits = ""
    repeat(5) {
        val produce = (decimalFraction * targetBase.toBigDecimal()).setScale(5, RoundingMode.CEILING)
        digits += validRadix[produce.toInt()]
        decimalFraction = produce - produce.toInt().toBigDecimal()
    }
    return digits
}

fun convertFractional(number: String, sourceBase: BigInteger, targetBase: BigInteger): String {
    val (whole, fraction) = number.split('.')
    return "${convertWhole(whole, sourceBase, targetBase)}.${convertFraction(fraction, sourceBase, targetBase)}"
}

fun convert(sourceBase: BigInteger, targetBase: BigInteger) {
    print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
    val number = readLine()!!
    val result: Any = when {
        number == "/back" -> radix()
        '.' !in number -> convertWhole(number, sourceBase, targetBase)
        '.' in number -> convertFractional(number, sourceBase, targetBase)
        else -> "conversion failed due to illegal input"
    }
    println("Conversion result: $result\n")
    convert(sourceBase, targetBase)
}