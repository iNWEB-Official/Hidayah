package com.hidayah.app

import com.hidayah.app.data.*
import org.junit.Assert.*
import org.junit.Test

class LearningContentTest {
    @Test fun storiesAndQuestionsHaveUniqueIdsAndSources() {
        assertEquals(5, Stories.size)
        assertEquals(5, QuizQuestions.size)
        assertEquals(Stories.size, Stories.map { it.id }.toSet().size)
        assertEquals(QuizQuestions.size, QuizQuestions.map { it.id }.toSet().size)
        assertTrue(Stories.all { it.category in StoryCategories && it.title.isNotBlank() && it.summary.isNotBlank() && it.reflection.isNotBlank() && it.source.isNotBlank() && it.url.startsWith("https://quran.com/") })
        assertTrue(QuizQuestions.all { it.correct in it.answers.indices && it.answers.size >= 2 && it.answers.toSet().size == it.answers.size && it.explanation.isNotBlank() && it.source.isNotBlank() && it.url.startsWith("https://quran.com/") })
    }
    @Test fun storySearchUnderstandsCategoryTextAndBengaliDigits() {
        assertEquals(setOf("musa", "nuh"), searchStories("", "আল্লাহর সাহায্য").map { it.id }.toSet())
        assertEquals("yusuf", searchStories("১২:৯১", "সব").single().id)
        assertEquals("yusuf", searchStories("12:91", "সব").single().id)
        assertTrue(searchStories("মূসা", "প্রার্থনা").isEmpty())
        assertTrue(searchStories("অপরিচিত", "সব").isEmpty())
    }
    @Test fun cannotAdvanceWithoutAnswerOrAnswerInvalidIndex() {
        val start = QuizProgress()
        assertEquals(start, advanceQuiz(start))
        assertEquals(start, answerQuiz(start, -1))
        assertEquals(start, answerQuiz(start, 99))
    }
    @Test fun firstAnswerIsLockedAndScoreCountsOnce() {
        val start = QuizProgress()
        val answered = answerQuiz(start, QuizQuestions[0].correct)
        assertEquals(1, answered.score)
        assertEquals(answered, answerQuiz(answered, (QuizQuestions[0].correct + 1) % QuizQuestions[0].answers.size))
        assertEquals(1, advanceQuiz(answered).position)
    }
    @Test fun completeQuizAndRestart() {
        var quiz = QuizProgress()
        QuizQuestions.forEachIndexed { index, question ->
            assertEquals(index, quiz.position)
            val answer = if (index == 1) (question.correct + 1) % question.answers.size else question.correct
            quiz = advanceQuiz(answerQuiz(quiz, answer))
        }
        assertTrue(quiz.complete)
        assertEquals(QuizQuestions.size - 1, quiz.score)
        assertEquals(quiz, advanceQuiz(quiz))
        assertEquals(quiz, answerQuiz(quiz, 0))
        assertFalse(QuizProgress().complete)
        assertEquals(0, QuizProgress().score)
    }
    @Test fun corruptedAnswerListDoesNotCrashOrAdvance() {
        val corrupt = QuizProgress(position = 1, answers = emptyList())
        assertEquals(corrupt, answerQuiz(corrupt, 0))
        assertEquals(corrupt, advanceQuiz(corrupt))
    }
}
