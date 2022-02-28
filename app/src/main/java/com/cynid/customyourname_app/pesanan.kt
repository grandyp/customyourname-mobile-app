package com.cynid.customyourname_app

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class pesanan(
    var id_pesanan:String?=null,
    var daftar_barang:ArrayList<keranjang>?=null,
    var pembeli:User?=null,
    var status_pesanan:String?=null,
    var total_harga:String?=null,
    var total_ongkir:String?=null,
    var kurir:String?=null,
    var tgl_pemesanan:String?=null,
    var no_resi:String?="belum ada",
    var catatan:String?="-",
    var catatan_pengrajin:String?="tidak ada catatan",
    var acc_admin:String?="tidak",
    var metode_pembayaran:String?=null,
    var url_bukti:String?="-"
):Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(keranjang.CREATOR),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id_pesanan)
        parcel.writeTypedList(daftar_barang)
        parcel.writeParcelable(pembeli, flags)
        parcel.writeString(status_pesanan)
        parcel.writeString(total_harga)
        parcel.writeString(total_ongkir)
        parcel.writeString(kurir)
        parcel.writeString(tgl_pemesanan)
        parcel.writeString(no_resi)
        parcel.writeString(catatan)
        parcel.writeString(catatan_pengrajin)
        parcel.writeString(acc_admin)
        parcel.writeString(metode_pembayaran)
        parcel.writeString(url_bukti)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<pesanan> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): pesanan {
            return pesanan(parcel)
        }

        override fun newArray(size: Int): Array<pesanan?> {
            return arrayOfNulls(size)
        }
    }
}