package com.layerdocs.rendering.html.pdf

import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.multipdf.PDFMergerUtility
import java.io.File
import java.io.FileOutputStream

object PdfMerger {
    /**
     * Merges a list of PDF files into a single destination PDF.
     * @param files List of PDF files to merge, in order.
     * @param destination The file to write the merged PDF to.
     */
    fun merge(files: List<File>, destination: File) {
        if (files.isEmpty()) return
        
        val merger = PDFMergerUtility()
        destination.parentFile?.mkdirs()
        
        FileOutputStream(destination).use { outputStream ->
            merger.destinationStream = outputStream
            
            files.forEach { file ->
                merger.addSource(file)
            }
            
            // Perform the merge
            merger.mergeDocuments(null)
        }
    }
}
