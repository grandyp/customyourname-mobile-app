package com.cynid.customyourname_app

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User(
    var nama: String?=null,
    var email:String?=null,
    var nomorhp:String?=null,
    var alamat:String?=null,
    var provinsi:String?=null,
    var kota:String?=null,
    var id_kota:String?=null,
    var kecamatan:String?=null,
    var kelurahan:String?=null,
    var id_user:String?=null
):Parcelable {
    constructor(parcel: Parcel) : this(
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

    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "nama" to nama,
            "email" to email,
            "nomorhp" to nomorhp,
            "alamat" to alamat,
            "provinsi" to provinsi,
            "kota" to kota,
            "id_kota" to id_kota,
            "kecamatan" to kecamatan,
            "kelurahan" to kelurahan
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nama)
        parcel.writeString(email)
        parcel.writeString(nomorhp)
        parcel.writeString(alamat)
        parcel.writeString(provinsi)
        parcel.writeString(kota)
        parcel.writeString(id_kota)
        parcel.writeString(kecamatan)
        parcel.writeString(kelurahan)
        parcel.writeString(id_user)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}