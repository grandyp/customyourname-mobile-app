package com.cynid.customyourname_app

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class produk(
    var id_produk:String?=null,
    var nama_produk:String?=null,
    var harga_produk:String?=null,
    var deskripsi_produk:String?=null,
    var berat_produk:String?=null,
    var kategori_produk:String?=null,
    var status_produk:String?=null,
    var foto_produk:ArrayList<String> = arrayListOf(),
    var panjangRantai:String?="70cm",
    var warna_produk:String?="Silver",
    var custom_produk:String?="tidak",
    var desain_produk:desain?=null
) {
}