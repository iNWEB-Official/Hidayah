package com.hidayah.app

import com.hidayah.app.ui.RecitationPhase
import com.hidayah.app.ui.RecitationToggleAction
import com.hidayah.app.ui.recitationToggleAction
import org.junit.Assert.assertEquals
import org.junit.Test

class RecitationPlaybackPolicyTest {
    @Test fun pauseAlsoWorksDuringBufferingOrAudioFocusSuppression() {
        assertEquals(RecitationToggleAction.PAUSE, recitationToggleAction(false, true, RecitationPhase.OTHER))
        assertEquals(RecitationToggleAction.RESUME, recitationToggleAction(false, false, RecitationPhase.OTHER))
    }
    @Test fun retryAndEndedQueueHaveDistinctActions() {
        assertEquals(RecitationToggleAction.PREPARE_AND_PLAY, recitationToggleAction(true, true, RecitationPhase.IDLE))
        assertEquals(RecitationToggleAction.PREPARE_AND_PLAY, recitationToggleAction(false, false, RecitationPhase.IDLE))
        assertEquals(RecitationToggleAction.RESTART_AND_PLAY, recitationToggleAction(false, true, RecitationPhase.ENDED))
    }
}
