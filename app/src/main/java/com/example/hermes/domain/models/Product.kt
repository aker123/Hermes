package com.example.hermes.domain.models

class Product(
    var uid: String,
    val shopUid: String,
    val name: String,
    val price: Long,
    var amount: Long,
    var quantity: Long,
    val description: String,
    val gender: String,
    val category: Category,
    val imagePath: String,
    var sizes: List<Size>,
    val productUid: String
)
{
   fun getSelectedSize(): Size{
      return sizes.first { it.selected }
   }

   enum class Category(var key: String) {
      NONE(""),
      SCHUHE("Обувь"),
      SHORTS("Шорты"),
      TSHIRTS("Футболки"),
      ROCKS("Юбки"),
      HEMDEN("Рубашки"),
      HOSE("Брюки"),
      JEANS("Джинсы"),
      JACKEN("Кофты"),
      UNTERHEMDEN("Майки"),
      KLEIDERS("Платья"),
      SUITS("Костюмы")
   }
}