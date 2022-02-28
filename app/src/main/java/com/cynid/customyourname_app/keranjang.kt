package com.cynid.customyourname_app

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class keranjang(
    var id_produk:String?=null,
    var nama_produk:String?=null,
    var harga_produk:String?=null,
    var berat_produk:String?=null,
    var foto_produk:String?=null,
    var jumlah_produk:Int?=null,
    var warna_produk:String?="silver",
    var panjang_kalung:String?="0 cm",
    var custom_produk:String?=null,
    var kategori_produk:String?=null,
    var desain: desain?=null,
    var id_desain:String?=null,
    var id_cart:String?=null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(com.cynid.customyourname_app.desain::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id_produk)
        parcel.writeString(nama_produk)
        parcel.writeString(harga_produk)
        parcel.writeString(berat_produk)
        parcel.writeString(foto_produk)
        parcel.writeValue(jumlah_produk)
        parcel.writeString(warna_produk)
        parcel.writeString(panjang_kalung)
        parcel.writeString(custom_produk)
        parcel.writeString(kategori_produk)
        parcel.writeParcelable(desain, flags)
        parcel.writeString(id_desain)
        parcel.writeString(id_cart)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<keranjang> {
        override fun createFromParcel(parcel: Parcel): keranjang {
            return keranjang(parcel)
        }

        override fun newArray(size: Int): Array<keranjang?> {
            return arrayOfNulls(size)
        }
    }
}