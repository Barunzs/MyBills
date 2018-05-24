package bill.com.mybills.task

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.widget.Toast
import bill.com.mybills.R
import com.itextpdf.text.*
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

internal class LongOperation(context: Context?, file: File) : AsyncTask<String, Void, String>()  {
	private var cell: PdfPCell? = null
	private var bgImage: Image? = null
	private val myColor = WebColors.getRGBColor("#9E9E9E");
	private val myColor1 = WebColors.getRGBColor("#757575");
	private var contextRef: WeakReference<Context?> = WeakReference(context)
	private val file = file

	override fun onPreExecute() {
		super.onPreExecute()
	}

	override fun doInBackground(vararg params: String?): String {
		val doc = Document()
		//val file = File(dir, "newFile.pdf")
		val sdf = SimpleDateFormat("ddMMyyyy")
		/*sdf.format(Calendar.getInstance().getTime())*/


		try {
			val fOut = FileOutputStream(file)
			val writer = PdfWriter.getInstance(doc, fOut)
			//open the document
			doc.open()
			//create table
			val pt = PdfPTable(3)
			pt.widthPercentage = 100f
			val fl = floatArrayOf(40f, 40f, 35f)
			pt.setWidths(fl)
			cell = PdfPCell()
			cell?.border = Rectangle.NO_BORDER

			//set drawable in cell

			val myImage = contextRef.get()?.let { ContextCompat.getDrawable(it, R.drawable.logo) };
			val bitmap = (myImage as BitmapDrawable).bitmap
			val stream = ByteArrayOutputStream()
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
			val bitmapdata = stream.toByteArray()

			bgImage = Image.getInstance(bitmapdata)
			bgImage?.setAbsolutePosition(330f, 642f)
			cell?.addElement(bgImage)
			pt.addCell(cell)

			cell = PdfPCell()
			cell?.border = Rectangle.NO_BORDER
			cell?.addElement(Paragraph("ANUSHKA JEWELLERS"))
			cell?.addElement(Paragraph("BALAJI COMPLEX CHAMPASARI MORE"))
			cell?.addElement(Paragraph("SILIGURI"))
			pt.addCell(cell)

			val billdate = SimpleDateFormat("dd/MM/yyyy")
			cell = PdfPCell()
			cell?.addElement(Paragraph("DATE -" + billdate.format(Calendar.getInstance().getTime())))
			cell?.border = Rectangle.NO_BORDER
			pt.addCell(cell)

			val pTable = PdfPTable(1)
			pTable.widthPercentage = 100f
			cell = PdfPCell()
			cell?.colspan = 1
			cell?.addElement(pt)
			pTable.addCell(cell)

			val table = PdfPTable(6)
			val columnWidth = floatArrayOf(40f, 20f, 30f, 20f, 20f, 20f)
			table.setWidths(columnWidth)
			cell = PdfPCell()
			cell?.backgroundColor = myColor
			cell?.setColspan(6)
			cell?.addElement(pTable)
			table.addCell(cell)
			cell = PdfPCell()
			cell?.setColspan(6)
			table.addCell(cell)
			cell?.setColspan(6)
			cell?.setBackgroundColor(myColor1)
			cell = PdfPCell(Phrase("Particulars of Ornament"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)
			cell = PdfPCell(Phrase("Weight"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)
			cell = PdfPCell(Phrase("Rate of Gold"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)
			cell = PdfPCell(Phrase("Amount of Gold"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)
			cell = PdfPCell(Phrase("Making Charge"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)
			cell = PdfPCell(Phrase("Amount"))
			cell?.setBackgroundColor(myColor1)
			table.addCell(cell)

			//table.setHeaderRows(3);
			cell = PdfPCell()
			cell?.setColspan(6)

			for (i in 1..10) {
				table.addCell(i.toString())
				table.addCell("Header 1 row $i")
				table.addCell("Header 2 row $i")
				table.addCell("Header 3 row $i")
				table.addCell("Header 4 row $i")
				table.addCell("Header 5 row $i")
			}

			val ftable = PdfPTable(6)
			ftable.widthPercentage = 100f
			val columnWidthaa = floatArrayOf(30f, 10f, 30f, 10f, 30f, 10f)
			ftable.setWidths(columnWidthaa)
			cell = PdfPCell()
			cell?.setColspan(6)
			cell?.setBackgroundColor(myColor1)
			cell = PdfPCell(Phrase("Total Nunber"))
			cell?.setBorder(Rectangle.NO_BORDER)
			cell?.setBackgroundColor(myColor1)
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
			cell = PdfPCell(Phrase(""))
			cell?.border = Rectangle.NO_BORDER
			cell?.backgroundColor = myColor1
			ftable.addCell(cell)
			cell = PdfPCell(Paragraph("Footer"))
			cell?.colspan = 6
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
		return ""
	}

	override fun onPostExecute(result: String) {
		super.onPostExecute(result)
		Toast.makeText(contextRef.get(), "created PDF", Toast.LENGTH_LONG).show()
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