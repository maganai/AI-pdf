package com.example

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Book
import com.example.data.Bookmark
import com.example.data.ChatMessage
import com.example.data.Note
import com.example.data.PdfAnnotation
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.HighlightAlt
import com.example.ui.BookViewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// Eye-friendly Reader Themes
enum class ReaderTheme(
    val title: String,
    val paperColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val useColorFilter: Boolean,
    val colorFilter: ColorFilter?
) {
    LIGHT(
        "Light",
        Color(0xFFFBFBFB),
        Color(0xFF15181F),
        Color(0xFF4A5568),
        false,
        null
    ),
    SEPIA(
        "Sepia",
        Color(0xFFF4ECD8),
        Color(0xFF433422),
        Color(0xFF6B5843),
        true,
        ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f,     0f,     0f,     1f, 0f
        )))
    ),
    CHARCOAL(
        "Charcoal",
        Color(0xFF2D3748),
        Color(0xFFEDF2F7),
        Color(0xFFA0AEC0),
        true,
        ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            -1f,  0f,  0f,  0f, 255f,
             0f, -1f,  0f,  0f, 255f,
             0f,  0f, -1f,  0f, 255f,
             0f,  0f,  0f,  1f,   0f
        )))
    ),
    MIDNIGHT(
        "Midnight",
        Color(0xFF0F172A),
        Color(0xFF94A3B8),
        Color(0xFF64748B),
        true,
        ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            -0.8f,  0f,  0f,  0f, 200f,
             0f, -0.8f,  0f,  0f, 200f,
             0f,  0f, -0.8f,  0f, 200f,
             0f,  0f,  0f,  1f,   0f
        )))
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: BookViewModel = viewModel()
                val selectedBook by viewModel.selectedBook.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF7F2FA) // Sleek Interface M3 Light Background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectedBook == null) {
                            DashboardScreen(viewModel = viewModel)
                        } else {
                            ReaderScreen(viewModel = viewModel, book = selectedBook!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: BookViewModel) {
    val context = LocalContext.current
    val books by viewModel.books.collectAsState()
    val allBookmarks by viewModel.allBookmarks.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Filter books based on query and category
    val filteredBooks = books.filter { book ->
        val matchesSearch = book.title.contains(searchQuery, ignoreCase = true) ||
                book.author.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || book.category == selectedCategory
        matchesSearch && matchesCategory
    }

    val continueBook = books.filter { it.currentPage > 0 }.maxByOrNull { it.lastReadTime }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importPdf(uri)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F2FA),
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { filePickerLauncher.launch("application/pdf") },
                containerColor = Color(0xFFEADDFF),
                contentColor = Color(0xFF21005D),
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("import_pdf_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import PDF", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Elegant Visual Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AURA CORE",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 2.sp,
                            color = Color(0xFF6750A4)
                        )
                    }
                    Text(
                        text = "Your AI Library",
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = Color(0xFF1D1B20),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                if (isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF6750A4),
                        strokeWidth = 2.5.dp
                    )
                }
            }

            // Search Bar & Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search title, authors...", color = Color(0xFF49454F)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF49454F)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_bar_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF6750A4),
                        unfocusedBorderColor = Color(0xFFCAC4D0),
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categorized Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("All", "Guide", "Imported")
                    for (cat in categories) {
                        val isSelected = selectedCategory == cat
                        val bgChip by animateColorAsState(if (isSelected) Color(0xFFEADDFF) else Color.White)
                        val textChipColor = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(bgChip)
                                .then(
                                    if (!isSelected) {
                                        Modifier.border(
                                            width = 1.dp,
                                            color = Color(0xFFCAC4D0),
                                            shape = RoundedCornerShape(30.dp)
                                        )
                                    } else Modifier
                                )
                                .clickable { viewModel.updateCategory(cat) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textChipColor
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                // Feature "Continue Reading" banner if there's a book user recently opened
                if (continueBook != null && searchQuery.isEmpty()) {
                    item {
                        Text(
                            text = "Continue Reading",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF49454F),
                            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
                        )
                        Card(
                            onClick = { viewModel.selectBook(continueBook) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(20.dp))
                                .testTag("book_card_${continueBook.id}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cover placeholder
                                Box(
                                    modifier = Modifier
                                        .size(60.dp, 84.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.verticalGradient(listOf(Color(0xFFEADDFF), Color(0xFFD0BCFF)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ChromeReaderMode, contentDescription = null, tint = Color(0xFF21005D), modifier = Modifier.size(24.dp))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = continueBook.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1D1B20),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = continueBook.author,
                                        fontSize = 12.sp,
                                        color = Color(0xFF49454F),
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val progressVal = (continueBook.currentPage.toFloat() + 1f) / continueBook.totalPages.toFloat()
                                    val progressPercent = (progressVal * 100).toInt()

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { progressVal },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            color = Color(0xFF6750A4),
                                            trackColor = Color(0xFFE6E0E9)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "$progressPercent%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6750A4)
                                        )
                                    }
                                    Text(
                                        text = "Page ${continueBook.currentPage + 1} of ${continueBook.totalPages}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF49454F),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Library Items
                item {
                    Text(
                        text = "All Books",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF49454F),
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                }

                if (filteredBooks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChromeReaderMode,
                                contentDescription = null,
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your Shelf is Empty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                            Text(
                                text = "Tap + to import details of custom PDF files",
                                fontSize = 12.sp,
                                color = Color(0xFF49454F),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    items(filteredBooks) { book ->
                        Card(
                            onClick = { viewModel.selectBook(book) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                                .testTag("book_card_${book.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Stylized custom icon background based on category
                                val gradientBook = when (book.category) {
                                    "Guide" -> Brush.verticalGradient(listOf(Color(0xFFEADDFF), Color(0xFFD0BCFF)))
                                    else -> Brush.verticalGradient(listOf(Color(0xFFF3EDF7), Color(0xFFEADDFF)))
                                }

                                Box(
                                    modifier = Modifier
                                        .size(46.dp, 64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(gradientBook),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = book.title.take(1).uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color(0xFF21005D)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = book.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1D1B20),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = book.author,
                                        fontSize = 12.sp,
                                        color = Color(0xFF49454F),
                                        maxLines = 1
                                    )

                                    if (book.currentPage > 0) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val progressVal = (book.currentPage.toFloat() + 1f) / book.totalPages.toFloat()
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            LinearProgressIndicator(
                                                progress = { progressVal },
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                color = Color(0xFF6750A4),
                                                trackColor = Color(0xFFE6E0E9)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${(progressVal * 100).toInt()}% read",
                                                fontSize = 10.sp,
                                                color = Color(0xFF49454F),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "${book.totalPages} pages",
                                            fontSize = 11.sp,
                                            color = Color(0xFF49454F),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteBook(book) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete book",
                                        tint = Color(0xFFEF4444).copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Global Bookmarks across all books
                if (allBookmarks.isNotEmpty() && searchQuery.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Global Bookmarks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF49454F),
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                    items(allBookmarks) { bookmark ->
                        val matchingBook = books.find { it.id == bookmark.bookId }
                        if (matchingBook != null) {
                            Card(
                                onClick = { 
                                    viewModel.selectBook(matchingBook)
                                    viewModel.changePage(bookmark.pageNumber)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                                    .testTag("global_bookmark_${bookmark.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark, 
                                        contentDescription = "Bookmark", 
                                        tint = Color(0xFF6750A4), 
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = matchingBook.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1D1B20),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Bookmarked Page ${bookmark.pageNumber + 1}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF49454F)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForwardIos, 
                                        contentDescription = "View", 
                                        tint = Color(0xFF6750A4), 
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReaderScreen(viewModel: BookViewModel, book: Book) {
    var activeTab by remember { mutableStateOf(0) } // 0: AI Assistant, 1: Annotations
    var activeReaderTheme by remember { mutableStateOf(ReaderTheme.SEPIA) }

    val currentPageImage by viewModel.currentPageImage.collectAsState()
    val isRendering by viewModel.isRendering.collectAsState()

    val bookmarks by viewModel.bookmarks.collectAsState()
    val isPageBookmarked = bookmarks.any { it.pageNumber == book.currentPage }
    val annotations by viewModel.annotations.collectAsState()

    var isAnnotationModeActive by remember { mutableStateOf(false) }
    var activeAnnotationTool by remember { mutableStateOf<String?>(null) }
    var activeAnnotationColor by remember { mutableStateOf("#FFFF00") }

    val activePageNote by viewModel.activePageNote.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF7F2FA),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Reader Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.unselectBook() },
                    modifier = Modifier.testTag("back_to_dashboard_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B20))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1D1B20),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Page ${book.currentPage + 1} of ${book.totalPages}",
                        fontSize = 12.sp,
                        color = Color(0xFF49454F)
                    )
                }

                IconButton(
                    onClick = { 
                        isAnnotationModeActive = !isAnnotationModeActive 
                        if (!isAnnotationModeActive) {
                            activeAnnotationTool = null
                        }
                    },
                    modifier = Modifier.testTag("annotation_mode_toggle_tag")
                ) {
                    Icon(
                        imageVector = if (isAnnotationModeActive) Icons.Default.HistoryEdu else Icons.Outlined.HistoryEdu,
                        contentDescription = "Toggle Markup",
                        tint = if (isAnnotationModeActive) Color(0xFF6750A4) else Color(0xFF1D1B20)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleBookmarkActivePage() },
                    modifier = Modifier.testTag("bookmark_toggle_tag")
                ) {
                    Icon(
                        imageVector = if (isPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark Page",
                        tint = if (isPageBookmarked) Color(0xFF6750A4) else Color(0xFF1D1B20)
                    )
                }
            }

            // PDF Book paper grid canvas viewport
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF7F2FA))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeReaderTheme.paperColor)
                        .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRendering) {
                        CircularProgressIndicator(color = activeReaderTheme.iconColor)
                    } else if (currentPageImage != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = currentPageImage!!.asImageBitmap(),
                                contentDescription = "PDF Page View rendered natively",
                                colorFilter = activeReaderTheme.colorFilter,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            // Interactive Markup Overlay Canvas
                            AnnotatableCanvas(
                                modifier = Modifier.fillMaxSize(),
                                bookId = book.id,
                                pageNumber = book.currentPage,
                                annotations = annotations,
                                activeTool = if (isAnnotationModeActive) activeAnnotationTool else null,
                                activeColor = activeAnnotationColor,
                                onAddAnnotation = { viewModel.addAnnotation(it) },
                                onDeleteAnnotation = { viewModel.deleteAnnotation(it) },
                                paperColor = activeReaderTheme.paperColor
                            )

                            // Visual Annotation Floating Edit Controller
                            if (isAnnotationModeActive) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                ) {
                                    AnnotationToolbar(
                                        activeTool = activeAnnotationTool,
                                        onToolSelected = { activeAnnotationTool = it },
                                        activeColor = activeAnnotationColor,
                                        onColorSelected = { activeAnnotationColor = it },
                                        onClearPage = { viewModel.clearAnnotationsForActivePage() }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Could not render page.\nTap '+' to import valid PDF files.",
                            color = activeReaderTheme.textColor,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Page Browsing Slider & Eye-friendly styling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3EDF7))
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFE6E0E9),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pg ${book.currentPage + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        modifier = Modifier.width(42.dp)
                    )

                    Slider(
                        value = book.currentPage.toFloat(),
                        onValueChange = { viewModel.changePage(it.toInt()) },
                        valueRange = 0f..(book.totalPages - 1).coerceAtLeast(1).toFloat(),
                        steps = (book.totalPages - 2).coerceAtLeast(0),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6750A4),
                            activeTrackColor = Color(0xFF6750A4),
                            inactiveTrackColor = Color(0xFFE6E0E9)
                        )
                    )

                    Text(
                        text = "${book.totalPages}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // Eye theme selection selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Eye Comfort:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF49454F)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (theme in ReaderTheme.values()) {
                            val isSelected = activeReaderTheme == theme
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color(0xFFEADDFF) else Color.White)
                                    .clickable { activeReaderTheme = theme }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else Color(0xFFCAC4D0),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            ) {
                                Text(
                                    text = theme.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)
                                )
                            }
                        }
                    }
                }
            }

            // Tab navigation selector (AI Companion vs Annotations/Bookmarks)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF3EDF7))
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFE6E0E9),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: AI Assistant
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { activeTab = 0 }
                        .drawBehind {
                            if (activeTab == 0) {
                                drawLine(
                                    color = Color(0xFF6750A4),
                                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (activeTab == 0) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = if (activeTab == 0) Color(0xFF6750A4) else Color(0xFF49454F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Companion",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) Color(0xFF6750A4) else Color(0xFF49454F)
                        )
                    }
                }

                // Tab 1: Annotations
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { activeTab = 1 }
                        .drawBehind {
                            if (activeTab == 1) {
                                drawLine(
                                    color = Color(0xFF6750A4),
                                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (activeTab == 1) Icons.AutoMirrored.Filled.ListAlt else Icons.AutoMirrored.Outlined.ListAlt,
                            contentDescription = "Book Annotations",
                            tint = if (activeTab == 1) Color(0xFF6750A4) else Color(0xFF49454F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Study Notes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) Color(0xFF6750A4) else Color(0xFF49454F)
                        )
                    }
                }
            }

            // Tab Panels viewport
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxWidth()
                    .background(Color(0xFFF7F2FA))
            ) {
                if (activeTab == 0) {
                    AiCompanionPanel(viewModel = viewModel, book = book, chatMessages = chatMessages)
                } else {
                    StudyNotesPanel(viewModel = viewModel, book = book, bookmarks = bookmarks, note = activePageNote)
                }
            }
        }
    }
}

