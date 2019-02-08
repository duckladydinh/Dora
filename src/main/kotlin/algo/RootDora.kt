package algo


import enumerate.Action
import enumerate.State
import struct.*
import tool.Calculator
import tool.ActionGroup
import tool.Node
import tool.RootNode
import java.util.*

abstract class RootDora(val gameData: GameData, val player: Boolean) {
    lateinit var enCharacter: CharacterData
    lateinit var myCharacter: CharacterData
    lateinit var actionLand: Array<Action>
    lateinit var actionAir: Array<Action>
    lateinit var realFrameData: FrameData
    lateinit var calculator: Calculator
    lateinit var myAvailableActions: Array<Action>
    lateinit var enAvailableActions: Array<Action>
    lateinit var myActionsEnoughEnergy: List<Action>
    lateinit var enActionsEnoughEnergy: List<Action>

    var distance = 0
    var startTime = 0L
    var bestScore = 0.0
    var bestAction: Action? = null
    var chosenLoopActions = ArrayList<Action>()
    var simulator = gameData.simulator!!
    var myMotionData = gameData.getMotionData(player)!!

    abstract val prepareAction: Action

    init {
        setRootActions()
    }

    open fun setRootActions() {
        this.actionLand = ActionGroup.LAND_ALL
        this.actionAir = ActionGroup.AIR_FREE
    }

    fun chooseLoopActions() {
        bestScore = -9999.0
        bestAction = null
        chosenLoopActions.clear()

        for (action in myActionsEnoughEnergy) {
            val score = calculator.computeScore(action)
            val mo = myMotionData[action.ordinal]

            if (score > 0) {
                chosenLoopActions.add(action)
                val priorityScore = (score + (if (mo.isAttackDownProp) 30.0 else 0.0) - mo.attackStartUp * 0.01 - mo.cancelAbleFrame * 0.0001)
                if (priorityScore > bestScore) {
                    bestScore = priorityScore
                    bestAction = action
                }
            }
        }
    }

    fun initialize(frameData: FrameData) {
        startTime = System.nanoTime()
        if (frameData.framesNumber >= 0) {
            if (frameData.framesNumber < 14) {
                realFrameData = FrameData(frameData)
            } else {
                realFrameData = simulator.simulate(frameData, this.player, null, null, 14)
            }

            distance = realFrameData.distanceX
            myCharacter = realFrameData.getCharacter(player)
            enCharacter = realFrameData.getCharacter(!player)

            myAvailableActions = if (myCharacter.state != State.AIR) actionLand else actionAir
            enAvailableActions = if (enCharacter.state != State.AIR) ActionGroup.LAND_ALL else ActionGroup.AIR_ALL

            calculator = Calculator(realFrameData, Action.NEUTRAL, gameData, player)
            myActionsEnoughEnergy = calculator.findActionsByEnergy(true, *myAvailableActions)
            enActionsEnoughEnergy = calculator.findActionsByEnergy(false, *enAvailableActions)

            chooseLoopActions()
        }
    }

    fun unattackingMode(): Action {
        if (myCharacter.state != State.AIR) {
            if (myCharacter.hp - enCharacter.hp > 200) {
                val possibleActions = calculator.findActionsByEnergy(true, Action.BACK_STEP, Action.JUMP, Action.FOR_JUMP, Action.FORWARD_WALK, Action.BACK_JUMP)
                return calculator.getMinMaxIfHadouken(possibleActions)
            } else {
                val possibleActions = if (distance < 300) {
                    calculator.findActionsByEnergy(true, Action.FOR_JUMP, Action.FORWARD_WALK, Action.JUMP, Action.BACK_JUMP, Action.BACK_STEP)
                } else {
                    calculator.findActionsByEnergy(true, Action.FORWARD_WALK, Action.FOR_JUMP, Action.JUMP, Action.BACK_JUMP, Action.BACK_STEP)
                }
                return calculator.getMinMaxIfHadouken(possibleActions)
            }
        } else {
            return Action.AIR_GUARD
        }
    }

    open fun getRootNode(simFrameData: FrameData): RootNode {
         return Node(166L * 100000 - (System.nanoTime() - startTime), simFrameData, null, gameData, player, myActionsEnoughEnergy, enActionsEnoughEnergy)
    }

    fun trainMCTS(simFrameData: FrameData, myStartActions: List<Action>): Action {
        val rootNode = getRootNode(simFrameData)
        rootNode.createNode(if (myStartActions.isEmpty()) myActionsEnoughEnergy else myStartActions)
        return rootNode.mcts()
    }
}
