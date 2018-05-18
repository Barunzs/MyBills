package bill.com.mybills.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.Rectangle.NO_BORDER
import com.itextpdf.text.pdf.PdfPTable
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.itextpdf.text.pdf.PdfWriter

import android.R.attr.path
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.text.SpannableStringBuilder
import bill.com.mybills.R
import bill.com.mybills.model.Item
import com.itextpdf.text.*
import com.itextpdf.text.html.WebColors
import kotlinx.android.synthetic.main.fragment_bill.*
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


internal class BillFragment : Fragment(), View.OnFocusChangeListener {


    private var path: String? = null
    private lateinit var dir: File
    private lateinit var file: File
    private val TAG = "BillFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //creating new file path
        path = Environment.getExternalStorageDirectory().absolutePath + "/Trinity/PDF Files";
        dir = File(path)
        if (dir.exists()) {
            dir.mkdirs()
        }
        file = File.createTempFile("Bill" + "", ".pdf", context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))
        //LongOperation(context, file).execute()
        initEventsListeners()


    }

    private fun initEventsListeners() {
        share.setOnClickListener { shareFile() }
        //addnext.setOnClickListener { addNewItem() }
        previous.setOnClickListener { viewPrevious() }
        generatebill.setOnClickListener { generateBill(it) }
        makingCharge.onFocusChangeListener = this

    }

    private fun generateBill(view: View) {
        try {
            val item = Item(particular?.text.toString(), weight.text.toString().toDouble(), rateofgold.text.toString().toDouble(), makingCharge.text.toString().toDouble())
            val totalAmt = (item.weight * item.goldRate) + makingCharge.text.toString().toDouble() + sgstrate.text.toString().toDouble() + cgstrate.text.toString().toDouble()
            total.text = SpannableStringBuilder("Rs " + totalAmt.toString())
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Please Enter all Fields", Toast.LENGTH_LONG).show()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus && v?.id == R.id.makingCharge) {
            val amountOfGold = weight.text.toString().toDouble() * rateofgold.text.toString().toDouble()
            amtofgold.text = SpannableStringBuilder(amountOfGold.toString())
        }
    }

    private fun viewPrevious() {
        val fm = activity?.supportFragmentManager
        fm?.popBackStackImmediate()
    }


    /*private fun addNewItem() {
        val ft = fragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        val fragment = BillFragment()
        ft?.add(R.id.frameLayout, fragment)
        ft?.addToBackStack(null)
        ft?.commit()
    }*/


    private fun shareFile() {
        val intentShareFile = Intent(Intent.ACTION_SEND);
        val fileWithinMyDir = File(file.absolutePath)
        if (fileWithinMyDir.exists()) {
            intentShareFile.type = "application/pdf";
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.absolutePath));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private class LongOperation(context: Context?, file: File) : AsyncTask<String, Void, String>() {
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
                cell?.setBorder(Rectangle.NO_BORDER)
                pt.addCell(cell)

                val pTable = PdfPTable(1)
                pTable.widthPercentage = 100f
                cell = PdfPCell()
                cell?.setColspan(1)
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
                cell?.setBorder(Rectangle.NO_BORDER)
                cell?.setBackgroundColor(myColor1)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell?.setBorder(Rectangle.NO_BORDER)
                cell?.setBackgroundColor(myColor1)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell?.setBorder(Rectangle.NO_BORDER)
                cell?.setBackgroundColor(myColor1)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell?.setBorder(Rectangle.NO_BORDER)
                cell?.setBackgroundColor(myColor1)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell?.setBorder(Rectangle.NO_BORDER)
                cell?.setBackgroundColor(myColor1)
                ftable.addCell(cell)
                cell = PdfPCell(Paragraph("Footer"))
                cell?.setColspan(6)
                ftable.addCell(cell)
                cell = PdfPCell()
                cell?.setColspan(6)
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
        }

    }
}