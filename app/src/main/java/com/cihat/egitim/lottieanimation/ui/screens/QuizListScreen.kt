package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuizListScreen(
    quizzes: List<UserQuiz>,
    folders: List<UserFolder>,
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onAdd: (Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onCreate: (String, Int, List<String>, Int?) -> Unit,
    onLogout: () -> Unit,
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
        var showFolderSelect by remember { mutableStateOf(false) }
        var createName by remember { mutableStateOf("") }
        var createCount by remember { mutableFloatStateOf(4f) }
        val subHeadings = remember { mutableStateListOf<String>() }
        var newSub by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quizzes.isEmpty()) {
                Text("Henüz quiziniz yok")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
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
                        val actionWidth = 72.dp
                        val swipeState = rememberSwipeableState(0)
                        val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }

                        // How much the actions are revealed. 0f when hidden, 1f when fully swiped.
                        val revealProgress = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)

                        // Collapse the item whenever it is swiped to reveal actions
                        LaunchedEffect(swipeState.offset) {
                            if (swipeState.offset.value != 0f) {
                                expanded = false
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clipToBounds()
                                .swipeable(
                                    state = swipeState,
                                    anchors = mapOf(0f to 0, -maxOffset to 1),
                                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                    orientation = Orientation.Horizontal
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .height(72.dp)
                                    .alpha(revealProgress)
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch { swipeState.animateTo(0) }
                                        showRename = true
                                    },
                                    enabled = swipeState.currentValue == 1,
                                    modifier = Modifier
                                        .background(Color(0xFFFFA500))
                                        .size(actionWidth)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                IconButton(
                                    onClick = {
                                        scope.launch { swipeState.animateTo(0) }
                                        showDelete = true
                                    },
                                    enabled = swipeState.currentValue == 1,
                                    modifier = Modifier
                                        .background(Color.Red)
                                        .size(actionWidth)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
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
                                            .clickable { expanded = !expanded }
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = quiz.name, modifier = Modifier.weight(1f))
                                        if (swipeState.offset.value == 0f) {
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = null
                                            )
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
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .aspectRatio(1f)
                                                                .clickable { onView(quizIndex, boxIndex) }
                                                                .padding(4.dp)
                                                                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text(text = "Box ${boxIndex + 1}")
                                                                Text(text = "${box.size} soru")
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Button(onClick = { onQuiz(quizIndex, boxIndex) }) { Text("Quiz") }
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
                                        Button(
                                            onClick = { onAdd(quizIndex) },
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .align(Alignment.CenterHorizontally)
                                        ) { Text("Add Question") }
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
                    }
                    }
                }
                Button(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Logout")
                }
            }
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Quiz Ekle") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .height(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }



        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                confirmButton = {
                    TextButton(onClick = {
                        showCreate = false
                        showFolderSelect = true
                    }) { Text("İleri") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreate = false }) { Text("İptal") }
                },
                title = { Text("Quiz Oluştur") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = { Text("Ad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Slider(
                            value = createCount,
                            onValueChange = { createCount = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                        Text("${createCount.toInt()} kutu")
                        subHeadings.forEachIndexed { index, text ->
                            OutlinedTextField(
                                value = text,
                                onValueChange = { subHeadings[index] = it },
                                label = { Text("Alt Başlık ${index + 1}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = newSub,
                                onValueChange = { newSub = it },
                                label = { Text("Alt Başlık Ekle") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (newSub.isNotBlank()) {
                                    subHeadings.add(newSub)
                                    newSub = ""
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add sub")
                            }
                        }
                    }
                }
            )
        }

        if (showFolderSelect) {
            var selected by remember { mutableStateOf(folders.firstOrNull()?.id) }
            AlertDialog(
                onDismissRequest = { showFolderSelect = false },
                confirmButton = {
                    TextButton(onClick = {
                        onCreate(createName, createCount.toInt(), subHeadings.toList(), selected)
                        showFolderSelect = false
                        createName = ""
                        createCount = 4f
                        subHeadings.clear()
                        newSub = ""
                    }) { Text("Oluştur") }
                },
                dismissButton = {
                    TextButton(onClick = { showFolderSelect = false }) { Text("İptal") }
                },
                title = { Text("Klasör Seç") },
                text = {
                    if (folders.isEmpty()) {
                        Text("Önce klasör oluşturun")
                    } else {
                        Column {
                            folders.forEach { folder ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selected = folder.id }
                                        .padding(4.dp)
                                ) {
                                    RadioButton(
                                        selected = selected == folder.id,
                                        onClick = { selected = folder.id }
                                    )
                                    Text(folder.name, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }
                }
            )
        }
        }
    }

