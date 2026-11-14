package com.hidayah.app.data

/** A small sourced offline collection; the Bengali wording needs qualified editorial review. */
data class DuaItem(
    val id: String,
    val title: String,
    val category: String,
    val arabic: String,
    val pronunciation: String,
    val meaning: String,
    val source: String,
    val sourceUrl: String,
)

val DuaItems = listOf(
    DuaItem("wakeup", "ঘুম থেকে ওঠার দোয়া", "সকাল", "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ", "আলহামদু লিল্লাহিল্লাযী আহইয়ানা বা‘দা মা আমাতানা ওয়া ইলাইহিন নুশূর।", "সব প্রশংসা আল্লাহর, যিনি আমাদের মৃত্যুর পর জীবন দান করেছেন; তাঁর কাছেই পুনরুত্থান।", "সহীহ বুখারী ৬৩১২", "https://sunnah.com/bukhari:6312"),
    DuaItem("before_eating", "খাবার শুরুতে", "খাবার", "بِسْمِ اللَّهِ", "বিসমিল্লাহ।", "আল্লাহর নামে।", "সুনান আবু দাউদ ৩৭৬৭", "https://sunnah.com/abudawud:3767"),
    DuaItem("after_eating", "খাবার শেষের দোয়া", "খাবার", "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ", "আলহামদু লিল্লাহিল্লাযী আতআমানী হাযা, ওয়া রাযাকানীহি মিন গাইরি হাওলিম মিন্নী ওয়া লা কুওয়াহ।", "সমস্ত প্রশংসা আল্লাহর, যিনি আমাকে এই খাবার দিয়েছেন এবং আমার কোনো শক্তি ও সামর্থ্য ছাড়াই তা আমাকে রিজিক হিসেবে দিয়েছেন।", "জামে তিরমিযী ৩৪৫৮", "https://sunnah.com/tirmidhi:3458"),
    DuaItem("leaving_home", "বাড়ি থেকে বের হওয়ার দোয়া", "বাইরে", "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "বিসমিল্লাহি, তাওয়াক্কালতু আলাল্লাহি, ওয়া লা হাওলা ওয়া লা কুওয়াতা ইল্লা বিল্লাহ।", "আল্লাহর নামে বের হচ্ছি; আল্লাহর ওপর ভরসা করলাম। আল্লাহ ছাড়া কোনো শক্তি ও সামর্থ্য নেই।", "রিয়াদুস সালিহীন ৮৩", "https://sunnah.com/riyadussalihin:83"),
    DuaItem("before_sleep", "ঘুমানোর আগে", "ঘুম", "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا", "বিসমিকা আল্লাহুম্মা আমূতু ওয়া আহইয়া।", "হে আল্লাহ, আপনার নামেই আমি মৃত্যু বরণ করি ও জীবন লাভ করি।", "সহীহ বুখারী ৬৩২৪", "https://sunnah.com/bukhari:6324"),
    DuaItem("bathroom", "শৌচাগারে প্রবেশের আগে", "দৈনন্দিন", "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ", "আল্লাহুম্মা ইন্নী আউযু বিকা মিনাল খুবুসি ওয়াল খাবাইস।", "হে আল্লাহ, আমি আপনার কাছে অপবিত্র পুরুষ ও নারী শয়তান থেকে আশ্রয় চাই।", "সহীহ বুখারী ৬৩২২", "https://sunnah.com/bukhari:6322"),
    DuaItem("iftar", "ইফতারের পরের দোয়া", "রমজান", "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ", "যাহাবায যামাউ ওয়াবতাল্লাতিল উরূকু ওয়া সাবাতাল আজরু ইনশাআল্লাহ।", "পিপাসা দূর হয়েছে, শিরা-উপশিরা সিক্ত হয়েছে এবং আল্লাহ চাইলে সওয়াব নিশ্চিত হয়েছে।", "সুনান আবু দাউদ ২৩৫৭", "https://sunnah.com/abudawud:2357"),
    DuaItem("tasbih", "আল্লাহর প্রশংসা ও পবিত্রতা", "জিকির", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "সুবহানাল্লাহি ওয়া বিহামদিহি।", "আল্লাহ পবিত্র; তাঁরই প্রশংসা।", "সহীহ বুখারী ৬৪০৫", "https://sunnah.com/bukhari:6405"),
    DuaItem("rabbana", "দুনিয়া ও আখিরাতের কল্যাণ", "দোয়া", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", "রব্বানা আতিনা ফিদ্দুনইয়া হাসানাতাঁও ওয়া ফিল আখিরাতি হাসানাতাঁও ওয়া কিনা আযাবান্নার।", "হে আমাদের প্রতিপালক, আমাদের দুনিয়ায় কল্যাণ দিন, আখিরাতেও কল্যাণ দিন এবং আগুনের শাস্তি থেকে রক্ষা করুন।", "কুরআন ২:২০১", "https://quran.com/2/201"),
)

val DuaCategories = listOf("সব", "পছন্দ", "সকাল", "খাবার", "বাইরে", "ঘুম", "দৈনন্দিন", "রমজান", "জিকির", "দোয়া")

/** Offline filtering only; search does not transmit personal interests to a server. */
fun searchDuas(query: String, category: String, favoriteIds: Set<String>): List<DuaItem> {
    fun normalize(text: String) = text.trim().lowercase().map { if (it in '০'..'৯') '0' + (it - '০') else it }.joinToString("")
    val needle = normalize(query)
    return DuaItems.filter { dua ->
        val inCategory = when (category) { "সব" -> true; "পছন্দ" -> dua.id in favoriteIds; else -> dua.category == category }
        inCategory && (needle.isEmpty() || listOf(dua.title, dua.category, dua.arabic, dua.pronunciation, dua.meaning, dua.source).any { normalize(it).contains(needle) })
    }
}

fun formatDuaShare(dua: DuaItem): String = listOf(dua.title, dua.arabic, "উচ্চারণ: ${dua.pronunciation}", "অর্থ: ${dua.meaning}", "সূত্র: ${dua.source}", dua.sourceUrl).joinToString("\n\n")
