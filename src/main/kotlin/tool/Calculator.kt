package tool

import enumerate.Action
import struct.FrameData
import struct.GameData
import java.util.*
import kotlin.math.min

class Calculator(var motoFrame: FrameData, preAct: Action, gameData: GameData, var player: Boolean) {
    var simulator = gameData.simulator
    var map = HashMap<Pair<Action, Action>, FrameData>()
    val myMotionData = gameData.getMotionData(player)
    val enMotionData = gameData.getMotionData(!player)
    var airThrowActions = ArrayList<Action>()
    var landThrowActions = ArrayList<Action>()
    var nonActionFrame = getFrame(preAct, Action.NEUTRAL)

    companion object {
        const val MAX_SIMULATION_TIME = 60
    }

    init {
        ActionGroup.AIR_ALL.forEach {
            val mo = myMotionData[it.ordinal]
            if (mo.getAttackSpeedX() != 0 || mo.getAttackSpeedY() != 0)
                airThrowActions.add(it)
        }
        ActionGroup.LAND_ALL.forEach {
            val mo = myMotionData[it.ordinal]
            if (mo.getAttackSpeedX() != 0 || mo.getAttackSpeedY() != 0)
                landThrowActions.add(it)
        }
    }

    fun isEnergyEnough(act: Action, isMe: Boolean): Boolean {
        val motionData = if (isMe) myMotionData else enMotionData
        val characterData = motoFrame.getCharacter(isMe)
        return motionData[act.ordinal].getAttackStartAddEnergy() + characterData.energy >= 0

    }

    fun isHittable() {

    }

    fun findActionsByEnergy(player: Boolean, vararg acts: Action): List<Action> {
        val possibleActions = LinkedList<Action>()
        acts.forEach {
            if (isEnergyEnough(it, player))
                possibleActions.add(it)
        }
        return possibleActions
    }

    fun getFrame(myAction: Action, enAction: Action): FrameData {
        val mAction = if (isEnergyEnough(myAction, true)) myAction else Action.NEUTRAL
        val eAction = if (isEnergyEnough(enAction, false)) enAction else Action.NEUTRAL
        val key = Pair(mAction, eAction)
        if (!map.containsKey(key)) {
            val myActions = ArrayDeque(listOf(mAction))
            val opActions = ArrayDeque(listOf(eAction))
            val value = this.simulator.simulate(motoFrame, player, myActions, opActions, MAX_SIMULATION_TIME)
            map[key] = value
        }
        return map[key]!!
    }

    fun computeScore(myAction: Action, enAction: Action = Action.NEUTRAL): Double {
        val fd = getFrame(myAction, enAction)
        return (fd.getCharacter(player).hp - nonActionFrame.getCharacter(player).hp).toDouble() - (fd.getCharacter(!player).hp - nonActionFrame.getCharacter(!player).hp).toDouble()
    }

    fun findMinScoreIfHadouken(myAction: Action): Double {
        var minScore = 9999.0
        landThrowActions.forEach {
            minScore = min(minScore, computeScore(myAction, it))
        }
        return minScore
    }

    fun findMinScore(myAction: Action, enActions: List<Action>): Double {
        var minScore = 9999.0
        enActions.forEach {
            val score = computeScore(myAction, it)
            minScore = min(minScore, score)
        }
        return minScore
    }

    fun getMinMaxIfHadouken(actions: List<Action>): Action {
        var maxScore = -9999.0
        var actionWithMaxScore = Action.FORWARD_WALK
        actions.forEach {
            val score = findMinScoreIfHadouken(it)
            if (score > maxScore) {
                maxScore = score
                actionWithMaxScore = it
            }
        }
        return actionWithMaxScore
    }
}
