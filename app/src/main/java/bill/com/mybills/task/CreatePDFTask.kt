package bill.com.mybills.task

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.BusinessProfile
import bill.com.mybills.model.Item
import bill.com.mybills.ui.activity.BillPreviewActivity
import bill.com.mybills.ui.fragment.MyProfileFragment
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_editprofile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


internal class CreatePDFTask(context: BillPreviewActivity?, var file: File, var billItemList: ArrayList<Item>, var businessProfile: BusinessProfile, var bitmapLogo: Bitmap, val db: FirebaseFirestore?, val user: FirebaseUser?) : AsyncTask<String, Void, String>() {

    private var cell: PdfPCell? = null
    private var bgImage: Image? = null
    private val primarylight = WebColors.getRGBColor("#2979FF");
    private val myColor1 = WebColors.getRGBColor("#2979FF");
    private var contextRef: WeakReference<Context?> = WeakReference(context)
    private var appDAL: AppDAL? = null
    private var gst: Double = 0.0

    override fun onPreExecute() {
        super.onPreExecute()
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

            val bitmap = bitmapLogo
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
            var ph = selector.process(businessProfile.orgName)
            cell?.addElement(ph)
            ph = selector.process(businessProfile.address)
            cell?.addElement(ph)
            ph = selector.process("Pin: " + businessProfile.pincode)
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process(businessProfile.gstIN)
            cell?.addElement(ph)
            ph = selector.process("Phone: " + businessProfile.phone)
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("___________________________________________________________")
            cell?.addElement(ph)
            ph = selector.process("Customer Name:  ${billItemList[0].customerName}")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("")
            cell?.addElement(ph)
            ph = selector.process("Mobile:  ${billItemList[0].phoneNo}")
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

            val table = PdfPTable(8)
            val columnWidth = floatArrayOf(25f, 25f, 15f, 30f, 30f, 30f, 20f, 25f)
            table.setWidths(columnWidth)
            cell = PdfPCell()
            cell?.backgroundColor = primarylight
            cell?.colspan = 8
            cell?.addElement(pTable)
            table.addCell(cell)
            cell = PdfPCell()
            cell?.colspan = 8
            table.addCell(cell)
            cell?.colspan = 8
            cell?.backgroundColor = myColor1
            ph = selector.process("Ornmt")
            cell = PdfPCell()
            cell?.fixedHeight = 50f
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Others")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
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
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Making Chrg")
            cell = PdfPCell()
            cell?.border = PdfPCell.ALIGN_CENTER
            cell?.addElement(ph)
            cell?.backgroundColor = myColor1
            table.addCell(cell)
            ph = selector.process("Other Chrg")
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
            cell?.colspan = 8

            for (item in billItemList) {
                val fontselector = FontSelector();
                val f1 = FontFactory.getFont("MSung-Light",
                        "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED)
                f1.color = BaseColor.BLACK
                fontselector.addFont(f1)
                cell = PdfPCell()
                cell?.fixedHeight = (300 / billItemList.size).toFloat()
                var ph = fontselector.process(item.particulars)
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(item.otherItemDesc.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(item.weight.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(item.goldRate.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(df.format(item.amtGold))
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(item.makingCharge.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(item.otherItemPrice.toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(df.format(item.amtGold + item.makingCharge + item.otherItemPrice).toString())
                cell?.addElement(ph)
                table.addCell(cell)
                cell = PdfPCell()
                ph = fontselector.process(df.format(item.amtGold + item.makingCharge + item.otherItemPrice).toString())
                cell?.addElement(ph)
                table.addCell(cell)
                //gst = (((item.amtGold + item.makingCharge) * 1.5) / 100)
                totalAmt += (item.amtGold + item.makingCharge + item.otherItemPrice)
            }
            /*val selectorGST = FontSelector();
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
            table.addCell(cell)*/

            val ftable = PdfPTable(7)
            ftable.widthPercentage = 100f
            val columnWidthaa = floatArrayOf(0f, 10f, 5f, 5f, 5f, 25f,40f)
            ftable.setWidths(columnWidthaa)
            cell = PdfPCell()
            cell?.colspan = 7
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
            cell?.colspan = 8
            cell?.fixedHeight = 50f
            ftable.addCell(cell)
            cell = PdfPCell()
            cell?.colspan = 8
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
        appDAL?.billItemJson = String()
        var count = 0
        for (item in billItemList) {
            user?.uid?.let {
                db?.collection(it)?.document(billItemList[0].phoneNo)?.collection("Bill Items")?.document()?.set(item)?.addOnSuccessListener { void: Void? ->
                    count++
                    if (count == billItemList.size) {
                        shareFile()
                    }
                }?.addOnFailureListener { exception: java.lang.Exception ->
                    (contextRef.get() as BillPreviewActivity).dismiss()
                    Toast.makeText(contextRef.get(), "Failure", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun shareFile() {
        val intentShareFile = Intent(Intent.ACTION_SEND);
        val fileWithinMyDir = File(file.absolutePath)
        val filePath: StorageReference?
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val uri = Uri.fromFile(fileWithinMyDir)
        filePath = uri?.lastPathSegment?.let { it -> storageReference.child(user?.uid + "/" + billItemList[0].billNo + "/bills").child(it) }
        filePath?.putFile(uri)?.addOnFailureListener {
            Toast.makeText(contextRef.get(), "Error uploading bill" + it.localizedMessage, Toast.LENGTH_LONG).show()
            (contextRef.get() as BillPreviewActivity).dismiss()
        }?.addOnSuccessListener {
            Toast.makeText(contextRef.get(), "Bill Uploaded Successfully", Toast.LENGTH_LONG).show()
            (contextRef.get() as BillPreviewActivity).dismiss()
            if (fileWithinMyDir.exists()) {
                Toast.makeText(contextRef.get(), "Please share Bill to Customer", Toast.LENGTH_LONG).show()
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
}