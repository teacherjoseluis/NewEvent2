package com.bridesandgrooms.event.UI.Activities

import Application.AnalyticsManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bridesandgrooms.event.Functions.convertToFilenameString
import com.bridesandgrooms.event.Functions.converttoString
import com.bridesandgrooms.event.Functions.currentDateTime
import com.bridesandgrooms.event.MVP.ExportPDFPresenter
import com.bridesandgrooms.event.Model.Category.Companion.getCategoryName
import com.bridesandgrooms.event.Model.EventDBHelper
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.Task.CREATOR.getTaskStatusName
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.TaskPDFBudgetReport
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.databinding.ExportpdfBinding
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory.HELVETICA
import com.itextpdf.text.FontFactory.HELVETICA_BOLD
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.DottedLineSeparator
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.DateFormat


class ExportPDF : AppCompatActivity(), ExportPDFPresenter.EPDFTasks {

    private lateinit var exportPDFP: ExportPDFPresenter
    private lateinit var binding: ExportpdfBinding
    lateinit var context: Context
    private var arrayTask = ArrayList<Task>()
    val testTask1 = Task(
        "Task123",
        "Task report",
        "12/01/2024",
        "flowers",
        "$1,000",
        "active",
        "123",
        "26/01/2024"
    )
    val testTask2 = Task(
        "Task456",
        "Task report 2",
        "12/01/2024",
        "guests",
        "$2,000",
        "active",
        "123",
        "27/01/2024"
    )
    val testTask3 = Task(
        "Task789",
        "Task report 3",
        "12/01/2024",
        "food",
        "$3,000",
        "completed",
        "123",
        "28/01/2024"
    )

    val testTask4 = Task(
        "Task123",
        "Task report 4",
        "12/01/2024",
        "food",
        "$1,000",
        "active",
        "123",
        "26/01/2024"
    )
    val testTask5 = Task(
        "Task456",
        "Task report 5",
        "12/01/2024",
        "guests",
        "$2,000",
        "active",
        "123",
        "27/01/2024"
    )
    val testTask6 = Task(
        "Task789",
        "Task report 6",
        "12/01/2024",
        "food",
        "$3,000",
        "completed",
        "123",
        "28/01/2024"
    )

    val testTask7 = Task(
        "Task123",
        "Task report 7",
        "12/01/2024",
        "food",
        "$1,000",
        "active",
        "123",
        "26/01/2024"
    )

    val testTask8 = Task(
        "Task456",
        "Task report 8",
        "12/01/2024",
        "guests",
        "$2,000",
        "active",
        "123",
        "27/01/2024"
    )

    val testTask9 = Task(
        "Task789",
        "Task report 9",
        "12/01/2024",
        "food",
        "$3,000",
        "completed",
        "123",
        "28/01/2024"
    )

    val testTask10 = Task(
        "Task123",
        "Task report 10",
        "12/01/2024",
        "food",
        "$1,000",
        "active",
        "123",
        "26/01/2024"
    )

    val testTask11 = Task(
        "Task456",
        "Task report 11",
        "12/01/2024",
        "guests",
        "$2,000",
        "active",
        "123",
        "27/01/2024"
    )

    val testTask12 = Task(
        "Task789",
        "Task report 12",
        "12/01/2024",
        "food",
        "$3,000",
        "completed",
        "123",
        "28/01/2024"
    )

