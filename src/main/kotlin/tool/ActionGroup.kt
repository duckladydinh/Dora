package tool

import enumerate.Action

object ActionGroup {
    val AIR_ALL = arrayOf(Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB, Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA, Action.AIR_D_DF_FB, Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA, Action.AIR_D_DB_BB)
    val LAND_ALL = arrayOf(Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH, Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.THROW_B, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB, Action.STAND_F_D_DFA, Action.STAND_F_D_DFB, Action.STAND_D_DF_FC , Action.STAND_D_DB_BB, Action.THROW_A)// THROW_A
    val AIR_FREE = arrayOf(Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DB, Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA)
    val LAND_FREE = arrayOf(Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH, Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_F_D_DFA)// removed
}
