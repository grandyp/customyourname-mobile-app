package com.cynid.customyourname_app

import android.os.Parcel
import android.os.Parcelable

data class asset(
    var nama:String?="",
    var url1:String?="",
    var url2:String?="",
    var posisiy:String?="",
    var kategori:String?=""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nama)
        parcel.writeString(url1)
        parcel.writeString(url2)
        parcel.writeString(posisiy)
        parcel.writeString(kategori)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<asset> {
        override fun createFromParcel(parcel: Parcel): asset {
            return asset(parcel)
        }

        override fun newArray(size: Int): Array<asset?> {
            return arrayOfNulls(size)
        }
    }
}