@Composable
fun AiCompanionPanel(
    viewModel: BookViewModel,
    book: Book,
    chatMessages: List<ChatMessage>
) {
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    var userPrompt by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Scroll chat to the last item on list updates
    LaunchedEffect(chatMessages.size, isAiLoading) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Actions Row (Generate Summary / Translate Spanish / Vocalize TTS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3EDF7))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auto Page Summary
                Button(
                    onClick = { viewModel.triggerQuickSummary() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF381E72)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("ai_summary_trigger_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF381E72), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Summary", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Translate Page
                Button(
                    onClick = { viewModel.triggerTranslation() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0BCFF),
                        contentColor = Color(0xFF381E72)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = Color(0xFF381E72), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Translate", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Audio synthesis control (Speak Summary aloud)
            IconButton(
                onClick = {
                    if (isSpeaking) {
                        viewModel.stopSpeaking()
                    } else {
                        // Find last AI response or make current notes vocal
                        val parsedText = chatMessages.lastOrNull { !it.isUser }?.message
                            ?: "No AI message found to read aloud. Try tapping 'Auto Summary' first to compile review materials."
                        viewModel.speak(parsedText)
                    }
                },
                modifier = Modifier
                    .size(32.dp)
                    .background(if (isSpeaking) Color(0xFFEF4444) else Color(0xFFEADDFF), CircleShape)
                    .testTag("play_tts_voice_button")
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = "Speak page text synthetic aloud",
                    tint = if (isSpeaking) Color.White else Color(0xFF21005D),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        if (isAiLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.5.dp),
                color = Color(0xFF6750A4),
                trackColor = Color.Transparent
            )
        }

        // Chat Console Logs
        Box(modifier = Modifier.weight(1f)) {
            if (chatMessages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Reading Companion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "Tap 'Auto Summary' or type questions to research details natively on this page",
                        fontSize = 11.sp,
                        color = Color(0xFF49454F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp).padding(top = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (msg.isUser) 16.dp else 0.dp,
                                            bottomEnd = if (msg.isUser) 0.dp else 16.dp
                                        )
                                    )
                                    .background(if (msg.isUser) Color(0xFFD0BCFF) else Color.White)
                                    .then(
                                        if (!msg.isUser) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = Color(0xFFCAC4D0),
                                                shape = RoundedCornerShape(
                                                    topStart = 16.dp,
                                                    topEnd = 16.dp,
                                                    bottomStart = 0.dp,
                                                    bottomEnd = 16.dp
                                                )
                                            )
                                        } else Modifier
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.message,
                                    fontSize = 13.sp,
                                    color = if (msg.isUser) Color(0xFF381E72) else Color(0xFF1D1B20),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3EDF7))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.clearChat() }
            ) {
                Icon(Icons.Default.HistoryEdu, contentDescription = "Clear History", tint = Color(0xFFEF4444).copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.width(4.dp))

            TextField(
                value = userPrompt,
                onValueChange = { userPrompt = it },
                placeholder = { Text("Ask anything about this page...", fontSize = 13.sp, color = Color(0xFF49454F)) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (userPrompt.isNotEmpty() && !isAiLoading) {
                        viewModel.submitUserChat(userPrompt)
                        userPrompt = ""
                        keyboardController?.hide()
                    }
                }),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (userPrompt.isNotEmpty() && !isAiLoading) {
                        viewModel.submitUserChat(userPrompt)
                        userPrompt = ""
                        keyboardController?.hide()
                    }
                },
                enabled = userPrompt.isNotEmpty() && !isAiLoading,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (userPrompt.isNotEmpty() && !isAiLoading) Color(0xFFEADDFF) else Color(0xFFCAC4D0),
                        CircleShape
                    )
                    .testTag("send_chat_message_button")
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send prompt",
                    tint = if (userPrompt.isNotEmpty() && !isAiLoading) Color(0xFF21005D) else Color(0xFF49454F),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StudyNotesPanel(
    viewModel: BookViewModel,
    book: Book,
    bookmarks: List<Bookmark>,
    note: Note?
) {
    var noteInput by remember(note) { mutableStateOf(note?.content ?: "") }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Active Page Note, 1: Bookmarks, 2: Annotations

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Toggle Buttons sub headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeSubTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == 0) Color(0xFFEADDFF) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (activeSubTab != 0) BorderStroke(1.dp, Color(0xFFCAC4D0)) else null,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Page Notes",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 0) Color(0xFF21005D) else Color(0xFF49454F)
                )
            }

            Button(
                onClick = { activeSubTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == 1) Color(0xFFEADDFF) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (activeSubTab != 1) BorderStroke(1.dp, Color(0xFFCAC4D0)) else null,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Bookmarks (${bookmarks.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 1) Color(0xFF21005D) else Color(0xFF49454F)
                )
            }

            Button(
                onClick = { activeSubTab = 2 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == 2) Color(0xFFEADDFF) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (activeSubTab != 2) BorderStroke(1.dp, Color(0xFFCAC4D0)) else null,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Annotations",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 2) Color(0xFF21005D) else Color(0xFF49454F)
                )
            }
        }

        if (activeSubTab == 0) {
            // Study annotation notes editor
            Text(
                text = "Write your notes for Page ${book.currentPage + 1}:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = noteInput,
                onValueChange = {
                    noteInput = it
                    viewModel.saveActivePageNote(it)
                },
                placeholder = { Text("Outline formulas, vocabulary definitions, questions or thoughts...", fontSize = 13.sp, color = Color(0xFF49454F)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color(0xFFCAC4D0),
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20)
                )
            )
        } else if (activeSubTab == 1) {
            // Bookmarked page list jumps directory
            if (bookmarks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color(0xFFD0BCFF), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("No Bookmark Landmarks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                    Text("Pin page icon in reader header to save reference tags.", fontSize = 10.sp, color = Color(0xFF49454F))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(bookmarks) { bk ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(10.dp))
                                .clickable { viewModel.changePage(bk.pageNumber) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bookmarked Page ${bk.pageNumber + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            }
                            Text(
                                text = "Go to Page ->",
                                fontSize = 11.sp,
                                color = Color(0xFF6750A4),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            // Annotations Directory view list
            val annotations = viewModel.annotations.collectAsState().value
            if (annotations.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFFD0BCFF), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("No markup annotations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                    Text("Tap the brush icon in the toolbar to draw on active pages.", fontSize = 10.sp, color = Color(0xFF49454F))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(annotations) { ann ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(10.dp))
                                .clickable { viewModel.changePage(ann.pageNumber) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (ann.type) {
                                    "HIGHLIGHT" -> Icons.Default.EventNote
                                    "DRAWING" -> Icons.Default.HistoryEdu
                                    else -> Icons.Default.NoteAlt
                                }
                                val textDesc = when (ann.type) {
                                    "HIGHLIGHT" -> "Text Highlight Tracker"
                                    "DRAWING" -> "Freehand markup sketch"
                                    else -> "Sticky: \"${ann.content?.take(25) ?: "Memo..."}\""
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = try { Color(android.graphics.Color.parseColor(ann.colorHex)) } catch (e: Exception) { Color(0xFF6750A4) },
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = textDesc,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D1B20),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Page ${ann.pageNumber + 1}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF49454F)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.deleteAnnotation(ann) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Annotation",
                                    tint = Color.Red.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnnotatableCanvas(
    modifier: Modifier,
    bookId: Long,
    pageNumber: Int,
    annotations: List<PdfAnnotation>,
    activeTool: String?,
    activeColor: String,
    onAddAnnotation: (PdfAnnotation) -> Unit,
    onDeleteAnnotation: (PdfAnnotation) -> Unit,
    paperColor: Color
) {
    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var showStickyDialog by remember { mutableStateOf(false) }
    var stickyDialogCoords by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    var selectedInteractiveSticky by remember { mutableStateOf<PdfAnnotation?>(null) }
    var annotationTextInput by remember { mutableStateOf("") }

    if (showStickyDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = {
            showStickyDialog = false
            selectedInteractiveSticky = null
            annotationTextInput = ""
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedInteractiveSticky != null) "Edit Sticky Note" else "Add Sticky Note",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1D1B20)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = annotationTextInput,
                        onValueChange = { annotationTextInput = it },
                        placeholder = { Text("Write your thoughts...", fontSize = 13.sp, color = Color(0xFF49454F)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCAC4D0)
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedInteractiveSticky != null) {
                            Button(
                                onClick = {
                                    onDeleteAnnotation(selectedInteractiveSticky!!)
                                    showStickyDialog = false
                                    selectedInteractiveSticky = null
                                    annotationTextInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                            ) {
                                Text("Delete", color = Color.White, fontSize = 12.sp)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    showStickyDialog = false
                                    selectedInteractiveSticky = null
                                    annotationTextInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEADDFF), contentColor = Color(0xFF21005D))
                            ) {
                                Text("Cancel", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    if (annotationTextInput.isNotBlank()) {
                                        if (selectedInteractiveSticky != null) {
                                            onAddAnnotation(selectedInteractiveSticky!!.copy(
                                                content = annotationTextInput,
                                                timestamp = System.currentTimeMillis()
                                            ))
                                        } else if (stickyDialogCoords != null) {
                                            onAddAnnotation(
                                                PdfAnnotation(
                                                    bookId = bookId,
                                                    pageNumber = pageNumber,
                                                    type = "STICKY",
                                                    colorHex = activeColor,
                                                    content = annotationTextInput,
                                                    x = stickyDialogCoords!!.first,
                                                    y = stickyDialogCoords!!.second,
                                                    timestamp = System.currentTimeMillis()
                                                )
                                            )
                                        }
                                    }
                                    showStickyDialog = false
                                    selectedInteractiveSticky = null
                                    annotationTextInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                            ) {
                                Text("Save", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(activeTool, activeColor) {
                if (activeTool == "STICKY") {
                    detectTapGestures { offset ->
                        val relX = offset.x / size.width
                        val relY = offset.y / size.height

                        val tappedSticky = annotations.find { ann ->
                            if (ann.type == "STICKY" && ann.pageNumber == pageNumber) {
                                val sx = ann.x * size.width
                                val sy = ann.y * size.height
                                val dx = offset.x - sx
                                val dy = offset.y - sy
                                Math.sqrt((dx * dx + dy * dy).toDouble()) < 35.0
                            } else false
                        }

                        if (tappedSticky != null) {
                            selectedInteractiveSticky = tappedSticky
                            annotationTextInput = tappedSticky.content ?: ""
                            showStickyDialog = true
                        } else {
                            stickyDialogCoords = Pair(relX, relY)
                            selectedInteractiveSticky = null
                            annotationTextInput = ""
                            showStickyDialog = true
                        }
                    }
                } else if (activeTool == "PEN" || activeTool == "HIGHLIGHT") {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPathPoints = listOf(offset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentPathPoints = currentPathPoints + change.position
                        },
                        onDragEnd = {
                            if (currentPathPoints.size > 1) {
                                val pointsStr = currentPathPoints.joinToString(";") { offset ->
                                    val rx = offset.x / size.width
                                    val ry = offset.y / size.height
                                    "$rx,$ry"
                                }
                                onAddAnnotation(
                                    PdfAnnotation(
                                        bookId = bookId,
                                        pageNumber = pageNumber,
                                        type = activeTool,
                                        colorHex = activeColor,
                                        pointsData = pointsStr,
                                        thickness = if (activeTool == "HIGHLIGHT") 24f else 6f,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            }
                            currentPathPoints = emptyList()
                        }
                    )
                } else {
                    detectTapGestures { offset ->
                        val tappedSticky = annotations.find { ann ->
                            if (ann.type == "STICKY" && ann.pageNumber == pageNumber) {
                                val sx = ann.x * size.width
                                val sy = ann.y * size.height
                                val dx = offset.x - sx
                                val dy = offset.y - sy
                                Math.sqrt((dx * dx + dy * dy).toDouble()) < 35.0
                            } else false
                        }
                        if (tappedSticky != null) {
                            selectedInteractiveSticky = tappedSticky
                            annotationTextInput = tappedSticky.content ?: ""
                            showStickyDialog = true
                        }
                    }
                }
            }
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            annotations.forEach { ann ->
                if (ann.pageNumber == pageNumber && ann.type == "HIGHLIGHT" && !ann.pointsData.isNullOrEmpty()) {
                    val path = Path()
                    val rawPoints = ann.pointsData.split(";")
                    var isFirst = true
                    rawPoints.forEach { pt ->
                        val xy = pt.split(",")
                        if (xy.size == 2) {
                            val rx = xy[0].toFloatOrNull() ?: 0f
                            val ry = xy[1].toFloatOrNull() ?: 0f
                            val px = rx * canvasWidth
                            val py = ry * canvasHeight
                            if (isFirst) {
                                path.moveTo(px, py)
                                isFirst = false
                            } else {
                                path.lineTo(px, py)
                            }
                        }
                    }
                    if (!isFirst) {
                        val strokeColor = try {
                            val hexVal = ann.colorHex.replace("#", "")
                            Color(android.graphics.Color.parseColor("#44$hexVal"))
                        } catch (e: Exception) {
                            Color(0x44FFFF00)
                        }

                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(
                                width = ann.thickness,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            annotations.forEach { ann ->
                if (ann.pageNumber == pageNumber && ann.type == "DRAWING" && !ann.pointsData.isNullOrEmpty()) {
                    val path = Path()
                    val rawPoints = ann.pointsData.split(";")
                    var isFirst = true
                    rawPoints.forEach { pt ->
                        val xy = pt.split(",")
                        if (xy.size == 2) {
                            val rx = xy[0].toFloatOrNull() ?: 0f
                            val ry = xy[1].toFloatOrNull() ?: 0f
                            val px = rx * canvasWidth
                            val py = ry * canvasHeight
                            if (isFirst) {
                                path.moveTo(px, py)
                                isFirst = false
                            } else {
                                path.lineTo(px, py)
                            }
                        }
                    }
                    if (!isFirst) {
                        val strokeColor = try {
                            val hexVal = ann.colorHex
                            Color(android.graphics.Color.parseColor(hexVal))
                        } catch (e: Exception) {
                            Color.Red
                        }

                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(
                                width = ann.thickness,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            if (currentPathPoints.size > 1 && (activeTool == "PEN" || activeTool == "HIGHLIGHT")) {
                val activePath = Path()
                activePath.moveTo(currentPathPoints[0].x, currentPathPoints[0].y)
                for (i in 1 until currentPathPoints.size) {
                    activePath.lineTo(currentPathPoints[i].x, currentPathPoints[i].y)
                }

                val strokeColor = try {
                    if (activeTool == "HIGHLIGHT") {
                        val hexVal = activeColor.replace("#", "")
                        Color(android.graphics.Color.parseColor("#44$hexVal"))
                    } else {
                        Color(android.graphics.Color.parseColor(activeColor))
                    }
                } catch (e: Exception) {
                    Color.Yellow
                }

                drawPath(
                    path = activePath,
                    color = strokeColor,
                    style = Stroke(
                        width = if (activeTool == "HIGHLIGHT") 24f else 6f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        annotations.forEach { ann ->
            if (ann.pageNumber == pageNumber && ann.type == "STICKY") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val posX = (ann.x * constraints.maxWidth).toInt() - (placeable.width / 2)
                            val posY = (ann.y * constraints.maxHeight).toInt() - placeable.height
                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(posX, posY)
                            }
                        }
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(ann.colorHex))
                            } catch (e: Exception) {
                                Color(0xFFFFC107)
                            }
                        )
                        .border(1.5.dp, Color.White, CircleShape)
                        .padding(4.dp)
                        .clickable {
                            selectedInteractiveSticky = ann
                            annotationTextInput = ann.content ?: ""
                            showStickyDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NoteAlt,
                        contentDescription = "Pin notes marker text representation",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnotationToolbar(
    activeTool: String?,
    onToolSelected: (String?) -> Unit,
    activeColor: String,
    onColorSelected: (String) -> Unit,
    onClearPage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .padding(12.dp)
            .border(1.dp, Color(0xFFEADDFF), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onToolSelected(if (activeTool == "PEN") null else "PEN") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "PEN") Color(0xFFEADDFF) else Color.Transparent, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = "Freehand drawing tool",
                        tint = if (activeTool == "PEN") Color(0xFF21005D) else Color(0xFF49454F),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { onToolSelected(if (activeTool == "HIGHLIGHT") null else "HIGHLIGHT") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "HIGHLIGHT") Color(0xFFEADDFF) else Color.Transparent, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "Text highlight tool",
                        tint = if (activeTool == "HIGHLIGHT") Color(0xFF21005D) else Color(0xFF49454F),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { onToolSelected(if (activeTool == "STICKY") null else "STICKY") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "STICKY") Color(0xFFEADDFF) else Color.Transparent, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.NoteAlt,
                        contentDescription = "Sticky Memo pins tool",
                        tint = if (activeTool == "STICKY") Color(0xFF21005D) else Color(0xFF49454F),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFCAC4D0))
                )

                IconButton(
                    onClick = { onClearPage() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear active page markups",
                        tint = Color(0xFFBA1A1A),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (activeTool != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val colorsList = listOf(
                        "#FFFF00" to "Yellow",
                        "#4CAF50" to "Green",
                        "#FF4081" to "Pink",
                        "#00BCD4" to "Cyan",
                        "#9C27B0" to "Purple"
                    )
                    colorsList.forEach { (hex, name) ->
                        val isColorSelected = activeColor == hex
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(
                                    width = if (isColorSelected) 2.dp else 1.dp,
                                    color = if (isColorSelected) Color(0xFF6750A4) else Color.White,
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(hex) }
                        )
                    }
                }
            }
        }
    }
}
