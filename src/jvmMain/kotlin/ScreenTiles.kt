import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import navigation.NavGraph

class ScreenTiles {

    companion object{
        @Composable
        fun SimpleTopBar(
            title: String,
            actions: @Composable () -> Unit = {},
            navigationIcon: ImageVector? = null,
            navController : NavGraph? = null
        ) : @Composable () -> Unit{
            return{
                TopAppBar(
                    modifier = Modifier.height(70.dp),
                    title = {Text(title)},
                    actions = {actions()},
                    navigationIcon = {
                        navigationIcon.let {
                            IconButton(onClick = { navController?.back() }) {
                                if (it != null) {
                                    Icon(it, contentDescription = null)
                                }
                            }
                        }
                    }
                )
            }

        }



    }

}