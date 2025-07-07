import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.utils.FormatUtils
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

fun generateSharedExpensesPdf(
    context: Context,
    expensesWithDetails: List<EntryRecordAndSharedExpenseDetails>,
    balance: Double
): File? {
    val pageWidth = 595
    val pageHeight = 842
    val marginStart = 40f
    val marginEnd = 550f
    val lineHeight = 20
    val maxY = 800

    val columnDate = marginStart
    val columnDescription = marginStart + 70f
    val columnAmount = marginStart + 140f
    val columnMyShare = marginStart + 210f
    val columnTheirShare = marginStart + 280f
    val columnPaidBy = marginStart + 350f

    val pdfDocument = PdfDocument()
    val paint = Paint()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var pageNumber = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var pdfPage = pdfDocument.startPage(pageInfo)
    var canvas = pdfPage.canvas
    var y = 50

    fun drawHeader() {
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("Gastos Compartidos", marginStart, y.toFloat(), paint)
        y += 40
        paint.textSize = 11f  // Reducido para que quepan 6 columnas
        paint.isFakeBoldText = true
        canvas.drawText("Fecha", columnDate, y.toFloat(), paint)
        canvas.drawText("Descripción", columnDescription, y.toFloat(), paint)
        canvas.drawText("Monto", columnAmount, y.toFloat(), paint)
        canvas.drawText("Parte Eze", columnMyShare, y.toFloat(), paint)
        canvas.drawText("Parte Pau", columnTheirShare, y.toFloat(), paint)
        canvas.drawText("Pagó", columnPaidBy, y.toFloat(), paint)
        y += 20
        paint.isFakeBoldText = false
        canvas.drawLine(marginStart, y.toFloat(), marginEnd, y.toFloat(), paint)
        y += 15
    }

    drawHeader()

    expensesWithDetails.forEach { expenseWithDetails ->
        if (y > maxY) {
            pdfDocument.finishPage(pdfPage)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            pdfPage = pdfDocument.startPage(pageInfo)
            canvas = pdfPage.canvas
            y = 50
            drawHeader()
        }
        
        val expense = expenseWithDetails.entryRecord
        val sharedDetails = expenseWithDetails.sharedExpenseDetails
        
        val date = expense.date.format(dateFormatter)
        val desc = expense.description.take(15)
        val amount = FormatUtils.formatAsCurrency(expense.amount)

        val myShare = sharedDetails.find { it.userId == MY_USER_ID }?.split ?: 0.0
        val theirShare = sharedDetails.find { it.userId != MY_USER_ID }?.split ?: 0.0
        
        // Determinar quién pagó
        val paidBy = when (expense.paidBy) {
            MY_USER_ID -> "Eze"
            else -> "Pau"
        }
        
        paint.textSize = 11f  // Reducido para que quepan 6 columnas
        paint.isFakeBoldText = false
        canvas.drawText(date, columnDate, y.toFloat(), paint)
        canvas.drawText(desc, columnDescription, y.toFloat(), paint)
        canvas.drawText(amount, columnAmount, y.toFloat(), paint)
        canvas.drawText(FormatUtils.formatAsCurrency(myShare), columnMyShare, y.toFloat(), paint)
        canvas.drawText(FormatUtils.formatAsCurrency(theirShare), columnTheirShare, y.toFloat(), paint)
        canvas.drawText(paidBy, columnPaidBy, y.toFloat(), paint)
        y += lineHeight
    }

    y += 30
    paint.textSize = 18f
    paint.isFakeBoldText = true
    canvas.drawText("Saldo restante: ${FormatUtils.formatAsCurrency(balance)}", marginStart, y.toFloat(), paint)

    pdfDocument.finishPage(pdfPage)

    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "gastos_compartidos.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        pdfDocument.close()
    }
    return file
}

fun openPdfFile(context: Context, file: File) {
    try {
        // Verificar que el archivo existe
        if (!file.exists()) {
            android.util.Log.e("PdfUtils", "El archivo PDF no existe: ${file.absolutePath}")
            return
        }
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        // Verificar si hay apps que puedan manejar PDFs
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        if (resolveInfo != null) {
            context.startActivity(intent)
            android.util.Log.d("PdfUtils", "PDF abierto exitosamente")
        } else {
            android.util.Log.e("PdfUtils", "No hay apps instaladas que puedan abrir PDFs")
            // Opcional: mostrar un Toast o Snackbar al usuario
        }
    } catch (e: Exception) {
        android.util.Log.e("PdfUtils", "Error al abrir PDF: ${e.message}")
        e.printStackTrace()
    }
}

fun sharePdfFile(context: Context, file: File) {
    try {
        // Verificar que el archivo existe
        if (!file.exists()) {
            android.util.Log.e("PdfUtils", "El archivo PDF no existe: ${file.absolutePath}")
            return
        }
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Gastos Compartidos")
            putExtra(Intent.EXTRA_TEXT, "Adjunto el reporte de gastos compartidos.")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
        android.util.Log.d("PdfUtils", "PDF compartido exitosamente")
    } catch (e: Exception) {
        android.util.Log.e("PdfUtils", "Error al compartir PDF: ${e.message}")
        e.printStackTrace()
    }
} 