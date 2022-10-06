package com.example.hermes.domain

import com.example.hermes.domain.data.local.address.entities.AddressEntity
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.local.products.entities.SizeEntity
import com.example.hermes.domain.data.local.shops.entities.ShopEntity
import com.example.hermes.domain.data.local.user.entities.UserEntity
import com.example.hermes.domain.data.network.profile.models.IOperator
import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.products.models.ISize
import com.example.hermes.domain.data.network.profile.models.IUser
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.*

class Mapper {

    fun mapUserDBToUser(userEntity: UserEntity?): User? {
        if (userEntity == null) return null
        return User(
            userEntity.uid,
            userEntity.login,
            userEntity.password,
            userEntity.surname,
            userEntity.name,
            userEntity.phoneNumber,
            userEntity.mail
        )
    }

    fun mapToNetworkUser(user: User): IUser {
        return IUser(
            user.uid,
            user.login,
            user.password,
            user.surname,
            user.name,
            user.phoneNumber,
            user.mail
        )
    }

    fun mapUserToUserDB(user: User): UserEntity {
        return UserEntity(
            user.uid,
            user.login,
            user.password,
            user.surname,
            user.name,
            user.phoneNumber,
            user.mail
        )
    }

    fun mapShopToDBShop(shop: Shop?): ShopEntity? {
        if (shop == null) return null
        return ShopEntity(
            shop.uid,
            shop.name,
            shop.physicalAddress,
            shop.legalAddress,
            shop.phoneNumber,
            shop.imagePath
        )
    }

    fun mapShopDBToShop(shopEntity: ShopEntity?): Shop? {
        if (shopEntity == null) return null
        return Shop(
            shopEntity.uid,
            shopEntity.name,
            shopEntity.physicalAddress,
            shopEntity.legalAddress,
            shopEntity.phoneNumber,
            shopEntity.imagePath
        )
    }

    fun mapNetworkShopToShop(i_shop: IShop?): Shop? {
        if (i_shop == null) return null
        return Shop(
            i_shop.uid,
            i_shop.name,
            i_shop.physicalAddress,
            i_shop.legalAddress,
            i_shop.phoneNumber,
            i_shop.imagePath
        )
    }

    fun mapNetworkOperatorToOperator(shop: Shop?, operator: IOperator?): Operator? {
        if (shop == null || operator == null) return null
        return Operator(
            operator.uid,
            operator.login,
            operator.password,
            operator.surname,
            operator.name,
            operator.phoneNumber,
            operator.mail,
            shop
        )
    }

    fun mapOperatorToOperatorDB(operator: Operator): OperatorEntity {
        return OperatorEntity(
            operator.uid,
            operator.login,
            operator.password,
            operator.surname,
            operator.name,
            operator.phoneNumber,
            operator.mail,
            operator.shop.uid
        )
    }

    fun mapNetworkUserToUser(user: IUser?): User? {
        if (user == null) return null
        return User(
            user.uid,
            user.login,
            user.password,
            user.surname,
            user.name,
            user.phoneNumber,
            user.mail
        )
    }

    fun mapDBAddressToAddress(addressEntity: List<AddressEntity>?): List<Address> {
        if (addressEntity == null) return listOf()
        val address: MutableList<Address> = mutableListOf()
        addressEntity.forEach {
            mapDBAddressToAddress(it)?.let { it1 -> address.add(it1) }
        }
        return address
    }

    fun mapAddressToDBAddress(addresses : List<Address>?): List<AddressEntity> {
        if (addresses == null) return listOf()
        val addressesEntity: MutableList<AddressEntity> = mutableListOf()
        addresses.forEach {
            addressesEntity.add(mapAddressToDBAddress(it))
        }
        return addressesEntity
    }

    fun mapDBAddressToAddress(addressEntity: AddressEntity?): Address? {
        if (addressEntity == null) return null
        return Address(
            addressEntity.uid,
            addressEntity.street,
            addressEntity.entrance,
            addressEntity.floor.toLong(),
            addressEntity.numberApartment,
            addressEntity.intercom,
            addressEntity.active == "true"
        )
    }

