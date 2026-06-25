package com.example.data

import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooksFlow()

    suspend fun getBookById(id: Long): Book? = bookDao.getBookById(id)

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    // Bookmarks
    val allBookmarks: Flow<List<Bookmark>> = bookDao.getAllBookmarksFlow()

    fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>> = bookDao.getBookmarksForBookFlow(bookId)

    suspend fun insertBookmark(bookmark: Bookmark): Long = bookDao.insertBookmark(bookmark)

    suspend fun deleteBookmark(bookmark: Bookmark) = bookDao.deleteBookmark(bookmark)

    // Notes
    fun getNotesForBook(bookId: Long): Flow<List<Note>> = bookDao.getNotesForBookFlow(bookId)

    suspend fun getNoteByPage(bookId: Long, pageNumber: Int): Note? = bookDao.getNoteByPage(bookId, pageNumber)

    suspend fun insertNote(note: Note): Long = bookDao.insertNote(note)

    suspend fun deleteNote(note: Note) = bookDao.deleteNote(note)

    // Chat
    fun getChatMessagesForBook(bookId: Long): Flow<List<ChatMessage>> = bookDao.getChatMessagesForBookFlow(bookId)

    suspend fun insertChatMessage(message: ChatMessage): Long = bookDao.insertChatMessage(message)

    suspend fun clearChatHistory(bookId: Long) = bookDao.clearChatHistory(bookId)

    // Annotations
    fun getAnnotationsForBook(bookId: Long): Flow<List<PdfAnnotation>> = bookDao.getAnnotationsForBookFlow(bookId)

    suspend fun insertAnnotation(annotation: PdfAnnotation): Long = bookDao.insertAnnotation(annotation)

    suspend fun deleteAnnotation(annotation: PdfAnnotation) = bookDao.deleteAnnotation(annotation)

    suspend fun clearAnnotationsForPage(bookId: Long, pageNumber: Int) = bookDao.clearAnnotationsForPage(bookId, pageNumber)
}
