import core.Game
import manager.DisplayManager

fun main(options: Array<String>) {
    val parameterStr = "  --a1 Dora --a2 Dora --c1 ZEN --c2 ZEN -n 1 "

    val game = Game()
    game.setOptions(if (options.isNullOrEmpty()) {
        parameterStr.trim().split(' ').toTypedArray()
    } else {
        options
    })

    val displayManager = DisplayManager()
    displayManager.start(game)
}
