package com.hidayah.app.data

enum class AvatarStyle { NEUTRAL, BOY, GIRL, MAN, WOMAN, SENIOR_MAN, SENIOR_WOMAN }
data class ProfileAvatar(val id: String, val label: String, val style: AvatarStyle)

/** Choosing an avatar is optional and does not record a user's actual age or gender. */
val ProfileAvatars = listOf(
    ProfileAvatar("neutral", "সাধারণ", AvatarStyle.NEUTRAL),
    ProfileAvatar("boy", "ছেলে শিশু", AvatarStyle.BOY),
    ProfileAvatar("girl", "মেয়ে শিশু", AvatarStyle.GIRL),
    ProfileAvatar("man", "প্রাপ্তবয়স্ক পুরুষ", AvatarStyle.MAN),
    ProfileAvatar("woman", "প্রাপ্তবয়স্ক নারী", AvatarStyle.WOMAN),
    ProfileAvatar("senior_man", "প্রবীণ পুরুষ", AvatarStyle.SENIOR_MAN),
    ProfileAvatar("senior_woman", "প্রবীণ নারী", AvatarStyle.SENIOR_WOMAN),
)
fun profileAvatar(id: String?): ProfileAvatar = ProfileAvatars.firstOrNull { it.id == id } ?: ProfileAvatars.first()
