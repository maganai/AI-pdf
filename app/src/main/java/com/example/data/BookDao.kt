package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY lastReadTime DESC")
    fun getAllBooksFlow(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarksFlow(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getBookmarksForBookFlow(bookId: Long): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    // Notes
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY pageNumber ASC, timestamp DESC")
    fun getNotesForBookFlow(bookId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE bookId = :bookId AND pageNumber = :pageNumber")
    suspend fun getNoteByPage(bookId: Long, pageNumber: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Delete
    suspend fun deleteNote(note: Note)

    // Chat messages
    @Query("SELECT * FROM chat_messages WHERE bookId = :bookId ORDER BY timestamp ASC")
    fun getChatMessagesForBookFlow(bookId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE bookId = :bookId")
    suspend fun clearChatHistory(bookId: Long)

    // Annotations
    @Query("SELECT * FROM pdf_annotations WHERE bookId = :bookId ORDER BY pageNumber ASC, timestamp ASC")
    fun getAnnotationsForBookFlow(bookId: Long): Flow<List<PdfAnnotation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: PdfAnnotation): Long

    @Delete
    suspend fun deleteAnnotation(annotation: PdfAnnotation)

    @Query("DELETE FROM pdf_annotations WHERE bookId = :bookId AND pageNumber = :pageNumber")
    suspend fun clearAnnotationsForPage(bookId: Long, pageNumber: Int)
}
