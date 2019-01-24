// Default size of the board
const val SIZE = 8

// Default amount of pieces per player
const val PIECES = 12

// Maximum number of moves allowed before a draw is forced
const val MAX_MOVES = 250

// The characters used for printing the pieces
const val BLUEPIECE = "b"
const val REDPIECE = "r"
const val EMPTYSQUARE = " "

// Edge case scores for minimax to use
const val BEST_SCORE = SIZE * SIZE
const val WORST_SCORE = - (SIZE * SIZE)
const val DRAW_SCORE = 0

// Helper function to prevent duplicate code and ensure null safe input strings.
// Will terminate if EOL (CTRL+D) is given
fun readLineWrapped(): String {
    val line = readLine()
    if (line == null) {
        println("Inputstream was terminated, exiting")
        System.exit(1)
    }
    return line!!
}

// Ask the user for the strategies of the two players
fun choosePlayer(player: Player): Strategies {
    println("Who will player $player be?")
    println("Please enter one of the following options:")
    val choices = Strategies.values().map { it.toString().toLowerCase() }
    Strategies.values().forEach {
        println("${it.toString().toLowerCase()}: ${it.description}")
    }
    print("Your choice: ")
    var choice = readLineWrapped().toLowerCase()
    while (choice !in choices) {
        println("You did not enter a valid choice, please try again:")
        print("Your choice: ")
        choice = readLineWrapped().toLowerCase()
    }
    return Strategies.values()[choices.indexOf(choice)]
}

// Ask if the user wants to play again
fun playAgain(): Boolean {
    println("Do you want to play again? y/yes/(Anything not yes is taken as no)")
    val choice = readLineWrapped().toLowerCase()
    return choice == "yes" || choice == "y"
}


// Helper to calculate the end board state score given the board and the player you are
fun getEndScore(board: Board, player: Player): Int {
    assert(board.finished())
    return when (board.gameResult()) {
        player -> BEST_SCORE
        board.getOpponent(player) -> WORST_SCORE
        else -> DRAW_SCORE
    }
}