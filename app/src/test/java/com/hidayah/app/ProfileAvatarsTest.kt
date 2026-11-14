package com.hidayah.app

import com.hidayah.app.data.AvatarStyle
import com.hidayah.app.data.ProfileAvatars
import com.hidayah.app.data.profileAvatar
import org.junit.Assert.*
import org.junit.Test

class ProfileAvatarsTest {
    @Test fun includesAChoiceForEveryBuiltInStyle() {
        assertEquals(7, ProfileAvatars.size)
        assertEquals(AvatarStyle.entries.toSet(), ProfileAvatars.map { it.style }.toSet())
        assertEquals(ProfileAvatars.size, ProfileAvatars.map { it.id }.toSet().size)
        assertTrue(ProfileAvatars.all { it.label.isNotBlank() })
    }
    @Test fun unknownAndMissingSelectionsUseNeutral() {
        assertEquals(AvatarStyle.NEUTRAL, profileAvatar(null).style)
        assertEquals(AvatarStyle.NEUTRAL, profileAvatar("not_available").style)
    }
    @Test fun eachSelectionResolvesToTheSameItem() {
        ProfileAvatars.forEach { assertEquals(it, profileAvatar(it.id)) }
    }
}
