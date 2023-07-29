import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import controllers.Patient

sealed class Screen(val name: String, val icon: ImageVector = Icons.Default.MoreVert) {
    object Home : Screen("Home", icon = Icons.Filled.Home)

    data class Patients(
        val selectedPatient: MutableState<Patient> = mutableStateOf(Patient()),
        val isInAppointment: Boolean = false
    ) : Screen("Patients", icon = Icons.Filled.Person)

    data class Register(
        val patientName: MutableState<String> = mutableStateOf(""),
        val patientSurname: MutableState<String> = mutableStateOf(""),
        val patientAge: MutableState<String> = mutableStateOf(""),
        val patientRUT: MutableState<String> = mutableStateOf(""),
        val date: String = "NaN",
        val medicalFindings: MutableState<String> = mutableStateOf(""),
        val isEditing: Boolean = false
    ) : Screen("Register")

    data class CreateNote(
        val title: MutableState<String> = mutableStateOf(""),
        val description: MutableState<String> = mutableStateOf(""),
        val date: MutableState<String> = mutableStateOf(""),
        val isEditable: Boolean = false
    ) : Screen("Create Note")

    object Appointments : Screen("Appoint", icon = Icons.Filled.DateRange)

    object SetAppointments : Screen("SetAppointments")


    data class PatientDetail(
        val patientName: MutableState<String>,
        val patientSurname: MutableState<String>,
        val patientAge: MutableState<String>,
        val RUT: MutableState<String>,
        val date: String,
        val medicalFindings: MutableState<String>)
        : Screen("Patient Details")




    companion object{
        fun getValues(): List<Screen>{
            return listOf(Home, Patients(), Appointments)
        }
    }
}

