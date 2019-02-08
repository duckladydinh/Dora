import aiinterface.AIInterface
import aiinterface.CommandCenter
import algo.DoraGarnet
import algo.DoraLud
import algo.DoraZen
import algo.RootDora
import struct.FrameData
import struct.GameData
import struct.Key

class Dora: AIInterface {
    private var inputKey = Key()
    private var player = false
    private var frameData = FrameData()
    private var commander = CommandCenter()


    var ai: RootDora? = null

    override fun getInformation(frameData: FrameData) {
        this.frameData = frameData
        ai!!.initialize(frameData)
    }

    override fun initialize(gameData: GameData, player: Boolean): Int {
        this.player = player
        val characterName = gameData.getCharacterName(player)

        this.ai = if (characterName == "ZEN") {
            DoraZen(gameData, player)
        } else if (characterName == "GARNET") {
            DoraGarnet(gameData, player)
        } else {
            DoraLud(gameData, player)
        }
        return 0
    }

    override fun input(): Key? {
        return inputKey
    }

    override fun processing() {
        if (!frameData.emptyFlag && frameData.framesNumber >= 0) {
            if (commander.skillFlag) {
                inputKey = commander.skillKey
            } else {
                inputKey.empty()
                commander.setFrameData(ai!!.realFrameData, player)
                commander.skillCancel()
                val commandName = ai!!.prepareAction.name
                commander.commandCall(commandName)
            }
        }

    }

    override fun roundEnd(a: Int, b: Int, c: Int) {}
    override fun close() {}
}
