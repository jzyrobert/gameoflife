import java.lang.StringBuilder
import kotlin.random.Random

// To track the current player and result
enum class Player {
    BLUE, RED, DRAW
}

// Stores the positions of a single move
data class Move(val from: Pair<Int, Int>, val to: Pair<Int, Int>) {
    override fun toString(): String {
        return "from $from to $to"
    }
}

// Board class that initialises with two lists that contain a players pieces.
data class Board(var bluePieces: ArrayList<Pair<Int, Int>> = ArrayList(),
                 var redPieces: ArrayList<Pair<Int, Int>> = ArrayList(),
                 var moves: Int = 0,
                 var currentPlayer: Player = Player.BLUE,
                 val player1: Strategies,
                 val player2: Strategies) {

    // Initialise the board with random pieces, assuming minimum limits
    init {
        assert(SIZE > 1)
        assert(PIECES > 1)
        assert(PIECES * 2 < SIZE * SIZE)
        assignPieces(Player.BLUE)
        assignPieces(Player.RED)
        bluePieces.sortedBy { it.first }
        redPieces.sortedBy { it.first }
    }


    // Play out a single game, reporting the result
    fun play() {
        var currentStrategy = getStrategy()
        do {
            println(this)
            val move = getMove(this, currentStrategy)
            println("The chosen move is: $move for player $currentPlayer playing as ${getStrategy()}")
            applyMove(move)
            changeSides()
            currentStrategy = getStrategy()
        } while(!finished())
        println("The final game state is:")
        println(this)
        when (gameResult()) {
            Player.BLUE, Player.RED -> println("The ${gameResult()} player won!")
            Player.DRAW -> println("The game was a draw!")
        }
    }

    //Simulate a game without players
    fun simulate() {
        assert(player1 != Strategies.PLAYER)
        assert(player2 != Strategies.PLAYER)
        var currentStrategy = getStrategy()
        do {
            val move = getMove(this, currentStrategy)
            applyMove(move)
            changeSides()
            currentStrategy = getStrategy()
        } while(!finished())
    }

    // Helper function to change the current player
    fun changeSides() {
        currentPlayer = getOpponent()
    }

    // Get the strategy of a player, defaulting to the current one
    private fun getStrategy(player: Player = currentPlayer): Strategies {
        return when (player) {
            Player.BLUE -> player1
            else -> player2
        }
    }

    // Generate PIECES amount of random pieces for a player and places them in the appropriate ArrayList
    private fun assignPieces(player: Player) {
        assert(player != Player.DRAW)
        for (j in 1..PIECES) {
            var row: Int
            var col: Int
            var piece: Pair<Int, Int>
            do {
                row = Random.nextInt(1, SIZE + 1)
                col = Random.nextInt(1, SIZE + 1)
                piece = Pair(row, col)
            } while (piece in bluePieces || piece in redPieces)
            when(player) {
                Player.BLUE -> bluePieces.add(piece)
                Player.RED -> redPieces.add(piece)
                else -> throw InternalError("Invalid Player!")
            }
        }
    }

    /*
     Returns whether the game has finished.
     This occurs when blue or red has no more pieces left,
     when the current player has no more moves,
     or when the maximum amount of moves have occured
     (to prevent stalemates).
    */
    fun finished(): Boolean {
        val blue = bluePieces.size
        val red = redPieces.size
        val currentMoves = availableMoves(currentPlayer)
        return (blue == 0 || red == 0 || currentMoves.isEmpty() || moves > MAX_MOVES)
    }

    /*
    The result of a game, assuming it has finished
    If moves have exceeded MAX_MOVES, it is a draw
    If both players have no more pieces after a shuffle, it is a draw
    Otherwise the player with remaining pieces wins
     */
    fun gameResult(): Player {
        assert(finished())
        val playerMoves = availableMoves(currentPlayer)
        val opponentMoves = availableMoves(getOpponent())
        return when {
            moves > MAX_MOVES -> Player.DRAW
            playerMoves.isEmpty() && opponentMoves.isEmpty() -> Player.DRAW
            playerMoves.isEmpty() -> getOpponent()
            else -> currentPlayer
        }
    }

    // Returns the next player given a player (defaulting to currentPlayer), ignoring the DRAW enum
    fun getOpponent(player: Player = currentPlayer): Player {
        return when(player) {
            Player.BLUE -> Player.RED
            Player.RED -> Player.BLUE
            Player.DRAW -> throw InternalError("Invalid player!")
        }
    }

    // Acquire all available moves for the given player, defaulting to the current one
    fun availableMoves(player: Player = currentPlayer): Array<Move> {
        assert(player != Player.DRAW)
        val moves = ArrayList<Move>()
        val pieces = playerPieces(player)
        // Check the surrounding of each piece and if it is free
        pieces.forEach{ piece ->
            for (i in -1..1) {
                for (j in -1..1) {
                    val row = piece.first + i
                    val col = piece.second + j
                    val newPosition = Pair(row, col)
                    if (validPosition(newPosition) &&
                        newPosition !in bluePieces &&
                        newPosition !in redPieces) {
                        moves.add(Move(piece, newPosition))
                    }
                }
            }
        }
        return moves.toTypedArray()
    }

    // Returns a players pieces, or the current player's by default
    fun playerPieces(player: Player = currentPlayer): ArrayList<Pair<Int, Int>> {
        return if (player == Player.BLUE) bluePieces else redPieces
    }

    // Applies a move for a player, asserting that it is valid, and performs the shuffle
    // Assumes to be the currentPlayer unless otherwise stated
    fun applyMove(move: Move, player: Player = currentPlayer) {
        val pieces = playerPieces(player)
        assert(move.from in pieces)
        assert(validPosition(move.to))
        assert(!isOccupied(move.to))
        pieces.remove(move.from)
        pieces.add(move.to)
        shuffle()
    }

    //Performs a game of life shuffle as described in GameOfLife.kt
    private fun shuffle() {
        val newBluePieces = ArrayList<Pair<Int, Int>>()
        val newRedPieces = ArrayList<Pair<Int, Int>>()
        for (i in 1..SIZE) {
            for (j in 1..SIZE) {
                val position = Pair(i, j)
                val neighbours = neighbours(position)
                val totalNeighbours = neighbours.first + neighbours.second
                // Only pieces with 2 or 3 neighbours will live
                if ((totalNeighbours == 2 || totalNeighbours == 3) && isOccupied(position)) {
                    if (position in bluePieces) {
                        newBluePieces.add(position)
                    } else {
                        newRedPieces.add(position)
                    }
                    // Else, determine if a new piece will spawn
                } else if (totalNeighbours == 3) {
                    // Determine if more blue or red neighbours
                    if (neighbours.first > neighbours.second) {
                        newBluePieces.add(position)
                    } else {
                        newRedPieces.add(position)
                    }
                }
            }
        }
        bluePieces = newBluePieces
        redPieces = newRedPieces
    }

    // Returns whether a position is occupied by any piece
    private fun isOccupied(position: Pair<Int, Int>): Boolean {
        return position in bluePieces || position in redPieces
    }

    // Given a position, returns the amount of blue and red neighbours, ignoring itself
    private fun neighbours(position: Pair<Int, Int>): Pair<Int, Int> {
        var blue = 0
        var red = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val neighbour = Pair(position.first + i, position.second + j)
                // Ignores its own position
                if (!(i == 0 && j == 0) && validPosition(neighbour)) {
                    when (neighbour) {
                        in bluePieces -> blue++
                        in redPieces -> red++
                    }
                }
            }
        }
        return Pair(blue, red)
    }

    // Checks whether a position is valid on the board
    fun validPosition(position: Pair<Int, Int>): Boolean {
        return (position.first >= 1 && position.second >= 1 &&
                position.first <= SIZE && position.second <= SIZE)
    }

    //Return a deep copy of the board to allow for simulations
    fun getCopy(): Board {
        val board = Board(moves = moves, currentPlayer = currentPlayer, player1 = player1, player2 = player2)
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        return board
    }

    // Override the string function to print the board
    override fun toString(): String {
        val board = StringBuilder()
        board.append("  ")
        for (i in 1..SIZE) {
            board.append("$i")
        }
        board.append("\n")
        for (i in 1..SIZE) {
            board.append("$i|")
            for (j in 1..SIZE) {
                val square = Pair(i, j)
                board.append(when (square) {
                        in bluePieces -> BLUEPIECE
                        in redPieces -> REDPIECE
                        else -> EMPTYSQUARE
                    })
            }
            board.append("|")
            board.append("\n")
        }
        board.append(" " + "‾".repeat(SIZE + 2))
        return board.toString()
    }
}