package com.example.hermes.domain

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import android.widget.Toast
import com.example.hermes.R
import com.example.hermes.ui.map.AddressAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class Map(
    val context: Context,
    private val mapView: MapView,
    private val onGetAddressListener: OnGetAddressListener,
) : UserLocationObjectListener, Session.SearchListener,
    GeoObjectTapListener {

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun create() {
        MapKitFactory.initialize(context)
        mapView.map.isRotateGesturesEnabled = false

        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true

        userLocationLayer.setObjectListener(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        mapView.map.addTapListener(this)

        setLastKnownLocation()
    }

    fun setSearch(text: String) {
        submitQuery(text)
    }

    fun movePositon() {
        setLastKnownLocation()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
        )

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context, R.drawable.navigation
            )
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, R.drawable.pin),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(context, R.drawable.pin),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()

        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(context, R.drawable.pin)
                )
                mapView.map.move(CameraPosition(resultLocation, 18F, 0F, 0F))
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = context.getString(R.string.map_unknown_error_message)
        if (error is RemoteError) {
            errorMessage = context.getString(R.string.map_remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = context.getString(R.string.map_network_error_message)
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        val point = geoObjectTapEvent.geoObject.geometry.first().point ?: return false
        mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(context, R.drawable.pin)
        )
        val address = getAddress(point.latitude, point.longitude)
        onGetAddressListener.onGetAddress(address)
        return true
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<android.location.Address> = listOf()
        runBlocking {
            val task = launch(Dispatchers.IO) {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
            }
            task.join()
        }
        val city: String = addresses[0].locality ?: ""
        val street = addresses[0].thoroughfare ?: ""
        val house = addresses[0].subThoroughfare ?: ""
        return "$city, $street, $house"
    }

    @SuppressLint("MissingPermission")
    private fun setLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val address = getAddress(location.latitude, location.longitude)
                    mapView.map.move(
                        CameraPosition(
                            Point(location.latitude, location.longitude),
                            18F, 0F, 0F
                        )
                    )
                    onGetAddressListener.onGetAddress(address)
                }
            }
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    fun interface OnGetAddressListener {
        fun onGetAddress(address: String)
    }
}