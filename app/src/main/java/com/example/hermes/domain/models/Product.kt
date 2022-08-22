package com.example.hermes.domain.models

class Product(
   val uid: String,
   val shopUid: String,
   val name: String,
   val price: Long,
   var amount: Long,
   var quantity: Long
)