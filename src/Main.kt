import java.time.Duration
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom

const val MAX_NUMBER = 20
const val VERBOSE = false

fun main() {
    println("Please enter the size of the matrix:")
    val matrixSize = readlnOrNull()!!.toInt()
    println("Please enter the number of threads you want to use:")
    val threadCount = readlnOrNull()!!.toInt()
    if (matrixSize % threadCount != 0) {
        println("Your matrix size must be divisible by the thread count without a remainder.")
        return
    }
    val (matrix, duration) = measureExecTime { generateMatrix(matrixSize, threadCount) }
    if (VERBOSE) {
        println("\nGenerated Matrix:")
        for (row in matrix) {
            println(row.joinToString(" "))
        }
    }
    println("\nExecution time: ${duration.toMillis()} ms")
}

fun <T> measureExecTime(block: () -> T): Pair<T, Duration> {
    val start = Instant.now()
    val result = block()
    val duration = Duration.between(start, Instant.now())
    return Pair(result, duration)
}

fun generateMatrix(size: Int, threadCount: Int): Array<IntArray> {
    val matrix = Array(size) { IntArray(size) }
    val threads = mutableListOf<Thread>()
    val columnsPerThread = size / threadCount
    for (threadIndex in 0 ..< threadCount) {
        val startColumn = threadIndex * columnsPerThread
        val endColumn = if (threadIndex == threadCount - 1) size else (threadIndex + 1) * columnsPerThread
        Thread {
            for (column in startColumn ..< endColumn) {
                var product = 1
                for (row in 0 ..< size) {
                    if (row != size - 1 - column) {
                        matrix[row][column] = ThreadLocalRandom.current().nextInt(MAX_NUMBER)
                        product *= matrix[row][column]
                    }
                }
                matrix[size - 1 - column][column] = product
            }
        }.also {
            threads.add(it)
            it.start()
        }
    }
    threads.forEach { it.join() }
    return matrix
}
