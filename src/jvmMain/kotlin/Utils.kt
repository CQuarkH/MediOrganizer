import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun colorFromHex(hex: String): Color {
    val rgb = Integer.decode(hex)
    val r = (rgb shr 16 and 0xFF) / 255.0f
    val g = (rgb shr 8 and 0xFF) / 255.0f
    val b = (rgb and 0xFF) / 255.0f
    return Color(r, g, b)
}

fun getMilliseconds(date: String, time: String): Long {
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val localDate = LocalDate.parse(date, dateFormatter)
    val localTime = LocalTime.parse(time, timeFormatter)

    val dateTime = LocalDateTime.of(localDate, localTime)
    return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}


enum class Utils(val value: String){
    LOCAL_DATE_FORMAT("dd-MM-yyyy"),
    LOCAL_TIME_FORMAT("HH:mm"),
    LOCAL_MILIS_FORMAT("MM-dd - HH:mm")
}
