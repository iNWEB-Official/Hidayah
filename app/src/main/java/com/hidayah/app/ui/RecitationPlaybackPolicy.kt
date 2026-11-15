package com.hidayah.app.ui

internal enum class RecitationPhase { IDLE, ENDED, OTHER }
internal enum class RecitationToggleAction { PAUSE, RESUME, PREPARE_AND_PLAY, RESTART_AND_PLAY }

/** A pending play request can be paused even while buffering or audio focus is suppressed. */
internal fun recitationToggleAction(hasError: Boolean, playWhenReady: Boolean, phase: RecitationPhase): RecitationToggleAction = when {
    hasError -> RecitationToggleAction.PREPARE_AND_PLAY
    phase == RecitationPhase.ENDED -> RecitationToggleAction.RESTART_AND_PLAY
    playWhenReady -> RecitationToggleAction.PAUSE
    phase == RecitationPhase.IDLE -> RecitationToggleAction.PREPARE_AND_PLAY
    else -> RecitationToggleAction.RESUME
}
