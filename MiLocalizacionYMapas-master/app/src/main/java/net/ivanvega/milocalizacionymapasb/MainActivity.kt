package net.ivanvega.milocalizacionymapasb

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.platform.location.locationupdates.LocationUpdatesScreen
import com.example.platform.location.permission.LocationPermissionScreen
import net.ivanvega.milocalizacionymapasb.ui.location.CurrentLocationScreen
import net.ivanvega.milocalizacionymapasb.ui.mapas.Contrilling
import net.ivanvega.milocalizacionymapasb.ui.mapas.Customizing
import net.ivanvega.milocalizacionymapasb.ui.mapas.Mapa
import net.ivanvega.milocalizacionymapasb.ui.mapas.MiPrimerMapa
import net.ivanvega.milocalizacionymapasb.ui.mapas.RecomposingElements
import net.ivanvega.milocalizacionymapasb.ui.mapas.ReturnHome
import net.ivanvega.milocalizacionymapasb.ui.mapas.StreetViewScreen
import net.ivanvega.milocalizacionymapasb.ui.mapas.StreetViewScreen
import net.ivanvega.milocalizacionymapasb.ui.theme.MiLocalizacionYMapasBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiLocalizacionYMapasBTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //LocationPermissionScreen()
                        //CurrentLocationScreen()
                        //LocationUpdatesScreen()
                        MiPrimerMapa()
                        //Mapa()
                        //RecomposingElements()
                        //Customizing()
                        //StreetViewScreen()
                        //Contrilling()
                        ReturnHome()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiLocalizacionYMapasBTheme {
        Greeting("Android")
    }
}