    init {
        arrayTask.add(testTask1)
        arrayTask.add(testTask2)
        arrayTask.add(testTask3)
        arrayTask.add(testTask4)
        arrayTask.add(testTask5)
        arrayTask.add(testTask6)
        arrayTask.add(testTask7)
        arrayTask.add(testTask8)
        arrayTask.add(testTask9)
        arrayTask.add(testTask10)
        arrayTask.add(testTask11)
        arrayTask.add(testTask12)
        //val taskPDFReport = TaskPDFReport(0, 10F, 10F, arrayTask)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.exportpdf)
        binding = DataBindingUtil.setContentView(this, R.layout.exportpdf)
        binding.action1.paint.isUnderlineText = true
        binding.action1.setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "action1", "click")
            finish()
        }
        context = applicationContext
        try {
            exportPDFP = ExportPDFPresenter(context, this, "")
            exportPDFP.getTasksList()
            //createPdf(taskPDFReport = TaskPDFReport(15, 10F, 10F, arrayTask))
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun createPdf(
        taskPDFBudgetReport: TaskPDFBudgetReport,
        list: ArrayList<Task>,
        pageNum: Int
    ) {
        //val rosachillon = BaseColor(225, 58, 133)
        val primaryCream = BaseColor(124, 150, 127)
        val tableBackground = BaseColor(252, 252, 252)

        val eventDB = EventDBHelper()
        val event = eventDB.getEvent()!!

//        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            .toString()
//        val file = File(pdfPath, "TaskReport_${convertToFilenameString(currentDateTime)}.pdf")
//        binding.path.text = "${pdfPath}TaskReport_${convertToFilenameString(currentDateTime)}.pdf"
//
//        val outputStream = FileOutputStream(file)

        val fileName = "TaskReport_${convertToFilenameString(currentDateTime)}.pdf"
        binding.path.text = fileName

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Files.getContentUri("external")
            }

        val itemUri = resolver.insert(collection, contentValues)!!
        val outputStream = resolver.openOutputStream(itemUri)!!

        val document = Document()
        PdfWriter.getInstance(document, outputStream)
        document.open()
        //-----------------------------

        val tableHeader = PdfPTable(2)
        tableHeader.setTotalWidth(floatArrayOf(94f, 373f))
        tableHeader.isLockedWidth = true

        val logoCell = PdfPCell()
        logoCell.borderColor = BaseColor.WHITE

        val logoDrawable = getDrawable(R.drawable.welcomelogo_small)!!
        val bitmap = Bitmap.createBitmap(
            logoDrawable.intrinsicWidth,
            logoDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        logoDrawable.setBounds(0, 0, canvas.width, canvas.height)
        logoDrawable.draw(canvas)

        val logoStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream)
        val logoBytes = logoStream.toByteArray()
        val logoImage = Image.getInstance(logoBytes)

        logoCell.colspan = 1
        logoCell.rowspan = 2
        logoCell.image = logoImage
        //logoCell.phrase = phrase

        tableHeader.addCell(logoCell)
        //----------------------------------
        val pinkFont = Font()
        pinkFont.setFamily(HELVETICA)

        pinkFont.size = 12f
        pinkFont.color = primaryCream
        //----------------------------------
        val title1Header = PdfPCell()
        title1Header.borderColor = BaseColor.WHITE
        title1Header.horizontalAlignment = Element.ALIGN_RIGHT

        val title2Header = PdfPCell()
        title2Header.borderColor = BaseColor.WHITE
        title2Header.horizontalAlignment = Element.ALIGN_RIGHT

        title1Header.colspan = 1
        title1Header.rowspan = 1
        title1Header.phrase =
            Phrase("${event.name} – ${event.date} – ${event.time}", pinkFont)

        title2Header.colspan = 1
        title2Header.rowspan = 1
        title2Header.phrase = Phrase(
            "${context.getString(R.string.report_listoftasks)}: ${
                converttoString(
                    currentDateTime,
                    DateFormat.MEDIUM
                )
            }", pinkFont
        )

        tableHeader.addCell(title1Header)
        tableHeader.addCell(title2Header)
        document.add(tableHeader)
        document.add(Paragraph(" "))
        val linebreak = Chunk(DottedLineSeparator())
        document.add(linebreak)
        document.add(Paragraph(" "))

        val whiteFont = Font()
        whiteFont.setFamily(HELVETICA_BOLD)

        whiteFont.size = 12f
        whiteFont.color = BaseColor.WHITE

        var tableBody = PdfPTable(5)
        tableBody.setTotalWidth(floatArrayOf(100f, 100f, 100f, 100f, 100f))
        tableBody.isLockedWidth = true
        tableBody.defaultCell.horizontalAlignment = Element.ALIGN_CENTER;
        tableBody.defaultCell.backgroundColor = primaryCream
        tableBody.addCell(Paragraph(context.getString(R.string.name), whiteFont))
        tableBody.addCell(Phrase(context.getString(R.string.category), whiteFont))
        tableBody.addCell(Phrase(context.getString(R.string.deadline), whiteFont))
        tableBody.addCell(Phrase(context.getString(R.string.budget), whiteFont))
        tableBody.addCell(Phrase(context.getString(R.string.status), whiteFont))

        var taskCount = 0
        for (task in list) {
            tableBody.defaultCell.horizontalAlignment = Element.ALIGN_LEFT;
            tableBody.defaultCell.backgroundColor = tableBackground
            tableBody.addCell(Phrase(task.name))
            tableBody.addCell(Phrase(getCategoryName(task.category)))
            tableBody.addCell(Phrase(task.date))
            tableBody.addCell(Phrase(task.budget))
            tableBody.addCell(Phrase(getTaskStatusName(task.status)))
            taskCount++

            if (taskCount >= pageNum) {
                document.add(tableBody)
                document.add(Paragraph(" "))
                val linebreak = Chunk(DottedLineSeparator())
                document.add(linebreak)
                document.add(Paragraph(" "))
                document.add(Chunk.NEXTPAGE)
                document.add(Paragraph(" "))
                taskCount = 0 // Reset the task counter
                tableBody = PdfPTable(5)
                tableBody.setTotalWidth(floatArrayOf(100f, 100f, 100f, 100f, 100f))
                tableBody.isLockedWidth = true
                tableBody.defaultCell.horizontalAlignment = Element.ALIGN_CENTER;
                tableBody.defaultCell.backgroundColor = primaryCream
                tableBody.addCell(Paragraph(context.getString(R.string.name), whiteFont))
                tableBody.addCell(Phrase(context.getString(R.string.category), whiteFont))
                tableBody.addCell(Phrase(context.getString(R.string.deadline), whiteFont))
                tableBody.addCell(Phrase(context.getString(R.string.budget), whiteFont))
                tableBody.addCell(Phrase(context.getString(R.string.status), whiteFont))
            }
        }
        document.add(tableBody)

        //-----------------------------
        document.add(Paragraph(" "))
        val linebreak2 = Chunk(DottedLineSeparator())
        document.add(linebreak2)
        document.add(Paragraph(" "))

        val tableFooter = PdfPTable(1)
        tableFooter.defaultCell.borderColor = BaseColor.WHITE
        tableFooter.setTotalWidth(floatArrayOf(467f))
        tableFooter.isLockedWidth = true
        tableFooter.defaultCell.horizontalAlignment = Element.ALIGN_RIGHT;
        tableFooter.addCell(
            Phrase(
                "${context.getString(R.string.taskbudget_completed)}: ${taskPDFBudgetReport.budgetTasksCompleted}",
                pinkFont
            )
        )
        tableFooter.addCell(
            Phrase(
                "${context.getString(R.string.taskbudget_notcompleted)}: ${taskPDFBudgetReport.budgetTasksActive}",
                pinkFont
            )
        )

//        tableFooter.defaultCell.horizontalAlignment = Element.ALIGN_LEFT;
//        tableFooter.addCell(Phrase("Página 1"))

        document.add(tableFooter)
        document.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(itemUri, contentValues, null, null)
        }
    }

    override fun onEPDFTasks(list: ArrayList<Task>) {
        //Need to get event data. Is it coming from the session? A as in active needs to be translated by the function
        //At least the event should be coming and have a function that goes to local data if there is to retrieve the rest of info
        val taskdb = TaskDBHelper()
        // The below function gets the statistics for tasks and budgets associated to each category
        val taskPDFBudgetReport = taskdb.getTaskPDFBudgetReport()!!
        createPdf(taskPDFBudgetReport, list, 15)
    }

    override fun onEPDFTasksError(errcode: String) {
        TODO("Not yet implemented")
    }

    companion object {
        const val SCREEN_NAME="exportpdf.xml"
        const val TAG = "ExportPDF"
    }
}