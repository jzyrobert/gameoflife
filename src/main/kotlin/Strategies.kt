import kotlin.random.Random

// Enum class giving the available player strategies and short description
enum class Strategies(val description: String) {
    PLAYER("Human controlled"),
    RANDOM("Chooses a random move"),
    GREEDY("Aims to minimise opponent pieces"),
    SAFE("Aims to maximise self pieces"),
    OPTIMAL("Aims to maximise the difference in pieces between self and opponent"),
    MINIMAX("Aims to minimise the best move the opponent could make")
}

// Return a move given a board and a chosen Strategy
fun getMove(board: Board, strategy: Strategies):Move {
    val moves = board.availableMoves()
    return when (strategy) {
        Strategies.PLAYER  ->  player(board, moves)
        Strategies.RANDOM  ->         random(moves)
        Strategies.GREEDY  ->  greedy(board, moves)
        Strategies.SAFE    ->    safe(board, moves)
        Strategies.OPTIMAL -> optimal(board, moves)
        Strategies.MINIMAX -> minimax(board, moves)
    }
}

/*
    Let the player choose his move.
    Reduce the work to do by only allowing players to choose from an indexed list, rather than parsing a direct move as
    input.
 */
fun player(board: Board, moves: Array<Move>): Move {
    println("You are player ${board.currentPlayer}")
    println("The available pieces are:")
    val pieces = board.playerPieces()
    pieces.sortBy { it.first }
    pieces.forEachIndexed { i, pair ->
        println("$i: $pair")
    }
    println("Please enter the index of the piece you wish to move: ")
    var index = readLineWrapped().toIntOrNull()
    while (index == null || index < 0 || index >= pieces.size) {
        println("You have entered an invalid index, please try again.")
        println("Please enter the index of the piece you wish to move: ")
        index = readLineWrapped().toIntOrNull()
    }
    val piece = pieces[index]
    val indexMoves = moves.filter { it.from == piece }.sortedBy { it.to.first }
    println("The available moves for $piece are:")
    indexMoves.forEachIndexed { i, move ->
        println("$i: ${move.to}")
    }
    println("Please enter the index of the move you wish to take: ")
    index = readLineWrapped().toIntOrNull()
    while (index == null || index < 0 || index >= indexMoves.size) {
        println("You have entered an invalid index, please try again.")
        println("Please enter the index of the piece you wish to move: ")
        index = readLineWrapped().toIntOrNull()
    }
    return indexMoves[index]
}

// Chooses a random move out of the available ones
fun random(moves: Array<Move>): Move {
    val choice = Random.nextInt(moves.size)
    return moves[choice]
}

// Chooses the first move that minimises the opponents pieces after the shuffle
fun greedy(board: Board, moves: Array<Move>):Move {
    var move = moves[0]
    var fewestOpponentPieces = Int.MAX_VALUE
    moves.forEach {
        val copy = board.getCopy()
        copy.applyMove(it)
        val leftoverOpponentPieces = copy.playerPieces(copy.getOpponent()).size
        if (leftoverOpponentPieces < fewestOpponentPieces) {
            fewestOpponentPieces = leftoverOpponentPieces
            move = it
        }
    }
    return move
}

// Chooses the first move that maximises own pieces after the shuffle
fun safe(board: Board, moves: Array<Move>):Move {
    var move = moves[0]
    var mostOwnPieces = Int.MIN_VALUE
    moves.forEach {
        val copy = board.getCopy()
        copy.applyMove(it)
        val leftoverOwnPieces = copy.playerPieces(copy.currentPlayer).size
        println("move $it has $leftoverOwnPieces")
        if (leftoverOwnPieces > mostOwnPieces) {
            mostOwnPieces = leftoverOwnPieces
            move = it
        }
    }
    return move
}

// Chooses the first move that maximises the difference in pieces between opponent and self
fun optimal(board: Board, moves: Array<Move>): Move {
    var move = moves[0]
    var greatestDifference = Int.MIN_VALUE
    moves.forEach {
        val copy = board.getCopy()
        copy.applyMove(it)
        val leftoverOwnPieces = copy.playerPieces(copy.currentPlayer).size
        val leftOverOpponentPieces = copy.playerPieces(copy.getOpponent()).size
        val pieceDifference = leftoverOwnPieces - leftOverOpponentPieces
        if (pieceDifference > greatestDifference) {
            greatestDifference = pieceDifference
            move = it
        }
    }
    return move
}

// For each move, apply it and then assume the opponent will do a optimal move.
// Then, return the move that gives the opponent the least optimal move
fun minimax(board: Board, moves: Array<Move>): Move {
    var move = moves[0]
    var moveScore = Int.MIN_VALUE
    val results = ArrayList<Pair<Move, Board>>()
    moves.forEach {
        val copy = board.getCopy()
        copy.applyMove(it)
        results.add(Pair(it, copy))
    }
    // If a move leads to a win just return it
    results.forEach {
        if (it.second.finished() && it.second.gameResult() == it.second.currentPlayer) {
            return it.first
        }
        val copy = it.second
        val score: Int
        //We can assign certain scores if the game finishes after the first move
        if (copy.finished()) {
            score = getEndScore(copy, copy.currentPlayer)
        } else {
            //Otherwise, simulate a move as the opponent using optimal function
            copy.changeSides()
            val opponentMove = optimal(copy, copy.availableMoves())
            copy.applyMove(opponentMove)
            // At this point, currentplayer is the OPPONENT, so we need to change sides again
            copy.changeSides()
            score = if (copy.finished()) {
                getEndScore(copy, copy.currentPlayer)
            } else {
                // Otherwise calculate the piece difference
                // We want the GREATEST piece difference for the current player after a opponents move
                val leftoverOwnPieces = copy.playerPieces(copy.currentPlayer).size
                val leftOverOpponentPieces = copy.playerPieces(copy.getOpponent()).size
                leftoverOwnPieces - leftOverOpponentPieces
            }
        }
        if (score > moveScore) {
            moveScore = score
            move = it.first
        }
    }
    return move
}

