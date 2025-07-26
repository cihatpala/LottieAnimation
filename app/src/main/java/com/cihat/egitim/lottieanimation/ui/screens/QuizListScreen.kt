package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.cihat.egitim.lottieanimation.data.StoredUser
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.components.PrimaryAlert
import com.cihat.egitim.lottieanimation.ui.components.OverflowMenu
import com.cihat.egitim.lottieanimation.data.FolderHeading
import com.cihat.egitim.lottieanimation.data.Question
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Builds heading tree from question list when no folder headings exist */
private fun headingsFromQuestions(questions: List<Question>): List<FolderHeading> {
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    quizzes: List<UserQuiz>,
    folders: List<UserFolder>,
    currentUser: FirebaseUser?,
    storedUser: StoredUser?,
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onMoveQuiz: (Int, Int) -> Unit,
    onCreate: (String, Int, Int?) -> Unit,
    onCreateWithQuestion: (String, Int, Int?, String, String, String, String) -> Unit,
    onAddQuestion: (String, String, String, String, Int) -> Unit,
    onClaimQuiz: (Int) -> Unit,
    onSetCurrentQuiz: (Int) -> Unit,
    onUser: (String, String, String?) -> Unit,
    onFolders: () -> Unit,
    onBack: () -> Unit,
    bottomTab: BottomTab,
    onTab: (BottomTab) -> Unit,
    onMenu: () -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createCount by remember { mutableFloatStateOf(4f) }
    var selectedFolder by remember { mutableStateOf(folders.firstOrNull()?.id) }

    AppScaffold(
        title = "My Quizzes",
        showBack = false,
        onBack = onBack,
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab,
        actions = {
            OverflowMenu(listOf(
                "Quiz Ekle" to {
                    if (folders.isEmpty()) {
                        showWarning = true
                    } else {
                        showCreate = true
                    }
                }
            ))
        }
    ) {

        val listState = rememberLazyListState()
        var draggingQuizId by remember { mutableStateOf<Int?>(null) }
        var draggingIndex by remember { mutableIntStateOf(-1) }
        var dragOffset by remember { mutableFloatStateOf(0f) }
        val itemHeightPx = with(LocalDensity.current) { 72.dp.toPx() }
        var startDialogFor by remember { mutableStateOf<Int?>(null) }
        var emptyAlertFor by remember { mutableStateOf<Int?>(null) }
        var addDialogFor by remember { mutableStateOf<Int?>(null) }
        var claimDialogFor by remember { mutableStateOf<Int?>(null) }
        var openSwipeId by remember { mutableStateOf<Int?>(null) }
        val swipeStates = remember { mutableMapOf<Int, SwipeableState<Int>>() }

        LaunchedEffect(listState.isScrollInProgress) {
            if (listState.isScrollInProgress) {
                openSwipeId?.let { id ->
                    swipeStates[id]?.animateTo(0)
                    openSwipeId = null
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quizzes.isEmpty()) {
                Text("Henüz quiziniz yok")
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f)
                ) {
                    // Use a stable key so Compose properly disposes state when
                    // an item is removed. Without this, deleting the last item
                    // triggers an IndexOutOfBoundsException as the old state
                    // tries to read a missing index from the list.
                    itemsIndexed(
                        items = quizzes,
                        key = { _, quiz -> quiz.id }
                    ) { quizIndex, quiz ->
                        var expanded by remember(quiz.id) { mutableStateOf(false) }
                        var showRename by remember(quiz.id) { mutableStateOf(false) }
                        var showDelete by remember(quiz.id) { mutableStateOf(false) }
                        var newName by remember(quiz.id) { mutableStateOf(quiz.name) }
                        val scope = rememberCoroutineScope()
                        // Use the same icon size as the "Start" and "Detail" buttons
                        val actionWidth = 48.dp
                        val swipeState = rememberSwipeableState(0)
                        DisposableEffect(quiz.id) {
                            swipeStates[quiz.id] = swipeState
                            onDispose {
                                swipeStates.remove(quiz.id)
                                if (openSwipeId == quiz.id) openSwipeId = null
                            }
                        }
                        val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }

// Eğer başka bir item açıksa, ben kapanmalıyım
                        LaunchedEffect(openSwipeId) {
                            if (openSwipeId != quiz.id && swipeState.currentValue != 0) {
                                swipeState.animateTo(0, tween(durationMillis = 100, easing = LinearEasing))
                            }
                        }

// Eğer ben açılırsam, global swipe ID'yi kendime atamalıyım
                        LaunchedEffect(swipeState.currentValue) {
                            if (swipeState.currentValue != 0 && openSwipeId != quiz.id) {
                                openSwipeId = quiz.id
                            }
                        }


                        // How much the actions are revealed. 0f when hidden, 1f when fully swiped.
                        val revealProgressEnd = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
                        val revealProgressStart = (swipeState.offset.value / maxOffset).coerceIn(0f, 1f)

                        val isDragging = draggingQuizId == quiz.id
                        val dragModifier = Modifier
                            .offset { IntOffset(0, if (isDragging) dragOffset.roundToInt() else 0) }
                            .pointerInput(quiz.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggingQuizId = quiz.id
                                        draggingIndex = quizzes.indexOfFirst { it.id == quiz.id }
                                        dragOffset = 0f
                                    },
                                    onDragCancel = {
                                        dragOffset = 0f
                                        draggingIndex = -1
                                        draggingQuizId = null
                                    },
                                    onDragEnd = {
                                        dragOffset = 0f
                                        draggingIndex = -1
                                        draggingQuizId = null
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount.y
                                        val from = draggingIndex
                                        val potentialIndex = (from + (dragOffset / itemHeightPx).roundToInt())
                                            .coerceIn(0, quizzes.lastIndex)
                                        if (potentialIndex != from) {
                                            onMoveQuiz(from, potentialIndex)
                                            draggingIndex = potentialIndex
                                            dragOffset -= (potentialIndex - from) * itemHeightPx
                                        }
                                    }
                                )
                            }

                        Box(
                            modifier = dragModifier
                                .fillMaxWidth()
                                .animateItem()
                        ) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val ownerPhoto = currentUser?.photoUrl?.toString() ?: storedUser?.photoUrl
                                    if (ownerPhoto != null) {
                                        AsyncImage(
                                            model = ownerPhoto,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(currentUser?.displayName ?: storedUser?.name ?: "", modifier = Modifier.alignByBaseline())
                                    Spacer(Modifier.weight(1f))
                                    if (quiz.author != null && !quiz.isImported) {
                                        Row(
                                            modifier = Modifier
                                                .clickable { onUser(quiz.author, quiz.authorName ?: quiz.author, quiz.authorPhotoUrl) }
                                                .background(
                                                    MaterialTheme.colorScheme.secondaryContainer,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Download,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            quiz.authorPhotoUrl?.let { url ->
                                                Spacer(Modifier.width(4.dp))
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                )
                                            }
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                quiz.author,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clipToBounds()
                                        .swipeable(
                                            state = swipeState,
                                            anchors = mapOf(-maxOffset to 1, 0f to 0, maxOffset to 2),
                                            thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                            orientation = Orientation.Horizontal
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .height(72.dp)
                                            .alpha(revealProgressEnd)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                scope.launch { swipeState.animateTo(0) }
                                                showRename = true
                                            },
                                            enabled = swipeState.currentValue == 1,
                                            modifier = Modifier
                                                .size(actionWidth)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.tertiary,
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                scope.launch { swipeState.animateTo(0) }
                                                showDelete = true
                                            },
                                            enabled = swipeState.currentValue == 1,
                                            modifier = Modifier
                                                .size(actionWidth)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.error,
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .height(72.dp)
                                            .alpha(revealProgressStart)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                scope.launch { swipeState.animateTo(0) }
                                                onSetCurrentQuiz(quizIndex)
                                                addDialogFor = quizIndex
                                            },
                                            enabled = swipeState.currentValue == 2,
                                            modifier = Modifier
                                                .size(actionWidth)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Add",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                        if (quiz.isImported) {
                                            IconButton(
                                                onClick = {
                                                    scope.launch { swipeState.animateTo(0) }
                                                    claimDialogFor = quizIndex
                                                },
                                                enabled = swipeState.currentValue == 2,
                                                modifier = Modifier
                                                    .size(actionWidth)
                                                    .clip(CircleShape)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary,
                                                        CircleShape
                                                    )
                                            ) {
                                                Icon(
                                                    Icons.Default.CloudUpload,
                                                    contentDescription = "Claim",
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        val folderName = folders.find { it.id == quiz.folderId }?.name ?: ""
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(folderName, style = MaterialTheme.typography.labelSmall)
                                                Text(quiz.name, style = MaterialTheme.typography.bodyLarge)
                                            }
                                            FilledIconButton(
                                                onClick = { expanded = !expanded },
                                                enabled = kotlin.math.abs(swipeState.offset.value) < 1f,
                                                colors = IconButtonDefaults.filledIconButtonColors(),
                                            ) {
                                                Icon(Icons.Default.Description, contentDescription = "Detay")
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Box {
                                                FilledIconButton(
                                                    onClick = {
                                                        if (quiz.boxes.flatten().isEmpty()) {
                                                            emptyAlertFor = quizIndex
                                                        } else {
                                                            startDialogFor = quizIndex
                                                        }
                                                    },
                                                    enabled = kotlin.math.abs(swipeState.offset.value) < 1f,
                                                    colors = IconButtonDefaults.filledIconButtonColors(),
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = "Başlat")
                                                }
                                                if (quiz.boxes.flatten().isEmpty()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .background(Color.Red, CircleShape)
                                                            .align(Alignment.TopEnd)
                                                    )
                                                }
                                            }
                                        }
                                        if (expanded) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                            ) {
                                                quiz.boxes.chunked(2).forEachIndexed { rowIndex, pair ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        pair.forEachIndexed { colIndex, box ->
                                                            val boxIndex = rowIndex * 2 + colIndex
                                                            Card(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .aspectRatio(1f)
                                                                    .clickable { onView(quizIndex, boxIndex) },
                                                                shape = RoundedCornerShape(8.dp),
                                                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                                )
                                                            ) {
                                                                Column(
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .padding(8.dp),
                                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                                    verticalArrangement = Arrangement.Center
                                                                ) {
                                                                    Text(text = "Box ${boxIndex + 1}")
                                                                    Text(text = "${box.size} soru")
                                                                }
                                                            }
                                                        }
                                                        if (pair.size == 1) {
                                                            Spacer(modifier = Modifier.weight(1f))
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        if (showRename) {
                            AlertDialog(
                                onDismissRequest = { showRename = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        onRename(quizIndex, newName)
                                        showRename = false
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showRename = false }) { Text("Cancel") }
                                },
                                title = { Text("Edit Quiz") },
                                text = {
                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("Name") }
                                    )
                                }
                            )
                        }

                        if (showDelete) {
                            AlertDialog(
                                onDismissRequest = { showDelete = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        onDelete(quizIndex)
                                        showDelete = false
                                    }) { Text("Evet") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDelete = false }) { Text("Hayır") }
                                },
                                text = { Text("Silmek istediğinize emin misiniz?") }
                            )
                        }

                        if (addDialogFor == quizIndex) {
                            val folderId = quiz.folderId
                            val folder = folders.find { it.id == folderId }
                            val folderHeadings = folder?.headings
                                ?: headingsFromQuestions(quiz.boxes.flatten())
                            AddQuestionDialog(
                                boxCount = quiz.boxes.size,
                                headings = folderHeadings,
                                quizName = quiz.name,
                                folderName = folder?.name ?: "",
                                onAdd = { q, a, topic, sub, box ->
                                    onAddQuestion(q, a, topic, sub, box)
                                },
                                onDismiss = { addDialogFor = null }
                            )
                        }


                    }
                }
            }
        }



        startDialogFor?.let { idx ->
            val quiz = quizzes.getOrNull(idx)
            if (quiz != null) {
                AlertDialog(
                    onDismissRequest = { startDialogFor = null },
                    confirmButton = {
                        TextButton(onClick = { startDialogFor = null }) { Text("Kapat") }
                    },
                    dismissButton = {},
                    title = { Text("Kutuyu Seç") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val big = 72.dp
                            val small = 24.dp
                            quiz.boxes.chunked(3).forEachIndexed { rowIndex, row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEachIndexed { colIndex, box ->
                                        val boxIndex = rowIndex * 3 + colIndex
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clickable {
                                                    startDialogFor = null
                                                    onQuiz(idx, boxIndex)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(big)
                                                    .background(
                                                        MaterialTheme.colorScheme.secondaryContainer,
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${box.size} soru", textAlign = TextAlign.Center)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .offset(y = -(big / 2 + small / 2))
                                                    .size(small)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                    .align(Alignment.Center),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${boxIndex + 1}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }
                                    if (row.size < 3) {
                                        repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                )
            }
        }

        emptyAlertFor?.let { idx ->
            PrimaryAlert(
                title = "Uyarı",
                message = "Quizde henüz soru yok",
                onDismiss = { emptyAlertFor = null },
                confirmText = "Soru Ekle",
                onConfirm = {
                    emptyAlertFor = null
                    onSetCurrentQuiz(idx)
                    addDialogFor = idx
                }
            )
        }

        claimDialogFor?.let { idx ->
            PrimaryAlert(
                title = "Uyarı",
                message = "Quiz referanslı olacak. Kabul ediyor musunuz?",
                onDismiss = { claimDialogFor = null },
                confirmText = "Kabul et",
                onConfirm = {
                    onClaimQuiz(idx)
                    claimDialogFor = null
                }
            )
        }
    }



    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedFolder?.let { folderId ->
                        onCreate(createName, createCount.toInt(), folderId)
                    }
                    showCreate = false
                    createName = ""
                    createCount = 4f
                    selectedFolder = folders.firstOrNull()?.id
                }) { Text("Oluştur") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("İptal") }
            },
            title = { Text("Quiz Oluştur") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Slider(
                        value = createCount,
                        onValueChange = { createCount = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                    Text("${createCount.toInt()} kutu")
                    OutlinedTextField(
                        value = createName,
                        onValueChange = { createName = it },
                        label = { Text("Ad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Folder selection
                    var expandedFolder by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedFolder,
                        onExpandedChange = { expandedFolder = !expandedFolder }
                    ) {
                        OutlinedTextField(
                            value = folders.find { it.id == selectedFolder }?.name
                                ?: "Klasör Seç",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Klasör") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedFolder) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedFolder,
                            onDismissRequest = { expandedFolder = false }
                        ) {
                            folders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name) },
                                    onClick = {
                                        selectedFolder = folder.id
                                        expandedFolder = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        )
    }

    if (showWarning) {
        PrimaryAlert(
            title = "Uyarı",
            message = "Quiz oluşturmak için klasör oluşturunuz.",
            onDismiss = { showWarning = false },
            confirmText = "Klasör Oluştur",
            onConfirm = {
                showWarning = false
                onFolders()
            }
        )
    }
}

// Close AppScaffold and the QuizListScreen function
}
}







