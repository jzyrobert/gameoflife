import org.junit.Test
import kotlin.test.assertEquals

val bluePieces = arrayListOf(Pair(1,2),Pair(1,3),Pair(3,6),Pair(7,1),Pair(7,5),Pair(8,4))
val redPieces = arrayListOf(Pair(1,5),Pair(2,8),Pair(4, 7),Pair(6,5))

class MoveTest {
    @Test
    fun bloodlustRed() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.GREEDY)
        board.currentPlayer = Player.RED
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(5,4), Pair(5,6), Pair(5, 5))
        assertEquals(move.from, Pair(6,5))
        assert(move.to in toChoices)
    }

    @Test
    fun safeRed() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.SAFE)
        board.currentPlayer = Player.RED
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(3,7))
        assertEquals(move.from, Pair(2, 8))
        assert(move.to in toChoices)
    }

    @Test
    fun optimalRed() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.OPTIMAL)
        board.currentPlayer = Player.RED
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(5,6))
        assertEquals(move.from, Pair(6,5))
        assert(move.to in toChoices)
    }

    @Test
    fun minimaxRed() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.MINIMAX)
        board.currentPlayer = Player.RED
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(5,4), Pair(5,6), Pair(5, 5))
        assertEquals(move.from, Pair(6,5))
        assert(move.to in toChoices)
    }
    @Test
    fun bloodlustBlue() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.GREEDY)
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(3,5), Pair(2,5))
        assertEquals(move.from, Pair(3,6))
        assert(move.to in toChoices)
    }

    @Test
    fun safeBlue() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.SAFE)
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(2,3), Pair(2,5))
        assertEquals(move.from, Pair(1, 2))
        assert(move.to in toChoices)
    }

    @Test
    fun optimalBlue() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.OPTIMAL)
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(2,5))
        assertEquals(move.from, Pair(3,6))
        assert(move.to in toChoices)
    }

    @Test
    fun minimaxBlue() {
        val board = Board(player1 = Strategies.RANDOM, player2 = Strategies.MINIMAX)
        board.bluePieces = ArrayList(bluePieces)
        board.redPieces = ArrayList(redPieces)
        val move = getMove(board, board.player2)
        val toChoices = listOf(Pair(3,5), Pair(2,5))
        assertEquals(move.from, Pair(3,6))
        assert(move.to in toChoices)
    }
}