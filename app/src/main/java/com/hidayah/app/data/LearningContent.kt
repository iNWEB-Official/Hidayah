package com.hidayah.app.data

/** Bengali summaries are paraphrases, not direct Quran translations. */
data class LearningStory(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val reflection: String,
    val source: String,
    val url: String,
)

val Stories = listOf(
    LearningStory(
        "musa", "মূসা (আ.) ও সমুদ্র", "আল্লাহর সাহায্য",
        "আল্লাহ মূসা (আ.)-কে তাঁর লাঠি দিয়ে সমুদ্রে আঘাত করার নির্দেশ দেন। সমুদ্র বিভক্ত হয়; আল্লাহ মূসা (আ.) ও তাঁর সঙ্গীদের রক্ষা করেন।",
        "কঠিন পরিস্থিতিতে আল্লাহর ওপর ভরসা ও তাঁর নির্দেশ মানার গুরুত্ব নিয়ে ভাবুন।",
        "কুরআন ২৬:৬৩–৬৫", "https://quran.com/26/63"
    ),
    LearningStory(
        "yunus", "ইউনুস (আ.)-এর প্রার্থনা", "প্রার্থনা",
        "কুরআনে ইউনুস (আ.)-এর অন্ধকারে আল্লাহকে ডাকার কথা এসেছে। তিনি আল্লাহর পবিত্রতা ঘোষণা করেন এবং নিজের ভুল স্বীকার করেন। আল্লাহ তাঁর ডাকে সাড়া দিয়ে তাঁকে দুঃখ থেকে মুক্ত করেন।",
        "ভুল স্বীকার করে আল্লাহর কাছে ফিরে আসা ও তাঁর কাছে প্রার্থনার কথা মনে রাখুন।",
        "কুরআন ২১:৮৭–৮৮", "https://quran.com/21/87"
    ),
    LearningStory(
        "yusuf", "ইউসুফ (আ.)-এর ক্ষমা", "ক্ষমা ও ধৈর্য",
        "ভাইয়েরা নিজেদের অপরাধের কথা স্বীকার করলে ইউসুফ (আ.) তাঁদের বলেন, আজ তাঁদের প্রতি কোনো তিরস্কার নেই। তিনি তাঁদের জন্য আল্লাহর ক্ষমার কথা বলেন।",
        "অন্যের ভুলের মুখোমুখি হলে ক্ষমা ও সদাচরণের গুরুত্ব নিয়ে ভাবুন।",
        "কুরআন ১২:৯১–৯২", "https://quran.com/12/91"
    ),
    LearningStory(
        "nuh", "নূহ (আ.) ও নৌকা", "আল্লাহর সাহায্য",
        "আল্লাহ নূহ (আ.)-কে তাঁর নির্দেশনায় নৌকা তৈরি করতে বলেন। নূহ (আ.) নৌকা তৈরি করতে থাকেন; তাঁর সম্প্রদায়ের নেতারা পাশ দিয়ে যাওয়ার সময় তাঁকে বিদ্রূপ করত।",
        "বিদ্রূপের মধ্যেও সঠিক কাজে অবিচল থাকার বিষয়টি নিয়ে ভাবুন।",
        "কুরআন ১১:৩৭–৩৮", "https://quran.com/11/37"
    ),
    LearningStory(
        "luqman", "লুকমানের উপদেশ", "জীবনের শিক্ষা",
        "লুকমান তাঁর সন্তানকে সালাত প্রতিষ্ঠা, সৎকাজের নির্দেশ, অসৎকাজে নিষেধ এবং বিপদে ধৈর্য ধরার উপদেশ দেন।",
        "নিজের জীবনে সালাত ও ধৈর্যের চর্চার সুযোগগুলো নিয়ে ভাবুন।",
        "কুরআন ৩১:১৭", "https://quran.com/31/17"
    ),
)
val StoryCategories = listOf("সব", "আল্লাহর সাহায্য", "প্রার্থনা", "ক্ষমা ও ধৈর্য", "জীবনের শিক্ষা")

private fun normalizeLearning(text: String): String = text.trim().lowercase().map {
    if (it in '০'..'৯') '0' + (it - '০') else it
}.joinToString("")

