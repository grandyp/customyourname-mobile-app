package com.cynid.customyourname_app

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class message (
    var pengirim:String?=null,
    var id_pengirim:String?=null,
    var pesan:String?=null,
    var tanggal:String?=null,
    var jam:String?=null,
    var status:String?=null,
    var id_chat:String?=null
)