    fun mapAddressToDBAddress(address: Address): AddressEntity {
        return AddressEntity(
            address.uid,
            address.street,
            address.entrance,
            address.floor.toString(),
            address.numberApartment,
            address.intercom,
            address.active.toString()
        )
    }

    fun mapShopToDBShop(shops: List<Shop>): List<ShopEntity> {
        val shopsEntity: MutableList<ShopEntity> = mutableListOf()
        shops.forEach {
            val shopEntity = ShopEntity(
                it.uid,
                it.name,
                it.physicalAddress,
                it.legalAddress,
                it.phoneNumber,
                it.imagePath
            )
            shopsEntity.add(shopEntity)
        }
        return shopsEntity
    }

    fun mapNetworkShopsToShops(i_shops: List<IShop?>?): List<Shop> {
        if (i_shops == null) return listOf()
        val shops: MutableList<Shop> = mutableListOf()
        i_shops.forEach {
            if (it == null) return@forEach
            val shop = Shop(
                it.uid,
                it.name,
                it.physicalAddress,
                it.legalAddress,
                it.phoneNumber,
                it.imagePath
            )
            shops.add(shop)
        }
        return shops
    }

    fun mapOperatorDBToOperator(
        shopEntity: ShopEntity?,
        operatorEntity: OperatorEntity?
    ): Operator? {
        if (operatorEntity == null || shopEntity == null) return null
        val shop = mapShopDBToShop(shopEntity) ?: return null
        return Operator(
            operatorEntity.uid,
            operatorEntity.login,
            operatorEntity.password,
            operatorEntity.surname,
            operatorEntity.name,
            operatorEntity.phoneNumber,
            operatorEntity.mail,
            shop
        )
    }


    fun mapNetworkProductsToProducts(
        i_products: List<IOrderProduct?>?,
        i_order: IOrder
    ): List<Product> {
        if (i_products == null) return listOf()
        val products: MutableList<Product> = mutableListOf()
        i_products.forEach {
            if (it == null) return@forEach
            val sizes: List<Size> = listOf(Size("", it.size, true))
            val product = Product(
                it.uid,
                i_order.shopUid,
                it.name,
                it.price.toLong(),
                it.amount.toLong(),
                it.quantity.toLong(),
                it.imagePath,
                sizes
            )
            products.add(product)
        }
        return products
    }

    fun mapNetworkOrderToOrder(
        i_orders: IOrder,
        shop: Shop,
        products: List<Product>,
        address: Address?,
        client: Client
    ): Order {
        return Order(
            i_orders.uid,
            i_orders.number,
            i_orders.amount.toLong(),
            i_orders.quantity.toLong(),
            shop,
            client,
            address,
            products,
            i_orders.comment,
            Order.Status.values().first { it.key == i_orders.status },
            Order.Method.values().first { it.key == i_orders.method }
        )
    }

    fun mapNetworkUserToClient(iUser: IUser?): Client? {
        if (iUser == null) return null
        return Client(
            iUser.uid,
            iUser.surname,
            iUser.name,
            iUser.phoneNumber,
            iUser.mail
        )
    }

    fun mapNetworkDeliveryToDelivery(iDelivery: IDelivery?): Address? {
        if (iDelivery == null) return null
        return Address(
            iDelivery.uid,
            iDelivery.street,
            iDelivery.entrance,
            iDelivery.floor.toLong(),
            iDelivery.numberApartment,
            iDelivery.intercom,
            false
        )
    }

