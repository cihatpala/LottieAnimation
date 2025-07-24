package com.cihat.egitim.lottieanimation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.cihat.egitim.lottieanimation.data.local.LocalRepository
import kotlinx.coroutines.launch
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
class QuizViewModel(private val repository: LocalRepository) : ViewModel() {

    /** Folders created by the user */
    var folders = mutableStateListOf<UserFolder>()
        private set

    private var nextFolderId = 0
    private var nextHeadingId = 0

    /** Quizzes created or imported by the user */
    var quizzes = mutableStateListOf<UserQuiz>()
        private set

    private var nextQuizId = 0

    private val saveMutex = Mutex()

    /** Index of the quiz currently being viewed */
    private var currentQuizIndex by mutableStateOf(0)

    init {
        viewModelScope.launch {
            folders.addAll(repository.loadFolders())
            quizzes.addAll(repository.loadQuizzes())
            nextFolderId = (folders.maxOfOrNull { it.id } ?: -1) + 1
            nextHeadingId = (folders.flatMap { collectHeadingIds(it.headings) }.maxOrNull() ?: -1) + 1
            nextQuizId = (quizzes.maxOfOrNull { it.id } ?: -1) + 1
        }
    }

    private fun collectHeadingIds(list: List<FolderHeading>): List<Int> {
        val ids = mutableListOf<Int>()
        list.forEach { h ->
            ids.add(h.id)
            ids.addAll(collectHeadingIds(h.children))
        }
        return ids
    }

    private fun persistState() {
        viewModelScope.launch {
            saveMutex.withLock {
                val foldersSnapshot = folders.map { folder ->
                    folder.copy(headings = folder.headings.toMutableList())
                }
                val quizzesSnapshot = quizzes.map { quiz ->
                    quiz.copy(
                        boxes = quiz.boxes.map { it.toMutableList() }.toMutableList(),
                        subHeadings = quiz.subHeadings.toMutableList()
                    )
                }
                repository.saveFolders(foldersSnapshot)
                repository.saveQuizzes(quizzesSnapshot)
            }
        }
    }

    /** Convenient access to boxes of the current quiz */
    val boxes: MutableList<MutableList<Question>>
        get() = quizzes.getOrNull(currentQuizIndex)?.boxes ?: mutableListOf()

    /** Name of the active quiz */
    val currentQuizName: String
        get() = quizzes.getOrNull(currentQuizIndex)?.name ?: ""

    /** Folder name of the active quiz */
    val currentQuizFolderName: String
        get() {
            val folderId = quizzes.getOrNull(currentQuizIndex)?.folderId
            return folders.find { it.id == folderId }?.name ?: ""
        }

    /** Headings of the folder the current quiz belongs to */
    val currentQuizFolderHeadings: List<FolderHeading>
        get() {
            val folderId = quizzes.getOrNull(currentQuizIndex)?.folderId
            return folders.find { it.id == folderId }?.headings ?: emptyList()
        }

    /**
     * Heading options for the current quiz. Falls back to headings derived from
     * existing questions if the quiz is not assigned to a folder or the folder
     * has no headings.
     */
    val currentQuizHeadingOptions: List<FolderHeading>
        get() {
            val quiz = quizzes.getOrNull(currentQuizIndex) ?: return emptyList()
            val folderHeadings = currentQuizFolderHeadings
            return if (folderHeadings.isNotEmpty()) {
                folderHeadings
            } else {
                buildHeadingsFromQuestions(quiz.boxes.flatten())
            }
        }

    /** Sample public quizzes that could come from a backend in a real app */
    val publicQuizzes: List<PublicQuiz> = listOf(
        PublicQuiz(
            name = "Capital Cities",
            author = "Alice",
            questions = listOf(
                Question(
                    text = "Capital of France?",
                    answer = "Paris",
                    topic = "Geography",
                    subtopic = "Europe"
                ),
                Question(
                    text = "Capital of Spain?",
                    answer = "Madrid",
                    topic = "Geography",
                    subtopic = "Europe"
                )
            )
            ),
            authorPhotoUrl = "https://example.com/alice.png",
            folderName = "Geography"
        ),
        PublicQuiz(
            name = "Math Basics",
            author = "Bob",
            questions = listOf(
                Question(
                    text = "2 + 2?",
                    answer = "4",
                    topic = "Arithmetic",
                    subtopic = "Addition"
                ),
                Question(
                    text = "5 * 3?",
                    answer = "15",
                    topic = "Arithmetic",
                    subtopic = "Multiplication"
                )
            ),
            authorPhotoUrl = "https://example.com/bob.png",
            folderName = "Math"
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

    /**
     * Generic helper to traverse a heading list and apply an operation on the
     * target node. It copies the hierarchy along the traversed path so that the
     * original list remains unchanged.
     */
    private fun modifyHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>,
        operation: (MutableList<FolderHeading>, Int, FolderHeading) -> Unit
    ): MutableList<FolderHeading>? {
        val idx = path.firstOrNull() ?: return null
        val heading = list.getOrNull(idx) ?: return null
        val copy = list.toMutableList()
        if (path.size == 1) {
            operation(copy, idx, heading)
            return copy
        }
        val childCopy = modifyHeadingRec(heading.children, path.drop(1), operation) ?: return null
        copy[idx] = heading.copy(children = childCopy)
        return copy
    }

    /** Renames the folder at the given index */
    fun renameFolder(index: Int, newName: String) {
        val folder = folders.getOrNull(index) ?: return
        if (newName.isNotBlank()) {
            folders[index] = folder.copy(name = newName)
            persistState()
        }
    }

