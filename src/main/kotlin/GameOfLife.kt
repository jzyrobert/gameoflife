/*
    A 2 player(Blue vs Red) game consisting of a square board (default size 8x8 and 12 pieces per player)
    Each board generates the pieces in random positions which cannot overlap
    Blue has the starting move.

    A valid move consists of a piece moving to a neighbour position which is not occupied
    After a each move, a shuffle according to Conway's game of life is performed.
    This occurs as follows:
        - A piece with < 2 neighbours will die
        - A piece with 2-3 neighbours will survive
        - A piece with > 3 neighbours will die
        - An empty square with exactly 3 neighbours will live again, with the colour determined by the colour majority
          (eg 3 Blue neighbours -> new blue cell, 2 Red & 1 Blue neighbours -> new red cell)
    The game is won by a player if they have alive pieces and the opponent does not.
    The inability to move, both players having 0 pieces, or exceeding a set amount of moves (default 250) causes a draw.
 */
/*
    fun main() {
        var repeat = true
        while (repeat) {
            println("Welcome to the Game of Life boardgame")
            println()
            val player1 = choosePlayer(Player.BLUE)
            println()
            val player2 = choosePlayer(Player.RED)
            println()
            val board = Board(player1 = player1, player2 = player2)
            board.play()
            repeat = playAgain()
        }

    }
        */
//Simulate X bot games

fun main() {
    val x = 1000
    val p1 = Strategies.OPTIMAL
    val p2 = Strategies.RANDOM
    var moves = 0
    var bluewin = 0
    var redwin = 0
    var draws = 0
    for (i in 1..x) {
        println(i)
        val board = Board(player1 = p1, player2 = p2)
        board.simulate()
        when (board.gameResult()) {
            Player.BLUE -> bluewin++
            Player.RED -> redwin++
            Player.DRAW -> draws++
        }
        moves += board.moves
    }
    println("Played $x games of $p1 vs $p2")
    println("BLUE won $bluewin times")
    println("RED won $redwin times")
    println("There were $draws draws")
    println("The average game took ${moves / x} moves")
}