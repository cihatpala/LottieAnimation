package com.cihat.egitim.lottieanimation.data.local

import com.cihat.egitim.lottieanimation.data.FolderHeading
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.data.StoredUser
import com.cihat.egitim.lottieanimation.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.room.withTransaction

class LocalRepository(private val db: AppDatabase) {
    private val folderDao = db.folderDao()
    private val quizDao = db.quizDao()
    private val settingsDao = db.settingsDao()
    private val sessionDao = db.sessionDao()

    suspend fun loadFolders(): List<UserFolder> = withContext(Dispatchers.IO) {
        val folders = folderDao.getFolders()
        val headings = folderDao.getHeadings()
        folders.map { folder ->
            val folderHeadings = headings.filter { it.folderId == folder.id }
            UserFolder(
                id = folder.id,
                name = folder.name,
                headings = buildHeadingTree(folderHeadings)
            )
        }
    }

    private fun buildHeadingTree(list: List<FolderHeadingEntity>): MutableList<FolderHeading> {
        val map = mutableMapOf<Int, FolderHeading>()
        val roots = mutableListOf<FolderHeading>()
        list.forEach { entity ->
            map[entity.id] = FolderHeading(entity.id, entity.name)
        }
        list.forEach { entity ->
            val node = map[entity.id]!!
            if (entity.parentId == null) {
                roots.add(node)
            } else {
                map[entity.parentId]?.children?.add(node)
            }
        }
        return roots
    }

    suspend fun loadQuizzes(): List<UserQuiz> = withContext(Dispatchers.IO) {
        val quizzes = quizDao.getQuizzes()
        val questions = quizDao.getQuestions()
        val subs = quizDao.getSubHeadings()
        quizzes.map { quiz ->
            val qList = questions.filter { it.quizId == quiz.id }
            val sList = subs.filter { it.quizId == quiz.id }.sortedBy { it.boxIndex }
            val boxCount = maxOf(
                quiz.boxCount,
                (sList.maxOfOrNull { it.boxIndex } ?: -1) + 1,
                (qList.maxOfOrNull { it.boxIndex } ?: -1) + 1
            )
            val boxes = MutableList(boxCount) { mutableListOf<Question>() }
            qList.forEach { q ->
                boxes[q.boxIndex].add(Question(q.text, q.answer, q.topic, q.subtopic))
            }
            UserQuiz(
                id = quiz.id,
                name = quiz.name,
                boxes = boxes,
                subHeadings = sList.sortedBy { it.boxIndex }.map { it.name }.toMutableList(),
                folderId = quiz.folderId
            )
        }
    }

    suspend fun saveFolders(folders: List<UserFolder>) = withContext(Dispatchers.IO) {
        fun copyHeading(h: FolderHeading): FolderHeading =
            h.copy(children = h.children.map { copyHeading(it) }.toMutableList())

        val snapshot = folders.map { f ->
            f.copy(headings = f.headings.map { copyHeading(it) }.toMutableList())
        }
        db.withTransaction {
            folderDao.clearFolders()
            folderDao.clearHeadings()
            val folderEntities = snapshot.map { UserFolderEntity(it.id, it.name) }
            val headingEntities = mutableListOf<FolderHeadingEntity>()
            snapshot.forEach { folder ->
                flattenHeadings(folder.id, null, folder.headings, headingEntities)
            }
            folderDao.insert(folderEntities)
            folderDao.insertHeadings(headingEntities)
        }
    }

    private fun flattenHeadings(folderId: Int, parentId: Int?, list: List<FolderHeading>, dest: MutableList<FolderHeadingEntity>) {
        list.forEach { heading ->
            dest.add(FolderHeadingEntity(heading.id, heading.name, folderId, parentId))
            flattenHeadings(folderId, heading.id, heading.children, dest)
        }
    }

    suspend fun saveQuizzes(quizzes: List<UserQuiz>) = withContext(Dispatchers.IO) {
        val snapshot = quizzes.map { q ->
            q.copy(
                boxes = q.boxes.map { it.toMutableList() }.toMutableList(),
                subHeadings = q.subHeadings.toMutableList()
            )
        }
        db.withTransaction {
            quizDao.clearQuizzes()
            quizDao.clearQuestions()
            quizDao.clearSubHeadings()
            val quizEntities = snapshot.map { UserQuizEntity(it.id, it.name, it.folderId, it.boxes.size) }
            val questionEntities = mutableListOf<QuestionEntity>()
            val subEntities = mutableListOf<SubHeadingEntity>()
            snapshot.forEach { quiz ->
                quiz.boxes.forEachIndexed { index, box ->
                    box.forEach { q ->
                        questionEntities.add(
                            QuestionEntity(
                                quizId = quiz.id,
                                boxIndex = index,
                                text = q.text,
                                answer = q.answer,
                                topic = q.topic,
                                subtopic = q.subtopic
                            )
                        )
                    }
                }
                quiz.subHeadings.forEachIndexed { idx, name ->
                    subEntities.add(SubHeadingEntity(quizId = quiz.id, name = name, boxIndex = idx))
                }
            }
            quizDao.insert(quizEntities)
            quizDao.insertQuestions(questionEntities)
            quizDao.insertSubHeadings(subEntities)
        }
    }

    suspend fun getTheme(): ThemeMode = withContext(Dispatchers.IO) {
        when (settingsDao.getSetting("theme")) {
            ThemeMode.DARK.name -> ThemeMode.DARK
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun saveTheme(mode: ThemeMode) = withContext(Dispatchers.IO) {
        settingsDao.insert(SettingEntity("theme", mode.name))
    }

    suspend fun loadUserSession(): StoredUser? = withContext(Dispatchers.IO) {
        sessionDao.getUserSession()?.let {
            StoredUser(it.uid, it.name, it.email, it.photoUrl)
        }
    }

    suspend fun saveUserSession(user: StoredUser?) = withContext(Dispatchers.IO) {
        sessionDao.clearUserSession()
        user?.let {
            sessionDao.insert(
                UserSessionEntity(
                    uid = it.uid,
                    name = it.name,
                    email = it.email,
                    photoUrl = it.photoUrl
                )
            )
        }
    }
}
