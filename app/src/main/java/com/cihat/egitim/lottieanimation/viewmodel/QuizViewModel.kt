package com.cihat.egitim.lottieanimation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.data.PublicQuiz
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.data.FolderHeading
import kotlin.math.min

/**
 * ViewModel that holds quizzes, each containing its own set of boxes and
 * questions. Also manages quiz progress state.
 */
class QuizViewModel : ViewModel() {

    /** Folders created by the user */
    var folders = mutableStateListOf<UserFolder>()
        private set

    private var nextFolderId = 0
    private var nextHeadingId = 0

    /** Quizzes created or imported by the user */
    var quizzes = mutableStateListOf<UserQuiz>()
        private set

    private var nextQuizId = 0

    /** Index of the quiz currently being viewed */
    private var currentQuizIndex by mutableStateOf(0)

    /** Convenient access to boxes of the current quiz */
    val boxes: MutableList<MutableList<Question>>
        get() = quizzes.getOrNull(currentQuizIndex)?.boxes ?: mutableListOf()

    /** Name of the active quiz */
    val currentQuizName: String
        get() = quizzes.getOrNull(currentQuizIndex)?.name ?: ""

    /** Sample public quizzes that could come from a backend in a real app */
    val publicQuizzes: List<PublicQuiz> = listOf(
        PublicQuiz(
            name = "Capital Cities",
            author = "Alice",
            questions = listOf(
                Question("Capital of France?", "Paris"),
                Question("Capital of Spain?", "Madrid")
            )
        ),
        PublicQuiz(
            name = "Math Basics",
            author = "Bob",
            questions = listOf(
                Question("2 + 2?", "4"),
                Question("5 * 3?", "15")
            )
        )
    )

    private var currentBoxIndex by mutableStateOf(0)
    private var currentQuestionIndex by mutableStateOf(0)

    var isAnswerVisible by mutableStateOf(false)
        private set

    /** Returns the heading at the given path, or null if not found */
    private fun getHeadingAt(list: MutableList<FolderHeading>, path: List<Int>): FolderHeading? {
        var current: FolderHeading? = null
        var currentList = list
        for (index in path) {
            current = currentList.getOrNull(index) ?: return null
            currentList = current.children
        }
        return current
    }

    /** Returns the parent list for the heading at path */
    private fun getHeadingParent(list: MutableList<FolderHeading>, path: List<Int>): MutableList<FolderHeading>? {
        if (path.isEmpty()) return list
        var currentList = list
        for (level in 0 until path.size - 1) {
            val index = path[level]
            val h = currentList.getOrNull(index) ?: return null
            currentList = h.children
        }
        return currentList
    }

    /** Renames the folder at the given index */
    fun renameFolder(index: Int, newName: String) {
        val folder = folders.getOrNull(index) ?: return
        if (newName.isNotBlank()) {
            folders[index] = folder.copy(name = newName)
        }
    }

    /** Deletes the folder at the given index */
    fun deleteFolder(index: Int) {
        if (index in folders.indices) {
            folders.removeAt(index)
        }
    }

    /** Creates a new folder with optional root headings */
    fun createFolder(name: String, headings: List<String> = emptyList()) {
        if (name.isBlank()) return
        val exists = folders.any { it.name == name }
        if (exists) return
        val headingObjs = headings.map {
            FolderHeading(id = nextHeadingId++, name = it).also { _ -> }
        }
        folders.add(
            UserFolder(
                id = nextFolderId++,
                name = name,
                headings = headingObjs.toMutableList()
            )
        )
    }

    /** Renames a heading specified by path */
    fun renameHeading(folderIndex: Int, path: List<Int>, newName: String) {
        if (newName.isBlank()) return
        val folder = folders.getOrNull(folderIndex) ?: return
        val newHeadings = renameHeadingRec(folder.headings, path, newName) ?: return
        folders[folderIndex] = folder.copy(headings = newHeadings)
    }

