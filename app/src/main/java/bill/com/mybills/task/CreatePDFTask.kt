package bill.com.mybills.task

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.view.View
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import com.airbnb.lottie.LottieAnimationView
import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


internal class CreatePDFTask(context: Context?, var file: File, var billItemList: ArrayList<Item>, var progress: LottieAnimationView) : AsyncTask<String, Void, String>() {

    private var cell: PdfPCell? = null
    private var bgImage: Image? = null
    private val primarylight = WebColors.getRGBColor("#E07777");
    private val myColor1 = WebColors.getRGBColor("#E43F3F");
    private var contextRef: WeakReference<Context?> = WeakReference(context)
    private var appDAL: AppDAL? = null
    private var gst: Double = 0.0

    override fun onPreExecute() {
        super.onPreExecute()
        progress.visibility = View.VISIBLE
        appDAL = contextRef.get()?.let { AppDAL(it) }
    }

    override fun doInBackground(vararg params: String?): String {
        val doc = Document()
        var totalAmt = 0.0
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        try {
            val fOut = FileOutputStream(file)
            val writer = PdfWriter.getInstance(doc, fOut)
            //open the document
            doc.open()
            //create table
            val pt = PdfPTable(3)
            pt.widthPercentage = 100f
            val fl = floatArrayOf(30f, 50f, 20f)
            pt.setWidths(fl)
            cell = PdfPCell()
            cell?.border = Rectangle.NO_BORDER

            //set drawable in cell

            val myImage = contextRef.get()?.let { ContextCompat.getDrawable(it, R.drawable.anuska_jweller) };
            val bitmap = (myImage as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bitmapdata = stream.toByteArray()

            bgImage = Image.getInstance(bitmapdata)
            bgImage?.setAbsolutePosition(330f, 650f)
            cell?.addElement(bgImage)
            pt.addCell(cell)

            cell = PdfPCell()
            cell?.border = Rectangle.NO_BORDER
            val selector = FontSelector();
            val f1 = FontFactory.getFont("MSung-Light",
                    "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED)
            f1.color = BaseColor.WHITE
            selector.addFont(f1)
            var ph = selector.process("ANUSHKA JEWELLERS")
            cell?.addElement(ph)
            ph = selector.process("BALAJI COMPLEX CHAMPASARI MORE")
            cell?.addElement(ph)
            ph = selector.process("SILIGURI-734003")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("GSTIN: 19AFLPP9130J1ZW")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("Customer Name ${billItemList[0].customerName}")
            cell?.addElement(ph)
            pt.addCell(cell)

            val billdate = SimpleDateFormat("dd/MM/yyyy")
            cell = PdfPCell()
            ph = selector.process(billdate.format(Calendar.getInstance().time))
            cell?.addElement(ph)
            cell?.border = Rectangle.NO_BORDER
            pt.addCell(cell)

            val pTable = PdfPTable(1)
            pTable.widthPercentage = 100f
            cell = PdfPCell()
            cell?.colspan = 1
            cell?.addElement(pt)
            pTable.addCell(cell)

            val table = PdfPTable(6)
            val columnWidth = floatArrayOf(25f, 10f, 30f, 30f, 35f, 20f)
            table.setWidths(columnWidth)
            cell = PdfPCell()
            cell?.backgroundColor = primarylight
            cell?.colspan = 6
            cell?.addElement(pTable)
            table.addCell(cell)
            cell = PdfPCell()
            cell?.colspan = 6
            table.addCell(cell)
            cell?.colspan = 6
            cell?.backgroundColor = myColor1
            ph = selector.process("Ornament")
            cell = PdfPCell()
            cell?.fixedHeight = 50f
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            ph = selector.process("Wt")
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Rate of Gold")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Amt of Gold")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            cell?.addElement(ph)
            cell?.setBackgroundColor(myColor1)
            table.addCell(cell)
            ph = selector.process("Making Charge")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Amount")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)

            //table.setHeaderRows(3);
            cell = PdfPCell()
            cell?.colspan = 6

            for (item in billItemList) {
                val selector = FontSelector();
                val f1 = FontFactory.getFont("MSung-Light",
                        "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED)
                f1.color = BaseColor.BLACK
                selector.addFont(f1)
                cell = PdfPCell()
                cell?.fixedHeight = (300 / billItemList.size).toFloat()
                var ph = selector.process(item.particulars)
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = selector.process(item.weight.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = selector.process(item.goldRate.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = selector.process(df.format(item.amtGold))
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = selector.process(item.makingCharge.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = selector.process(df.format(item.amtGold + item.makingCharge).toString())
                cell?.addElement(ph)
                table.addCell(cell)
                gst = (((item.amtGold + item.makingCharge) * 1.5) / 100)
                totalAmt += (2 * gst + item.amtGold + item.makingCharge)
            }
            val selectorGST = FontSelector();
            val fGST = FontFactory.getFont("MSung-Light",
                    "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED)
            fGST.color = BaseColor.BLACK
            selectorGST.addFont(fGST)
            cell = PdfPCell()
            var phGST = selectorGST.process("SGST 1.5%")
            cell?.colspan = 5
            cell?.addElement(phGST)
            table.addCell(cell)
            cell = PdfPCell()
            phGST = selectorGST.process(df.format(gst))
            cell?.addElement(phGST)
            table.addCell(cell)
            cell = PdfPCell()
            phGST = selectorGST.process("CGST 1.5%")
            cell?.colspan = 5
            cell?.addElement(phGST)
            table.addCell(cell)
            cell = PdfPCell()
            phGST = selectorGST.process(df.format(gst))
            cell?.addElement(phGST)
            table.addCell(cell)

            val ftable = PdfPTable(6)
            ftable.widthPercentage = 100f
            val columnWidthaa = floatArrayOf(30f, 5f, 5f, 5f, 35f, 20f)
            ftable.setWidths(columnWidthaa)
            cell = PdfPCell()
            cell?.colspan = 6
            cell?.backgroundColor = myColor1
            ph = selector.process("Total Amount")
            cell = PdfPCell()
            cell?.addElement(ph)
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            cell?.fixedHeight = 50f
            ftable.addCell(cell)
            cell = PdfPCell(Phrase(""))
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            ftable.addCell(cell)
            cell = PdfPCell(Phrase(""))
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            ftable.addCell(cell)
            cell = PdfPCell(Phrase(""))
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            ftable.addCell(cell)
            cell = PdfPCell(Phrase(""))
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            ftable.addCell(cell)
            ph = selector.process("Rs." + df.format(totalAmt).toString())
            cell = PdfPCell()
            cell?.addElement(ph)
            table.addCell(cell)
            cell?.border = Rectangle.NO_BORDER
            cell?.backgroundColor = myColor1
            ftable.addCell(cell)
            cell = PdfPCell(Paragraph("This is a computer generated Bill"))
            cell?.colspan = 6
            cell?.fixedHeight = 50f
            ftable.addCell(cell)
            cell = PdfPCell()
            cell?.colspan = 6
            cell?.addElement(ftable)
            table.addCell(cell)
            doc.add(table)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            // close the document
            doc.close();
        }
        return totalAmt.toString()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        progress.visibility = View.GONE
        appDAL?.billItemJson = String()
        shareFile()
    }

    private fun shareFile() {
        val intentShareFile = Intent(Intent.ACTION_SEND);
        val fileWithinMyDir = File(file.absolutePath)
        if (fileWithinMyDir.exists()) {
            intentShareFile.type = "application/pdf";
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.absolutePath));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            intentShareFile.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            contextRef.get()?.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

}