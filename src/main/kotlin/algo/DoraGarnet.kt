package algo


import enumerate.Action
import enumerate.State
import struct.GameData

class DoraGarnet(gameData: GameData, player: Boolean) : RootDora(gameData, player) {

    override val prepareAction: Action
        get() {
            if (bestAction != null) {
                if (myCharacter.state != State.AIR) {
                    val possibleActions = calculator.findActionsByEnergy(false, Action.NEUTRAL, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB)
                    listOf(Action.STAND_B, Action.CROUCH_FB).forEach {
                        if (calculator.findMinScore(it, possibleActions) > 0) {
                            return it
                        }
                    }
                    if (!bestAction!!.name.contains("AIR")) {
                        return bestAction!!
                    }
                } else {
                    if (bestAction!!.name.contains("AIR")) {
                        return bestAction!!
                    }
                }
                return trainMCTS(realFrameData, chosenLoopActions)
            } else {
                return unattackingMode()
            }
        }
}