    fun mapNetworkOrderProductsToProducts(
        order: Order,
        i_orderProducts: List<IOrderProduct?>?
    ): List<Product> {
        if (i_orderProducts == null) return listOf()
        val products: MutableList<Product> = mutableListOf()
        i_orderProducts.forEach {
            if (it == null) return@forEach
            val sizes: List<Size> = listOf(Size("", it.size, true))
            val product = Product(
                it.uid,
                order.shop.uid,
                it.name,
                it.price.toLong(),
                it.amount.toLong(),
                it.quantity.toLong(),
                it.imagePath,
                sizes
            )
            products.add(product)
        }
        return products
    }


    fun mapToNetworkDelivery(address: Address): IDelivery {
        return IDelivery(
            address.uid,
            address.street,
            address.entrance,
            address.floor.toString(),
            address.numberApartment,
            address.intercom
        )
    }

    fun mapToNetworkOrder(order: Order): IOrder {
        return IOrder(
            order.uid,
            order.number,
            order.amount.toString(),
            order.quantity.toString(),
            order.comment,
            order.status.key,
            order.method.key,
            order.shop.uid,
            order.client.uid,
            order.address?.uid ?: order.shop.uid
        )
    }

    fun mapToNetworkOrderProducts(order: Order): List<IOrderProduct> {
        val orderProducts: MutableList<IOrderProduct> = mutableListOf()
        order.products.forEach {
            val product = IOrderProduct(
                it.uid,
                it.name,
                it.price.toString(),
                it.amount.toString(),
                it.quantity.toString(),
                order.uid,
                it.imagePath,
                it.getSelectedSize().value
            )
            orderProducts.add(product)
        }
        return orderProducts
    }

    fun mapBaseProductToProduct(
        productsEntity: List<ProductEntity>,
        sizesEntity: List<SizeEntity>
    ): List<Product> {
        val products: MutableList<Product> = mutableListOf()
        productsEntity.forEach {
            val sizes: List<Size> = sizesEntity.filter { sizeEntity ->
                sizeEntity.uidProduct == it.uid
            }.map { sizeEntity ->
                val selected = it.size == sizeEntity.value
                Size(sizeEntity.uid, sizeEntity.value, selected)
            }

            listOf(Size("", it.size, true))
            val product = Product(
                it.uid,
                it.shopUid,
                it.name,
                it.price,
                it.amount,
                it.quantity,
                it.imagePath,
                sizes
            )
            products.add(product)
        }
        return products
    }

    fun mapProductToBaseProduct(shop: Shop, product: List<Product>): List<ProductEntity> {
        val productsEntity: MutableList<ProductEntity> = mutableListOf()
        product.forEach {
            val productEntity = ProductEntity(
                it.uid,
                shop.uid,
                it.name,
                it.price,
                it.amount,
                it.quantity,
                it.imagePath,
                it.getSelectedSize().value
            )
            productsEntity.add(productEntity)
        }
        return productsEntity
    }


    fun mapToNetworkShop(shop: Shop): IShop {
        return IShop(
            shop.uid,
            shop.name,
            shop.physicalAddress,
            shop.legalAddress,
            shop.phoneNumber,
            shop.imagePath
        )
    }

    fun mapNetworkSizesToSizes(iSize: List<ISize>?): List<Size> {
        if (iSize == null) return listOf()
        val sizes: MutableList<Size> = mutableListOf()
        iSize.forEach {
            val size = Size(
                it.uid,
                it.value,
                false
            )
            sizes.add(size)
        }
        return sizes
    }

    fun mapSizesToBaseSizes(productUid: String, sizes: List<Size>?): List<SizeEntity> {
        if (sizes == null) return listOf()
        val sizesEntity: MutableList<SizeEntity> = mutableListOf()
        sizes.forEach {
            val size = SizeEntity(
                it.uid,
                it.value,
                productUid
            )
            sizesEntity.add(size)
        }
        return sizesEntity
    }

    fun mapNetworkProductsToProducts(i_product: IProduct, sizes: List<Size>): Product {
        return Product(
            i_product.uid,
            i_product.shopUID,
            i_product.name,
            i_product.price.toLong(),
            0,
            0,
            i_product.imagePath,
            sizes
        )
    }
}