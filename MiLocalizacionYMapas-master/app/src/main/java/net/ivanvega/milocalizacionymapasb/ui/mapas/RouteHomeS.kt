package net.ivanvega.milocalizacionymapasb.ui.mapas

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.ivanvega.milocalizacionymapasb.service.ApiServirce
import net.ivanvega.milocalizacionymapasb.ui.location.PermissionBox
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// ----------------------------------------------------------------------------------
@SuppressLint("MissingPermission")
@Composable
fun ReturnHome() {
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
        onGranted = {
            CurrentLocationContent(
                usePreciseLocation = it.contains(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        },
    )
}
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)

// ----------------------------------------------------------------------------------

@Composable
fun CurrentLocationContent(usePreciseLocation: Boolean) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var Dennis = remember {
        LatLng(20.01008187361755, -101.02132203034884)
    }

    var Itsur = remember {
        LatLng(20.1404425247015, -101.15054421966045)
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Itsur, 10f)
    }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        val point = rememberMarkerState(position = Dennis)

        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {

                Marker(
                    state = MarkerState(position = Dennis),
                    title = "Nenesh",
                    snippet = "home"
                )

                if (!point.position.equals(Dennis)) {
                    Marker(
                        state = MarkerState(position = point.position),
                        title = "Ubicacion actual"
                    )
                    val RouteList = remember { mutableStateOf<List<LatLng>>(emptyList()) }
                    createRoute(Dennis, point.position) { routePoints ->
                        val pointsList = mutableListOf<LatLng>()
                        for (i in routePoints.indices step 2) {
                            val lat = routePoints[i]
                            val lng = routePoints[i + 1]
                            pointsList.add(LatLng(lat, lng))
                        }
                        RouteList.value = pointsList
                    }
                    Polyline(points = RouteList.value)
                }
            }
            Row {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val priority = if (usePreciseLocation) {
                                Priority.PRIORITY_HIGH_ACCURACY
                            } else {
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY
                            }
                            val result = locationClient.getCurrentLocation(
                                priority,
                                CancellationTokenSource().token,
                            ).await()
                            result?.let { fetchedLocation ->
                                point.position =
                                    LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
                            }
                        }
                    },
                ) {
                    Text(text = "Nenesh")
                }
            }
        }
    }
}

private fun createRoute(
    startLocation: LatLng,
    endLocation: LatLng,
    callback: (List<Double>) -> Unit,
) {
    val routePoints = mutableListOf<LatLng>()
    CoroutineScope(Dispatchers.IO).launch {
        val call = getRetrofit().create(ApiServirce:: class.java)
            .getRoute(
                "5b3ce3597851110001cf62487a59928fd2a345b19602528254d56aec",
                "${startLocation.longitude},${startLocation.latitude}",
                "${endLocation.longitude},${endLocation.latitude}"
            )
        if (call.isSuccessful) {
            drawRoute(call.body(), routePoints)
            val pointsList = routePoints.flatMap { listOf(it.latitude, it.longitude) }
            callback(pointsList)
            Log.i("route", "OK")

        } else {
            Log.i("route", "KO")
        }
    }
}

// ----------------------------------------------------------------------------------

private fun drawRoute(routeResponse: RouteR?, routePoints: MutableList<LatLng>) {
    routeResponse?.features?.firstOrNull()?.geometry?.coordinates?.forEach {
        val latLng = LatLng(it[1], it[0])
        routePoints.add(latLng)
    }
}


private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}