fun searchStories(query: String, category: String): List<LearningStory> {
    val needle = normalizeLearning(query)
    return Stories.filter { story ->
        (category == "সব" || story.category == category) &&
            (needle.isEmpty() || listOf(story.title, story.category, story.summary, story.reflection, story.source).any { normalizeLearning(it).contains(needle) })
    }
}

data class QuizQuestion(
    val id: String,
    val question: String,
    val answers: List<String>,
    val correct: Int,
    val explanation: String,
    val source: String,
    val url: String,
)
val QuizQuestions = listOf(
    QuizQuestion("sea", "কুরআনের বর্ণনায় সমুদ্রে লাঠি দিয়ে আঘাত করার নির্দেশ কে পেয়েছিলেন?", listOf("ইউসুফ (আ.)", "মূসা (আ.)", "ইউনুস (আ.)"), 1, "আল্লাহ মূসা (আ.)-কে তাঁর লাঠি দিয়ে সমুদ্রে আঘাত করার নির্দেশ দেন।", "কুরআন ২৬:৬৩", "https://quran.com/26/63"),
    QuizQuestion("prayer", "অন্ধকারে আল্লাহর কাছে প্রার্থনার ঘটনা কার?", listOf("ইউনুস (আ.)", "নূহ (আ.)", "মূসা (আ.)"), 0, "ইউনুস (আ.) অন্ধকারে আল্লাহকে ডেকেছিলেন; আল্লাহ তাঁর ডাকে সাড়া দেন।", "কুরআন ২১:৮৭–৮৮", "https://quran.com/21/87"),
    QuizQuestion("forgive", "ভাইদের প্রতি তিরস্কার না করার কথা কে বলেছিলেন?", listOf("মূসা (আ.)", "ইউনুস (আ.)", "ইউসুফ (আ.)"), 2, "ইউসুফ (আ.) তাঁর ভাইদের বলেন যে সেদিন তাঁদের প্রতি কোনো তিরস্কার নেই।", "কুরআন ১২:৯২", "https://quran.com/12/92"),
    QuizQuestion("ark", "নৌকা তৈরির নির্দেশ কে পেয়েছিলেন?", listOf("ইউসুফ (আ.)", "নূহ (আ.)", "লুকমান"), 1, "আল্লাহ নূহ (আ.)-কে তাঁর নির্দেশনায় নৌকা নির্মাণ করতে বলেন।", "কুরআন ১১:৩৭", "https://quran.com/11/37"),
    QuizQuestion("advice", "লুকমান তাঁর সন্তানকে নিচের কোন উপদেশ দেন?", listOf("সালাত প্রতিষ্ঠা ও ধৈর্য ধরা", "বিদ্রূপের জবাবে প্রতিশোধ নেওয়া", "সৎকাজ থেকে দূরে থাকা"), 0, "লুকমান সালাত প্রতিষ্ঠা ও বিপদে ধৈর্য ধরার পাশাপাশি সৎকাজের নির্দেশ এবং অসৎকাজে নিষেধের উপদেশ দেন।", "কুরআন ৩১:১৭", "https://quran.com/31/17"),
)

/** State is kept for this screen/session, not stored permanently or transmitted. */
data class QuizProgress(val position: Int = 0, val answers: List<Int> = List(QuizQuestions.size) { -1 }) {
    val complete: Boolean get() = position >= QuizQuestions.size
    val score: Int get() = answers.take(QuizQuestions.size).mapIndexed { i, answer -> if (answer == QuizQuestions[i].correct) 1 else 0 }.sum()
}

fun answerQuiz(progress: QuizProgress, answer: Int): QuizProgress {
    if (progress.complete || progress.position !in QuizQuestions.indices || progress.answers.size != QuizQuestions.size ||
        progress.answers[progress.position] != -1 || answer !in QuizQuestions[progress.position].answers.indices) return progress
    return progress.copy(answers = progress.answers.toMutableList().apply { this[progress.position] = answer })
}
fun advanceQuiz(progress: QuizProgress): QuizProgress {
    if (progress.complete || progress.position !in QuizQuestions.indices || progress.answers.size != QuizQuestions.size || progress.answers[progress.position] !in QuizQuestions[progress.position].answers.indices) return progress
    return progress.copy(position = progress.position + 1)
}
