package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.Book
import com.example.data.BookRepository
import com.example.data.Bookmark
import com.example.data.ChatMessage
import com.example.data.Note
import com.example.data.PdfAnnotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale

class BookViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: BookRepository
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    // State flows
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private val _currentPageImage = MutableStateFlow<Bitmap?>(null)
    val currentPageImage: StateFlow<Bitmap?> = _currentPageImage.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _allBookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val allBookmarks: StateFlow<List<Bookmark>> = _allBookmarks.asStateFlow()

    private val _annotations = MutableStateFlow<List<PdfAnnotation>>(emptyList())
    val annotations: StateFlow<List<PdfAnnotation>> = _annotations.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _activePageNote = MutableStateFlow<Note?>(null)
    val activePageNote: StateFlow<Note?> = _activePageNote.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Loading & UI States
    private val _isRendering = MutableStateFlow(false)
    val isRendering: StateFlow<Boolean> = _isRendering.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BookRepository(database.bookDao())
        
        // Initialize TTS
        tts = TextToSpeech(application, this)

        // Fetch Books, run initial guide generation if empty
        viewModelScope.launch {
            repository.allBooks.collect { bookList ->
                if (bookList.isEmpty()) {
                    generateAndRegisterSampleGuide()
                } else {
                    _books.value = bookList
                }
            }
        }

        // Collect all global bookmarks
        viewModelScope.launch {
            repository.allBookmarks.collect { bookmarkList ->
                _allBookmarks.value = bookmarkList
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInitialized = true
            }
        }
    }

    fun speak(text: String) {
        if (!isTtsInitialized || tts == null) return
        viewModelScope.launch {
            _isSpeaking.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PDF_READER_UTTERANCE")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    private suspend fun generateAndRegisterSampleGuide() = withContext(Dispatchers.IO) {
        val context = getApplication<Application>()
        val samplePath = generateSamplePdf(context)
        val file = File(samplePath)

        if (file.exists()) {
            val sampleBook = Book(
                title = "The AI Reading Era: A Practical Guide",
                author = "Gemini AI",
                filePath = file.absolutePath,
                category = "Guide",
                totalPages = 3,
                currentPage = 0
            )
            repository.insertBook(sampleBook)
        }
    }

    private fun generateSamplePdf(context: Context): String {
        val file = File(context.filesDir, "sample_guide.pdf")
        if (file.exists()) return file.absolutePath

        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Page 1
        val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas

        paint.textSize = 24f
        paint.isFakeBoldText = true
        paint.color = Color.DKGRAY
        canvas1.drawText("AI Reading Era: Guide to Latest Features", 40f, 60f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        paint.color = Color.GRAY
        canvas1.drawText("By Gemini Assistant", 40f, 85f, paint)

        paint.color = Color.LTGRAY
        canvas1.drawLine(40f, 105f, 555f, 105f, paint)

        paint.textSize = 12f
        paint.color = Color.BLACK
        val body1 = listOf(
            "Welcome to the next generation of mobile PDF reading experiences!",
            "This guide is a real PDF document generated programmatically on your",
            "Android device to demonstrate native PDF rendering, bookmark synchronization,",
            "text-to-speech reading, and Gemini AI companion features.",
            "",
            "With standard document readers, you are a passive learner. In the AI era,",
            "every document becomes an interactive conversation space.",
            "",
            "KEY FEATURE 1: MULTIMODAL GEMINI AI COMPANION",
            "By tapping the 'AI Assistant' button on the reader screen, you can:",
            "1. Request an automatic, concise summary of the active page text.",
            "2. Interact in real-time - ask questions, clarify formulas, check grammar,",
            "   or define jargon instantly.",
            "3. Capture the page as a rich visual bitmap (thanks to Gemini 3.5's core",
            "   vision intelligence) so layout, diagrams, and formatting are preserved.",
            "",
            "We welcome you to turn to Page 2 to learn about our Audio Companion & bookmarks!"
        )
        var yOffset1 = 140f
        for (line in body1) {
            canvas1.drawText(line, 40f, yOffset1, paint)
            yOffset1 += 22f
        }
        pdfDocument.finishPage(page1)

        // Page 2
        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        val canvas2 = page2.canvas

        paint.textSize = 20f
        paint.isFakeBoldText = true
        paint.color = Color.DKGRAY
        canvas2.drawText("Chapter 2: Audio Companion & Progress Sync", 40f, 60f, paint)

        paint.color = Color.LTGRAY
        canvas2.drawLine(40f, 80f, 555f, 80f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = Color.BLACK
        val body2 = listOf(
            "Modern readers expect accessibility support across all their utilities.",
            "This app packs rich interactive tools designed for a seamless reader journey:",
            "",
            "KEY FEATURE 2: NATIVE TEXT-TO-SPEECH (PLAY AUDIO)",
            "Tapping the 'Play Audio' button activates Android's local Speech synthesis",
            "engine to read the summary or generated notes out loud. Perfect for hands-free",
            "multitasking or auditory learners looking to absorb information on the go.",
            "",
            "KEY FEATURE 3: BOOKMARKS & PERSONAL NOTATION",
            "Keep high-impact quotes and formulas close at hand. Under the annotations tab:",
            "- You can pin specific pages as Bookmarks.",
            "- Write personalized page-level notes and study checklists.",
            "- Your notes and bookmarks are stored securely on-device using a high-",
            "  efficiency SQLite database powered by Android Jetpack Room.",
            "",
            "KEY FEATURE 4: READING POSITION RESTORATION",
            "No more scanning to find your page. Every time you close a document or quit the",
            "app, your exact last-viewed page is synchronized. Launching the book from",
            "the Dashboard instantly transports you right back to your exact reading session.",
            "",
            "Let's move onto Page 3 to see the technology stack and AI setup guidelines!"
        )
        var yOffset2 = 115f
        for (line in body2) {
            canvas2.drawText(line, 40f, yOffset2, paint)
            yOffset2 += 22f
        }
        pdfDocument.finishPage(page2)

        // Page 3
        val pageInfo3 = PdfDocument.PageInfo.Builder(595, 842, 3).create()
        val page3 = pdfDocument.startPage(pageInfo3)
        val canvas3 = page3.canvas

        paint.textSize = 20f
        paint.isFakeBoldText = true
        paint.color = Color.DKGRAY
        canvas3.drawText("Chapter 3: Connecting the AI Engine", 40f, 60f, paint)

        paint.color = Color.LTGRAY
        canvas3.drawLine(40f, 80f, 555f, 80f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = Color.BLACK
        val body3 = listOf(
            "To enable direct page summaries and interactive text conversations, this app",
            "connects to Google's flagship Gemini 3.5 Flash model.",
            "",
            "SECURITY CONSTRAINTS & CREDENTIAL SETTING:",
            "- Hardcoded API keys are a major security vulnerability as APK compilation",
            "  can be easily decompiled and extracted.",
            "- This app complies strictly with modern secret injection protocols. It accesses",
            "  your Gemini API Key securely using Android's BuildConfig context.",
            "- To run your AI companion in active mode, enter your API Key in the AI Studio",
            "  Secrets Panel.",
            "",
            "INTERVENTIONS & FALLBACK MODES:",
            "If you do not have an active key, the Gemini model remains inactive, but all core",
            "reader utilities stay 100% operational! You can still read PDFs, navigate,",
            "create bookmarks, add notes, and invoke localized text-to-speech flawlessly.",
            "",
            "We are excited for you to import your own eBooks, resumes, and study materials!",
            "Tap the '+' FAB icon on the Library Dashboard to pick any custom PDF from",
            "your local files directory and enjoy a modern, private reading workflow."
        )
        var yOffset3 = 115f
        for (line in body3) {
            canvas3.drawText(line, 40f, yOffset3, paint)
            yOffset3 += 22f
        }
        pdfDocument.finishPage(page3)

        try {
            val outStream = FileOutputStream(file)
            pdfDocument.writeTo(outStream)
            outStream.close()
        } catch (e: Exception) {
            Log.e("BookViewModel", "Failed to generate guide PDF", e)
        } finally {
            pdfDocument.close()
        }
        return file.absolutePath
    }

    fun selectBook(book: Book) {
        viewModelScope.launch {
            // Save current lastReadTime
            val updated = book.copy(lastReadTime = System.currentTimeMillis())
            repository.updateBook(updated)
            _selectedBook.value = updated
            loadActivePage(updated.currentPage)
            loadActiveNotesAndBookmarks(updated.id, updated.currentPage)
        }
    }

    fun unselectBook() {
        stopSpeaking()
        _selectedBook.value = null
        _currentPageImage.value = null
        _bookmarks.value = emptyList()
        _notes.value = emptyList()
        _activePageNote.value = null
        _chatMessages.value = emptyList()
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
            if (_selectedBook.value?.id == book.id) {
                unselectBook()
            }
            // Delete the local file associated
            try {
                val f = File(book.filePath)
                if (f.exists() && book.filePath.contains(getApplication<Application>().filesDir.name)) {
                    f.delete()
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Could not delete local file", e)
            }
        }
    }

    fun changePage(pageIndex: Int) {
        val current = _selectedBook.value ?: return
        if (pageIndex < 0 || pageIndex >= current.totalPages) return
        viewModelScope.launch {
            val updated = current.copy(currentPage = pageIndex, lastReadTime = System.currentTimeMillis())
            repository.updateBook(updated)
            _selectedBook.value = updated
            loadActivePage(pageIndex)
            loadActiveNotesAndBookmarks(updated.id, pageIndex)
        }
    }

    private fun loadActivePage(pageIndex: Int) {
        val current = _selectedBook.value ?: return
        _isRendering.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val renderedBitmap = renderPdfPage(current.filePath, pageIndex)
            withContext(Dispatchers.Main) {
                _currentPageImage.value = renderedBitmap
                _isRendering.value = false
            }
        }
    }

    private fun loadActiveNotesAndBookmarks(bookId: Long, pageIndex: Int) {
        viewModelScope.launch {
            repository.getBookmarksForBook(bookId).collect { list ->
                _bookmarks.value = list
            }
        }
        viewModelScope.launch {
            repository.getAnnotationsForBook(bookId).collect { list ->
                _annotations.value = list
            }
        }
        viewModelScope.launch {
            repository.getNotesForBook(bookId).collect { list ->
                _notes.value = list
                _activePageNote.value = list.find { it.pageNumber == pageIndex }
            }
        }
        viewModelScope.launch {
            repository.getChatMessagesForBook(bookId).collect { list ->
                _chatMessages.value = list
            }
        }
    }

    private suspend fun renderPdfPage(filePath: String, pageNumber: Int): Bitmap? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null

            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)

            if (pageNumber < 0 || pageNumber >= renderer.pageCount) {
                renderer.close()
                pfd.close()
                return null
            }

            val page = renderer.openPage(pageNumber)

            // Dynamic scale: 2x quality for high resolution display in reading card
            val bitmapWidth = (page.width * 2.5).toInt()
            val bitmapHeight = (page.height * 2.5).toInt()

            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close()
            renderer.close()
            pfd.close()
            bitmap
        } catch (e: Exception) {
            Log.e("BookViewModel", "Error rendering page $pageNumber", e)
            null
        }
    }

    // Bookmarking
    fun toggleBookmarkActivePage() {
        val currentBook = _selectedBook.value ?: return
        val currentBkFlow = _bookmarks.value
        val existing = currentBkFlow.find { it.pageNumber == currentBook.currentPage }

        viewModelScope.launch {
            if (existing != null) {
                repository.deleteBookmark(existing)
            } else {
                repository.insertBookmark(
                    Bookmark(
                        bookId = currentBook.id,
                        pageNumber = currentBook.currentPage,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // Annotations management
    fun addAnnotation(annotation: PdfAnnotation) {
        viewModelScope.launch {
            repository.insertAnnotation(annotation)
        }
    }

    fun deleteAnnotation(annotation: PdfAnnotation) {
        viewModelScope.launch {
            repository.deleteAnnotation(annotation)
        }
    }

    fun clearAnnotationsForActivePage() {
        val currentBook = _selectedBook.value ?: return
        viewModelScope.launch {
            repository.clearAnnotationsForPage(currentBook.id, currentBook.currentPage)
        }
    }

    // Saving page-level notes
    fun saveActivePageNote(content: String) {
        val currentBook = _selectedBook.value ?: return
        viewModelScope.launch {
            val existing = repository.getNoteByPage(currentBook.id, currentBook.currentPage)
            if (content.trim().isEmpty()) {
                if (existing != null) {
                    repository.deleteNote(existing)
                }
            } else {
                if (existing != null) {
                    repository.insertNote(existing.copy(content = content, timestamp = System.currentTimeMillis()))
                } else {
                    repository.insertNote(
                        Note(
                            bookId = currentBook.id,
                            pageNumber = currentBook.currentPage,
                            content = content,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
            // Trigger flow update
            loadActiveNotesAndBookmarks(currentBook.id, currentBook.currentPage)
        }
    }

    // Import external PDF files
    fun importPdf(uri: Uri) {
        _isImporting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            try {
                val contentResolver = context.contentResolver
                // Find clean name from URI
                var fileName = "imported_book_${System.currentTimeMillis()}.pdf"
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex)
                    }
                }

                // Copy stream to files directory
                val destinationFile = File(context.filesDir, "imported_${System.currentTimeMillis()}_$fileName")
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val outStream = FileOutputStream(destinationFile)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outStream.write(buffer, 0, bytesRead)
                    }
                    inputStream.close()
                    outStream.close()

                    // Parse page count
                    val pfd = ParcelFileDescriptor.open(destinationFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(pfd)
                    val pages = renderer.pageCount
                    renderer.close()
                    pfd.close()

                    // Create Book record
                    val bookTitle = fileName.replace(".pdf", "", ignoreCase = true)
                    val importedBook = Book(
                        title = bookTitle,
                        author = "My Documents",
                        filePath = destinationFile.absolutePath,
                        category = "Imported",
                        totalPages = pages,
                        currentPage = 0
                    )

                    repository.insertBook(importedBook)
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Failed to import PDF file", e)
            } finally {
                withContext(Dispatchers.Main) {
                    _isImporting.value = false
                }
            }
        }
    }

    // AI conversational logic
    fun clearChat() {
        val currentBook = _selectedBook.value ?: return
        viewModelScope.launch {
            repository.clearChatHistory(currentBook.id)
        }
    }

    fun triggerQuickSummary() {
        val currentBook = _selectedBook.value ?: return
        val bitmap = _currentPageImage.value ?: return

        sendAiMessage(
            systemPrompt = "You are an expert summary generator. Review this visual rendering of page ${currentBook.currentPage + 1} from '${currentBook.title}' and provide a clear, beautifully structured 3-bullet core takeaway summary explaining what is presented.",
            textPrompt = "Summarize page ${currentBook.currentPage + 1}.",
            bitmap = bitmap
        )
    }

    fun triggerTranslation() {
        val currentBook = _selectedBook.value ?: return
        val bitmap = _currentPageImage.value ?: return

        sendAiMessage(
            systemPrompt = "You are a professional literary translator. Read this page and translate its core textual paragraphs into standard cohesive Spanish, explaining the passage concisely.",
            textPrompt = "Translate page ${currentBook.currentPage + 1} core components to Spanish.",
            bitmap = bitmap
        )
    }

    fun submitUserChat(prompt: String) {
        if (prompt.trim().isEmpty()) return
        val currentBook = _selectedBook.value ?: return
        val bitmap = _currentPageImage.value

        viewModelScope.launch {
            // Save user message to database
            repository.insertChatMessage(
                ChatMessage(
                    bookId = currentBook.id,
                    isUser = true,
                    message = prompt,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Trigger AI compilation
            sendAiMessage(
                systemPrompt = "You are an expert reading study assistant. You are answering user's direct questions about page ${currentBook.currentPage + 1} of '${currentBook.title}'. Keep answers precise, helpful, educational, and formatting clean.",
                textPrompt = prompt,
                bitmap = bitmap
            )
        }
    }

    private fun sendAiMessage(systemPrompt: String, textPrompt: String, bitmap: Bitmap?) {
        val currentBook = _selectedBook.value ?: return
        _isAiLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                withContext(Dispatchers.Main) {
                    repository.insertChatMessage(
                        ChatMessage(
                            bookId = currentBook.id,
                            isUser = false,
                            message = "⚠️ No API Key Detected!\n\nTo unlock the active Gemini AI reading companion, please specify your Google Gemini API Key in the AI Studio Secrets panel.\n\nAll core offline features (TTS, Bookmarks, and Notes) remain fully interactive!",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    _isAiLoading.value = false
                }
                return@launch
            }

            try {
                // Conver bitmap to base64
                val base64Image = bitmap?.toBase64()

                val parts = mutableListOf<Part>()
                parts.add(Part(text = "System Directive: $systemPrompt\nUser Request: $textPrompt"))
                if (base64Image != null) {
                    parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image)))
                }

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = parts)),
                    generationConfig = GenerationConfig(temperature = 0.4f)
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val aiReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Sorry, I was unable to read this page. Could you try summarizing or asking again?"

                withContext(Dispatchers.Main) {
                    repository.insertChatMessage(
                        ChatMessage(
                            bookId = currentBook.id,
                            isUser = false,
                            message = aiReply,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    _isAiLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "API query failure", e)
                withContext(Dispatchers.Main) {
                    repository.insertChatMessage(
                        ChatMessage(
                            bookId = currentBook.id,
                            isUser = false,
                            message = "❌ Failed to reach the AI model: ${e.localizedMessage}. Please double-check your network connection or try again.",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    _isAiLoading.value = false
                }
            }
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        // Compress for high visual quality but fast network upload size
        compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
