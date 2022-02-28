package com.cynid.customyourname_app

import android.os.Parcel
import android.os.Parcelable

data class desain(
    var jenis: String? = null,
    var list_clipart:ArrayList<String>? = null,
    var tulisan:String?=null,
    var font:String?=null,
    var nama_desain:String?=null,
    var url_gambar:String?=null,
    var id_desain:String?=null,
    var list_url_clipart:ArrayList<String>?=null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jenis)
        parcel.writeStringList(list_clipart)
        parcel.writeString(tulisan)
        parcel.writeString(font)
        parcel.writeString(nama_desain)
        parcel.writeString(url_gambar)
        parcel.writeString(id_desain)
        parcel.writeStringList(list_url_clipart)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<desain> {
        override fun createFromParcel(parcel: Parcel): desain {
            return desain(parcel)
        }

        override fun newArray(size: Int): Array<desain?> {
            return arrayOfNulls(size)
        }
    }
}