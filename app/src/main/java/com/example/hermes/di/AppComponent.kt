package com.example.hermes.di

import com.example.hermes.ui.authorization.AuthorizationViewModel
import com.example.hermes.ui.basket.BasketViewModel
import com.example.hermes.ui.delivery.DeliveryViewModel
import com.example.hermes.ui.entrance.EntranceActivity
import com.example.hermes.ui.products.ProductsViewModel
import com.example.hermes.ui.registration.RegistrationViewModel
import com.example.hermes.ui.shops.ShopsViewModel
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        AppModule::class,
        RetrofitServiceModule::class,
        RegistrationModule::class,
        AuthorizationModule::class,
        ShopsModule::class,
        ProductsModule::class,
        OrderModule::class,
        EntranceModule::class
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
    fun inject(entranceActivity: EntranceActivity)
}