    private fun renameHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>,
        newName: String
    ): MutableList<FolderHeading>? {
        val idx = path.firstOrNull() ?: return null
        val heading = list.getOrNull(idx) ?: return null
        val copy = list.toMutableList()
        if (path.size == 1) {
            copy[idx] = heading.copy(name = newName)
            return copy
        }
        val childCopy = renameHeadingRec(heading.children, path.drop(1), newName) ?: return null
        copy[idx] = heading.copy(children = childCopy)
        return copy
    }

    /** Deletes the heading specified by path */
    fun deleteHeading(folderIndex: Int, path: List<Int>) {
        val folder = folders.getOrNull(folderIndex) ?: return
        val newHeadings = deleteHeadingRec(folder.headings, path) ?: return
        folders[folderIndex] = folder.copy(headings = newHeadings)
    }

    private fun deleteHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>
    ): MutableList<FolderHeading>? {
        val idx = path.firstOrNull() ?: return null
        val heading = list.getOrNull(idx) ?: return null
        val copy = list.toMutableList()
        if (path.size == 1) {
            if (idx !in copy.indices) return null
            copy.removeAt(idx)
            return copy
        }
        val childCopy = deleteHeadingRec(heading.children, path.drop(1)) ?: return null
        copy[idx] = heading.copy(children = childCopy)
        return copy
    }

    /** Adds a new child heading to the node specified by path */
    fun addHeading(folderIndex: Int, path: List<Int>, name: String) {
        if (name.isBlank()) return
        val folder = folders.getOrNull(folderIndex) ?: return
        val newHeadings = if (path.isEmpty()) {
            val copy = folder.headings.toMutableList()
            copy.add(FolderHeading(id = nextHeadingId++, name = name))
            copy
        } else {
            addHeadingRec(folder.headings, path, name) ?: return
        }
        folders[folderIndex] = folder.copy(headings = newHeadings)
    }

    private fun addHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>,
        name: String
    ): MutableList<FolderHeading>? {
        val idx = path.firstOrNull() ?: return null
        val heading = list.getOrNull(idx) ?: return null
        val copy = list.toMutableList()
        if (path.size == 1) {
            val childrenCopy = heading.children.toMutableList()
            childrenCopy.add(FolderHeading(id = nextHeadingId++, name = name))
            copy[idx] = heading.copy(children = childrenCopy)
            return copy
        }
        val childCopy = addHeadingRec(heading.children, path.drop(1), name) ?: return null
        copy[idx] = heading.copy(children = childCopy)
        return copy
    }

    /** Renames the quiz at the given index */
    fun renameQuiz(index: Int, newName: String) {
        val quiz = quizzes.getOrNull(index) ?: return
        if (newName.isNotBlank()) {
            quizzes[index] = quiz.copy(name = newName)
        }
    }

    /** Deletes the quiz at the given index */
    fun deleteQuiz(index: Int) {
        if (index in quizzes.indices) {
            quizzes.removeAt(index)
            if (currentQuizIndex >= quizzes.size) currentQuizIndex = quizzes.lastIndex.coerceAtLeast(0)
        }
    }

    /** Creates a new quiz with the given name, box count and optional sub headings */
    fun createQuiz(
        name: String,
        count: Int,
        subHeadings: List<String> = emptyList(),
        folderId: Int? = null
    ) {
        if (count <= 0) return
        // Prevent creating multiple quizzes with the same name
        val exists = quizzes.any { it.name == name }
        if (exists) return
        quizzes.add(
            UserQuiz(
                id = nextQuizId++,
                name = name,
                boxes = MutableList(count) { mutableListOf() },
                subHeadings = subHeadings.toMutableList(),
                folderId = folderId
            )
        )
        currentQuizIndex = quizzes.lastIndex
    }

    /** Changes the active quiz */
    fun setCurrentQuiz(index: Int) {
        currentQuizIndex = index.coerceIn(0, quizzes.lastIndex)
    }

    /**
     * Imports a public quiz as a new user quiz. If a quiz with the same name
     * already exists, the import is ignored to avoid duplicates.
     */
    fun importQuiz(quiz: PublicQuiz) {
        val exists = quizzes.any { it.name == quiz.name }
        if (exists) return
        val newBoxes = MutableList(4) { mutableListOf<Question>() }
        newBoxes[0].addAll(quiz.questions.map { it.copy() })
        quizzes.add(UserQuiz(nextQuizId++, quiz.name, newBoxes))
    }

    /** Returns the current question in quiz mode */
    val currentQuestion: Question?
        get() = boxes.getOrNull(currentBoxIndex)?.getOrNull(currentQuestionIndex)

    /** Adds a new question to the given box index of the current quiz */
    fun addQuestion(
        text: String,
        answer: String,
        topic: String,
        subtopic: String,
        boxIndex: Int
    ) {
        boxes.getOrNull(boxIndex)?.add(Question(text, answer, topic, subtopic))
    }

    /**
     * Starts quiz for the given box index.
     * @return true if the selected box contains at least one question
     */
    fun startQuiz(index: Int): Boolean {
        val selected = boxes.getOrNull(index)
        if (selected.isNullOrEmpty()) return false
        currentBoxIndex = index
        currentQuestionIndex = 0
        isAnswerVisible = false
        return true
    }

    /** Reveals the current answer */
    fun revealAnswer() { isAnswerVisible = true }

    /** Processes the user answer and moves question between boxes.
     *  Returns true if there are more questions to ask.
     */
    fun onAnswerSelected(correct: Boolean): Boolean {
        val box = boxes[currentBoxIndex]
        val question = box.removeAt(currentQuestionIndex)
        val nextIndex = if (correct) min(currentBoxIndex + 1, boxes.lastIndex) else 0
        boxes[nextIndex].add(question)
        isAnswerVisible = false
        if (box.isEmpty()) {
            return false
        } else {
            if (currentQuestionIndex >= box.size) currentQuestionIndex = 0
            return true
        }
    }
}
