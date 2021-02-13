package bill.com.mybills.model

import android.os.Parcel
import android.os.Parcelable

internal data class Item(var particulars: String?, var weight: Double, var goldRate: Double, var amtGold: Double, var makingCharge: Double, var cgst: Double, var sgst: Double, var customerName: String?, var itemUri: String?, var phoneNo: String?, var date: String?, var billNo: String?, var otherItemPrice: Double, var otherItemDesc: String?) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(particulars)
        parcel.writeDouble(weight)
        parcel.writeDouble(goldRate)
        parcel.writeDouble(amtGold)
        parcel.writeDouble(makingCharge)
        parcel.writeDouble(cgst)
        parcel.writeDouble(sgst)
        parcel.writeString(customerName)
        parcel.writeString(itemUri)
        parcel.writeString(phoneNo)
        parcel.writeString(date)
        parcel.writeString(billNo)
        parcel.writeDouble(otherItemPrice)
        parcel.writeString(otherItemDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}