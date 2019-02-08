package tool

import enumerate.Action

abstract class RootNode {
    abstract fun mcts(): Action
    abstract fun createNode(myActions: List<Action>)
}