package navigation

import Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.*


class NavGraph(private var startScreen: Screen = Screen.Home) {
    private val screens = Stack<Screen>().apply { push(startScreen) }

    var currentScreen: Screen by mutableStateOf(screens.peek())

    fun navigateTo(screen: Screen) {
        screens.push(screen)
        currentScreen = screen
    }

    fun back() {
        if (screens.size > 1) {
            screens.pop()
            currentScreen = screens.peek()
        }
    }
}








