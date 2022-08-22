package com.example.hermes.domain.data.network.authorization.models

import com.example.hermes.domain.models.Shop

class IOperator(
    val uid: String,
    val login: String,
    val password: String,
    val surname: String,
    val name: String,
    val phoneNumber: String,
    val mail: String,
    val shopUid: String
) {

}