    /** Deletes the folder at the given index */
    fun deleteFolder(index: Int) {
        val folder = folders.getOrNull(index) ?: return
        if (index !in folders.indices) return

        // Remove folder reference from any quizzes pointing to it to satisfy
        // the foreign key constraint in the database.
        quizzes.replaceAll { quiz ->
            if (quiz.folderId == folder.id) quiz.copy(folderId = null) else quiz
        }

        folders.removeAt(index)
        persistState()
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
        persistState()
    }

    /** Renames a heading specified by path */
    fun renameHeading(folderIndex: Int, path: List<Int>, newName: String) {
        if (newName.isBlank()) return
        val folder = folders.getOrNull(folderIndex) ?: return
        val newHeadings = renameHeadingRec(folder.headings, path, newName) ?: return
        folders[folderIndex] = folder.copy(headings = newHeadings)
        persistState()
    }

    /** Deletes the heading specified by path */
    fun deleteHeading(folderIndex: Int, path: List<Int>) {
        val folder = folders.getOrNull(folderIndex) ?: return
        val newHeadings = deleteHeadingRec(folder.headings, path) ?: return
        folders[folderIndex] = folder.copy(headings = newHeadings)
        persistState()
    }

    private fun deleteHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>
    ): MutableList<FolderHeading>? =
        modifyHeadingRec(list, path) { parent, index, _ ->
            if (index in parent.indices) parent.removeAt(index)
        }

    private fun renameHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>,
        newName: String
    ): MutableList<FolderHeading>? =
        modifyHeadingRec(list, path) { parent, index, heading ->
            if (index in parent.indices) parent[index] = heading.copy(name = newName)
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
        persistState()
    }

    private fun addHeadingRec(
        list: MutableList<FolderHeading>,
        path: List<Int>,
        name: String
    ): MutableList<FolderHeading>? {
        return modifyHeadingRec(list, path) { parent, index, heading ->
            val childList = heading.children.toMutableList()
            childList.add(FolderHeading(id = nextHeadingId++, name = name))
            parent[index] = heading.copy(children = childList)
        }
    }

    /** Renames the quiz at the given index */
    fun renameQuiz(index: Int, newName: String) {
        val quiz = quizzes.getOrNull(index) ?: return
        if (newName.isNotBlank()) {
            quizzes[index] = quiz.copy(name = newName)
            persistState()
        }
    }

    /** Deletes the quiz at the given index */
    fun deleteQuiz(index: Int) {
        if (index in quizzes.indices) {
            quizzes.removeAt(index)
            if (currentQuizIndex >= quizzes.size) currentQuizIndex = quizzes.lastIndex.coerceAtLeast(0)
            persistState()
        }
    }

    /**
     * Reorders quizzes by moving the item at the given index to the target index.
     */
    fun moveQuiz(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in quizzes.indices || toIndex !in quizzes.indices) return
        val quiz = quizzes.removeAt(fromIndex)
        quizzes.add(toIndex, quiz)
        persistState()
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
        persistState()
    }

    /**
     * Creates a quiz and immediately adds an initial question to the first box.
     */
    fun createQuizWithQuestion(
        name: String,
        count: Int,
        folderId: Int?,
        topic: String,
        subtopic: String,
        question: String,
        answer: String
    ) {
        createQuiz(name, count, emptyList(), folderId)
        addQuestion(question, answer, topic, subtopic, 0)
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
        persistState()
    }

    /**
     * Builds a temporary heading tree from the provided questions. This is used
     * when a quiz has no folder headings defined.
     */
    private fun buildHeadingsFromQuestions(questions: List<Question>): List<FolderHeading> {
        var nextId = -1
        fun next() = nextId--

        val roots = mutableListOf<FolderHeading>()
        for (q in questions) {
            val names = mutableListOf<String>()
            if (q.topic.isNotBlank()) names.add(q.topic)
            if (q.subtopic.isNotBlank()) {
                names.addAll(q.subtopic.split(" > ").map { it.trim() }.filter { it.isNotBlank() })
            }

            var list = roots
            var node: FolderHeading? = null
            for (n in names) {
                node = list.find { it.name == n }
                if (node == null) {
                    node = FolderHeading(id = next(), name = n, children = mutableListOf())
                    list.add(node)
                }
                list = node.children
            }
        }
        return roots
    }

    /**
     * Returns heading options for the given quiz, using folder headings if
     * available or deriving them from the quiz questions otherwise.
     */
    fun headingOptionsForQuiz(quiz: UserQuiz): List<FolderHeading> {
        val folderHeadings = folders.find { it.id == quiz.folderId }?.headings ?: emptyList()
        return if (folderHeadings.isNotEmpty()) folderHeadings else buildHeadingsFromQuestions(quiz.boxes.flatten())
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
        persistState()
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
    fun revealAnswer() {
        isAnswerVisible = true
    }

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

    /** Updates the question at the given box and index */
    fun editQuestion(boxIndex: Int, questionIndex: Int, newQuestion: Question) {
        val box = boxes.getOrNull(boxIndex) ?: return
        if (questionIndex in box.indices) {
            box[questionIndex] = newQuestion
            persistState()
        }
    }

    /** Deletes the question at the specified box and index */
    fun deleteQuestion(boxIndex: Int, questionIndex: Int) {
        boxes.getOrNull(boxIndex)?.let { box ->
            if (questionIndex in box.indices) {
                box.removeAt(questionIndex)
                persistState()
            }
        }
    }
}
