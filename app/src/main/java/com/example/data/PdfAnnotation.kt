package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_annotations")
data class PdfAnnotation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val pageNumber: Int,
    val type: String, // "HIGHLIGHT", "DRAWING", "STICKY"
    val colorHex: String, // Hex color code e.g. "#FFFF00", "#FF4081" (or "#55FFFF00" for translucent highlight)
    val pointsData: String? = null, // Path points scaled 0..1 as string: "x1,y1;x2,y2;x3,y3"
    val content: String? = null, // Sticky note written text
    val x: Float = 0f, // Relative horizontal anchor offset for sticky note (0f..1f)
    val y: Float = 0f, // Relative vertical anchor offset for sticky note (0f..1f)
    val thickness: Float = 5f, // Brushes stroke thickness
    val timestamp: Long = System.currentTimeMillis()
)
