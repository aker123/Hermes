package com.example.hermes.domain.repository

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.network.order.OrderApiManager
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.models.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderRepository(
    private val orderApiManager: OrderApiManager,
    private val mapper: Mapper
) {

    fun sendOrder(order: Order) {

        orderApiManager.setOrder(mapper.mapToNetworkOrder(order))

        orderApiManager.setOrderProducts(mapper.mapToNetworkOrderProducts(order))

        if (order.address != null) orderApiManager.setDelivery(mapper.mapToNetworkDelivery(order.address))
    }

    fun sendOrderStatus(order: Order) {
        orderApiManager.updateOrder(mapper.mapToNetworkOrder(order))
    }

    fun getOrders(shop: Shop): MutableLiveData<List<Order>?> {
        val orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        orderApiManager.getOrders(shop.uid, object : Callback<List<IOrder?>?> {
            override fun onResponse(
                call: Call<List<IOrder?>?>,
                response: Response<List<IOrder?>?>
            ) {
                if (response.isSuccessful) {
                    val orderList: MutableList<Order> = mutableListOf()
                    val body: List<IOrder?>? = response.body()
                    body?.forEach {
                        if (it == null) return@forEach
                        var address: Address? = null
                        var client: Client? = null

                        runBlocking {
                            val tasks = listOf(
                                launch(Dispatchers.IO) {
                                    address = getDelivered(it)
                                },
                                launch(Dispatchers.IO) {
                                    client = getClient(it)
                                })
                            tasks.joinAll()
                        }

                        if (client == null) return@forEach
                        orderList.add(
                            mapper.mapNetworkOrderToOrder(
                                it, shop, listOf(), address,
                                client!!
                            )
                        )
                    }
                    orders.value = orderList
                } else {
                    orders.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IOrder?>?>, t: Throwable) {
                orders.postValue(null)
            }
        })
        return orders
    }


    fun getActiveOrders(user: User): MutableLiveData<List<Order>?> {
        val orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        orderApiManager.getActiveOrders(user.uid, object : Callback<List<IOrder?>?> {
            override fun onResponse(
                call: Call<List<IOrder?>?>,
                response: Response<List<IOrder?>?>
            ) {
                if (response.isSuccessful) {
                    val orderList: MutableList<Order> = mutableListOf()
                    val body: List<IOrder?>? = response.body()
                    val client =
                        Client(user.uid, user.surname, user.name, user.phoneNumber, user.mail)
                    body?.forEach {
                        if (it == null) return@forEach
                        var address: Address? = null
                        var shop: Shop? = null
                        var products: List<Product> = listOf()
                        runBlocking {
                            val tasks = listOf(
                                launch(Dispatchers.IO) {
                                    address = getDelivered(it)
                                },
                                launch(Dispatchers.IO) {
                                    shop = getShop(it)
                                },
                                launch(Dispatchers.IO) {
                                    products = getOrderHistoryProducts(it)
                                })
                            tasks.joinAll()
                        }

                        if (shop == null || products.isEmpty()) return@forEach
                        orderList.add(
                            mapper.mapNetworkOrderToOrder(
                                it, shop!!, products, address,
                                client
                            )
                        )
                    }
                    orders.value = orderList
                } else {
                    orders.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IOrder?>?>, t: Throwable) {
                orders.postValue(null)
            }
        })
        return orders
    }


    fun getOrderHistory(user: User): MutableLiveData<List<Order>?> {
        val orders: MutableLiveData<List<Order>?> = MutableLiveData<List<Order>?>()
        orderApiManager.getOrderHistory(user.uid, object : Callback<List<IOrder?>?> {
            override fun onResponse(
                call: Call<List<IOrder?>?>,
                response: Response<List<IOrder?>?>
            ) {
                if (response.isSuccessful) {
                    val orderList: MutableList<Order> = mutableListOf()
                    val body: List<IOrder?>? = response.body()
                    val client =
                        Client(user.uid, user.surname, user.name, user.phoneNumber, user.mail)
                    body?.forEach {
                        if (it == null) return@forEach
                        var address: Address? = null
                        var shop: Shop? = null
                        var products: List<Product> = listOf()
                        runBlocking {
                            val tasks = listOf(
                                launch(Dispatchers.IO) {
                                    address = getDelivered(it)
                                },
                                launch(Dispatchers.IO) {
                                    shop = getShop(it)
                                },
                                launch(Dispatchers.IO) {
                                    products = getOrderHistoryProducts(it)
                                })
                            tasks.joinAll()
                        }

                        if (shop == null || products.isEmpty()) return@forEach
                        orderList.add(
                            mapper.mapNetworkOrderToOrder(
                                it, shop!!, products, address,
                                client
                            )
                        )
                    }
                    orders.value = orderList
                } else {
                    orders.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IOrder?>?>, t: Throwable) {
                orders.postValue(null)
            }
        })
        return orders
    }


    fun getOrderProducts(order: Order): MutableLiveData<List<Product>?> {
        val products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        orderApiManager.getOrderProducts(order.uid, object : Callback<List<IOrderProduct?>?> {
            override fun onResponse(
                call: Call<List<IOrderProduct?>?>,
                response: Response<List<IOrderProduct?>?>
            ) {
                if (response.isSuccessful) {
                    val body: List<IOrderProduct?>? = response.body()
                    products.value = mapper.mapNetworkOrderProductsToProducts(order, body)
                } else {
                    products.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IOrderProduct?>?>, t: Throwable) {
                products.postValue(null)
            }
        })

        return products
    }

    fun getDelivered(iOrder: IOrder): Address? {
        return mapper.mapNetworkDeliveryToDelivery(orderApiManager.getDelivered(iOrder.deliveryUid))
    }

    fun getShop(i_orders: IOrder): Shop? {
        return mapper.mapNetworkShopToShop(orderApiManager.getShop(i_orders.shopUid))
    }

    fun getClient(i_orders: IOrder): Client? {
        return mapper.mapNetworkUserToClient(orderApiManager.getUser(i_orders.clientUid))
    }

    fun getOrderHistoryProducts(i_order: IOrder): List<Product> {
        return mapper.mapNetworkProductsToProducts(
            orderApiManager.getOrderHistoryProducts(i_order.uid),
            i_order
        )
    }


}