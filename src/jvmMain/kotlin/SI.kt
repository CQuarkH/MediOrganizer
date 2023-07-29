import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface SI {
    @Composable
    fun Screen()
    @Composable
    fun TopBar(title: String): @Composable () -> Unit
    @Composable
    fun Content(innerPadding: PaddingValues)

}