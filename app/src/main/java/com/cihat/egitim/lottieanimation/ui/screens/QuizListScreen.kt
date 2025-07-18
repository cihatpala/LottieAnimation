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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.components.PrimaryAlert
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
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onMoveQuiz: (Int, Int) -> Unit,
    onCreate: (String, Int, Int?) -> Unit,
    onCreateWithQuestion: (String, Int, Int?, String, String, String, String) -> Unit,
    onQuickAdd: (Int, String, String, String, String) -> Unit,
    onAddQuestion: (Int) -> Unit,
    onFolders: () -> Unit,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "My Quizzes",
        showBack = true,
        onBack = onBack,
        bottomTab = BottomTab.HOME,
        onTabSelected = onTab
    ) {
        var showCreate by remember { mutableStateOf(false) }
        var showWarning by remember { mutableStateOf(false) }
        var createName by remember { mutableStateOf("") }
        var createCount by remember { mutableFloatStateOf(4f) }
        var selectedFolder by remember { mutableStateOf(folders.firstOrNull()?.id) }

        val listState = rememberLazyListState()
        var draggingQuizId by remember { mutableStateOf<Int?>(null) }
        var draggingIndex by remember { mutableIntStateOf(-1) }
        var dragOffset by remember { mutableFloatStateOf(0f) }
        val itemHeightPx = with(LocalDensity.current) { 72.dp.toPx() }
        var startDialogFor by remember { mutableStateOf<Int?>(null) }
        var emptyAlertFor by remember { mutableStateOf<Int?>(null) }

        // State for draggable FAB
        val density = LocalDensity.current
        var containerWidthPx by remember { mutableStateOf(0f) }
        var containerHeightPx by remember { mutableStateOf(0f) }
        var fabWidthPx by remember { mutableStateOf(0f) }
        var fabHeightPx by remember { mutableStateOf(0f) }
        var fabInit by remember { mutableStateOf(false) }
        val fabOffsetX = remember { Animatable(0f) }
        val fabOffsetY = remember { Animatable(0f) }
        val fabScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    containerWidthPx = it.size.width.toFloat()
                    containerHeightPx = it.size.height.toFloat()
                }
        ) {
            LaunchedEffect(containerWidthPx, containerHeightPx, fabWidthPx, fabHeightPx) {
                if (!fabInit &&
                    containerWidthPx > 0f &&
                    containerHeightPx > 0f &&
                    fabWidthPx > 0f &&
                    fabHeightPx > 0f
                ) {
                    val margin = with(density) { 16.dp.toPx() }
                    fabOffsetX.snapTo(containerWidthPx - fabWidthPx - margin)
                    fabOffsetY.snapTo(containerHeightPx - fabHeightPx - margin)
                    fabInit = true
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
                            var showQuickAdd by remember(quiz.id) { mutableStateOf(false) }
                            var quickQuestion by remember(quiz.id) { mutableStateOf("") }
                            var quickAnswer by remember(quiz.id) { mutableStateOf("") }
                            var quickPath by remember(quiz.id) { mutableStateOf<List<Int>>(emptyList()) }
                            var newName by remember(quiz.id) { mutableStateOf(quiz.name) }
                            val scope = rememberCoroutineScope()
                            val actionWidth = 72.dp
                            val swipeState = rememberSwipeableState(0)
                            val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }

                            // How much the actions are revealed. 0f when hidden, 1f when fully swiped.
                            val revealProgressEnd = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
                            val revealProgressStart = (swipeState.offset.value / maxOffset).coerceIn(0f, 1f)

                            // Collapse the item whenever it is swiped to reveal actions
                            LaunchedEffect(swipeState.offset) {
                                if (kotlin.math.abs(swipeState.offset.value) > 1f) {
                                    expanded = false
                                }
                            }

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
                                    .clipToBounds()
                                    .swipeable(
                                        state = swipeState,
                                        anchors = mapOf(-maxOffset to 1, 0f to 0, maxOffset to 2),
                                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                        orientation = Orientation.Horizontal
                                    )
                                    .animateItem()
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
                                            .background(MaterialTheme.colorScheme.tertiary)
                                            .size(actionWidth)
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
                                            .background(MaterialTheme.colorScheme.error)
                                            .size(actionWidth)
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
                                            showQuickAdd = true
                                        },
                                        enabled = swipeState.currentValue == 2,
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary)
                                            .size(actionWidth)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = quiz.name, modifier = Modifier.weight(1f))
                                        FilledIconButton(
                                            onClick = { expanded = !expanded },
                                            enabled = kotlin.math.abs(swipeState.offset.value) < 1f,
                                            colors = IconButtonDefaults.filledIconButtonColors()
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
                                                colors = IconButtonDefaults.filledIconButtonColors()
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

                            if (showQuickAdd) {
                                val folderId = quiz.folderId
                                val folderHeadings = folders.find { it.id == folderId }?.headings
                                    ?: headingsFromQuestions(quiz.boxes.flatten())
                                AlertDialog(
                                    onDismissRequest = { showQuickAdd = false },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            var list = folderHeadings
                                            val names = mutableListOf<String>()
                                            for (idx in quickPath) {
                                                val h = list.getOrNull(idx) ?: break
                                                names.add(h.name)
                                                list = h.children
                                            }
                                            val topic = names.firstOrNull() ?: ""
                                            val sub = names.drop(1).joinToString(" > ")
                                            onQuickAdd(quizIndex, topic, sub, quickQuestion, quickAnswer)
                                            showQuickAdd = false
                                            quickQuestion = ""
                                            quickAnswer = ""
                                            quickPath = emptyList()
                                        }) { Text("Soru Ekle") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showQuickAdd = false }) { Text("İptal") }
                                    },
                                    title = {
                                        Text(folderId?.let { folders.find { f -> f.id == it }?.name }
                                            ?: "")
                                    },
                                    text = {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            var currentList = folderHeadings
                                            for (level in 0..quickPath.size) {
                                                val options = currentList
                                                if (options.isEmpty()) break
                                                var expanded by remember(level, quickPath) { mutableStateOf(false) }
                                                val selectedIdx = quickPath.getOrNull(level)
                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = selectedIdx?.let { options[it].name }
                                                            ?: "Seçiniz",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Başlık ${level + 1}") },
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )
                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        options.forEachIndexed { index, h ->
                                                            DropdownMenuItem(
                                                                text = { Text(h.name) },
                                                                onClick = {
                                                                    quickPath = quickPath.take(level) + index
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                selectedIdx?.let { idx -> currentList = options[idx].children }
                                                    ?: run { currentList = emptyList() }
                                            }

                                            OutlinedTextField(
                                                value = quickQuestion,
                                                onValueChange = { quickQuestion = it },
                                                label = { Text("Soru") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = quickAnswer,
                                                onValueChange = { quickAnswer = it },
                                                label = { Text("Cevap") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                )
                            }


                        }
                    }
                }
            }
            ExtendedFloatingActionButton(
                onClick = {
                    if (folders.isEmpty()) {
                        showWarning = true
                    } else {
                        showCreate = true
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Quiz Ekle") },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp)
                    .offset { IntOffset(fabOffsetX.value.roundToInt(), fabOffsetY.value.roundToInt()) }
                    .onGloballyPositioned {
                        fabWidthPx = it.size.width.toFloat()
                        fabHeightPx = it.size.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragEnd = {
                                fabScope.launch {
                                    val margin = with(density) { 16.dp.toPx() }
                                    val max = containerWidthPx - fabWidthPx - margin
                                    val targetX = if (fabOffsetX.value < max / 2f) 0f else max
                                    fabOffsetX.animateTo(targetX, animationSpec = tween(300))
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                fabScope.launch {
                                    val margin = with(density) { 16.dp.toPx() }
                                    val maxX = containerWidthPx - fabWidthPx - margin
                                    val maxY = containerHeightPx - fabHeightPx - margin
                                    val newX = (fabOffsetX.value + dragAmount.x).coerceIn(0f, maxX)
                                    val newY = (fabOffsetY.value + dragAmount.y).coerceIn(0f, maxY)
                                    fabOffsetX.snapTo(newX)
                                    fabOffsetY.snapTo(newY)
                                }
                            }
                        )
                    }
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .height(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
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
                    onAddQuestion(idx)
                }
            )
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
        /*
        AlertDialog(
            onDismissRequest = { showWarning = false },
            confirmButton = {
                TextButton(onClick = {
                    showWarning = false
                    onFolders()
                }) { Text("Klasör Oluştur") }
            },
            dismissButton = {
                TextButton(onClick = { showWarning = false }) { Text("Kapat") }
            },
            icon = {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.cihat.egitim.lottieanimation.R.drawable.knowledge_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            },
            title = { Text("Uyarı") },
            text = { Text("Quiz oluşturmak için klasör oluşturunuz.") }
        )
    }
         */
    }
}


