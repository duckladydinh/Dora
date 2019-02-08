package tool

import enumerate.Action
import struct.FrameData
import struct.GameData
import java.util.*
import kotlin.math.min

data class Node (
        var executionTime: Long,
        var originalFrameData: FrameData,
        val parent: Node?,
        val gameData: GameData,
        var player: Boolean,
        var myAvailableActions: List<Action>,
        var enAvailableActions: List<Action>,
        var mySelectedActions: List<Action> = LinkedList()): RootNode() {

    private val depth: Int = if (this.parent == null) 0 else parent.depth + 1
    private var children = ArrayList<Node>()
    private val rnd = SplittableRandom()
    private var nKnownChildren = 0
    private var totalScore = 0.0
    private var myOriginalHP = 0
    private var enOriginalHP = 0
    private var nVisit = 0

    companion object {
        const val UCT_MAX_VISITED_CHILDREN = 10
        const val UCT_MAX_SIMULATION_TIME = 60
        const val UCB_BALANCE_FACTOR = 3.0
        const val UCT_MAX_TREE_DEPTH = 2
    }

    init {
        val myCharacter = originalFrameData.getCharacter(player)
        val oppCharacter = originalFrameData.getCharacter(!player)
        myOriginalHP = myCharacter.hp
        enOriginalHP = oppCharacter.hp
    }

    private fun findMostVisitedAction(): Action {
        var selected = -1
        var bestGames = -9999.0
        repeat (children.size) {
            if (bestGames < children[it].nVisit) {
                bestGames = children[it].nVisit.toDouble()
                selected = it
            }
        }
        return children[selected].mySelectedActions.first()
    }

    private fun createNodeByAction(action: Action): Node {
        return Node(this.executionTime, originalFrameData,this, gameData, player, myAvailableActions, enAvailableActions, mySelectedActions + action)
    }

    private fun computeScore(frameData: FrameData): Int {
        return (frameData.getCharacter(player).hp - myOriginalHP) - (frameData.getCharacter(!player).hp - enOriginalHP)
    }

    private fun computePotential(score: Double, n_parent_visit: Int, n_current_visit: Int): Double {
        return score + UCB_BALANCE_FACTOR * Math.sqrt(2 * Math.log(n_parent_visit.toDouble()) / n_current_visit)
    }

    private fun selectBestChild(): Int {
        if (children.isEmpty()) {
            createNode(myAvailableActions)
        }

        val limit = min(children.size, Node.UCT_MAX_VISITED_CHILDREN)
        if (nKnownChildren < limit) {
            val index = nKnownChildren + rnd.nextInt(limit - nKnownChildren)
            Collections.swap(children, nKnownChildren, index)
            nKnownChildren += 1

            return nKnownChildren - 1
        } else {
            var selectedIndex = 0
            var maxPotential = -9999.0

            repeat(nKnownChildren) {
                val child = children[it]
                val childPotential = computePotential(child.totalScore / child.nVisit, nVisit, child.nVisit)
                if (maxPotential < childPotential) {
                    maxPotential = childPotential
                    selectedIndex = it
                }
            }

            return selectedIndex
        }
    }

    private fun playout(): Double {
        val myActions = ArrayDeque(mySelectedActions)
        val enActions = ArrayDeque<Action>()

        repeat (5 - mySelectedActions.size) {
            myActions.add(myAvailableActions[rnd.nextInt(myAvailableActions.size)])
        }
        repeat (5) {
            enActions.add(enAvailableActions[rnd.nextInt(enAvailableActions.size)])
        }

        val predictedFrameData = gameData.simulator.simulate(originalFrameData, player, myActions, enActions, Node.UCT_MAX_SIMULATION_TIME)
        return computeScore(predictedFrameData).toDouble()
    }

    private fun uct(): Double {
        val selectedIndex = selectBestChild()
        val selectedChild = children[selectedIndex]

        val score = if (selectedChild.nVisit != 0 && selectedChild.depth < Node.UCT_MAX_TREE_DEPTH && (selectedChild.children.isNotEmpty() || Node.UCT_MAX_VISITED_CHILDREN <= selectedChild.nVisit)) {
            selectedChild.uct()
        } else {
            selectedChild.playout()
        }

        selectedChild.apply {
            totalScore += score
            nVisit++
        }

        if (depth == 0) {
            ++nVisit
        }

        return score
    }

    override fun mcts(): Action {
        val start = System.nanoTime()
        while (System.nanoTime() <= start + executionTime) {
            uct()
        }
        return findMostVisitedAction()
    }


    override fun createNode(myActions: List<Action>) {
        children.clear()
        myActions.forEach {
            children.add(createNodeByAction(it))
        }
    }
}
