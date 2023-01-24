package com.example.hermes.di

import com.example.hermes.ui.address.AddressViewModel
import com.example.hermes.ui.addressManager.AddressManagerViewModel
import com.example.hermes.ui.authorization.AuthorizationViewModel
import com.example.hermes.ui.basket.BasketViewModel
import com.example.hermes.ui.delivery.DeliveryViewModel
import com.example.hermes.ui.entrance.EntranceActivity
import com.example.hermes.ui.entrance.EntranceViewModel
import com.example.hermes.ui.map.MapViewModel
import com.example.hermes.ui.orderHistory.OrderHistoryViewModel
import com.example.hermes.ui.orderManager.OrderManagerViewModel
import com.example.hermes.ui.orders.OrdersActivity
import com.example.hermes.ui.orders.OrdersViewModel
import com.example.hermes.ui.payment.PaymentViewModel
import com.example.hermes.ui.products.ProductsViewModel
import com.example.hermes.ui.profile.ProfileViewModel
import com.example.hermes.ui.profileManager.ProfileManagerViewModel
import com.example.hermes.ui.registration.RegistrationViewModel
import com.example.hermes.ui.shops.ShopsViewModel
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        AppModule::class,
        RetrofitServiceModule::class,
        ShopsModule::class,
        ProductsModule::class,
        OrderModule::class,
        ProfileModule::class,
        AddressModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(registrationViewModel: RegistrationViewModel)
    fun inject(authorizationViewModel: AuthorizationViewModel)
    fun inject(shopsViewModel: ShopsViewModel)
    fun inject(productsViewModel: ProductsViewModel)
    fun inject(basketViewModel: BasketViewModel)
    fun inject(deliveryViewModel: DeliveryViewModel)
    fun inject(ordersViewModel: OrdersViewModel)
    fun inject(orderManagerViewModel: OrderManagerViewModel)
    fun inject(orderHistoryViewModel: OrderHistoryViewModel)
    fun inject(paymentViewModel: PaymentViewModel)
    fun inject(profileViewModel: ProfileViewModel)
    fun inject(mapViewModel: MapViewModel)
    fun inject(addressViewModel: AddressViewModel)
    fun inject(addressManagerViewModel: AddressManagerViewModel)
    fun inject(profileManagerViewModel: ProfileManagerViewModel)
    fun inject(entranceViewModel: EntranceViewModel)
}