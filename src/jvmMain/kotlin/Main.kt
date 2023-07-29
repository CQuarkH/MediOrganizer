// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import Screens.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import navigation.*


private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
@Preview
fun MyApp() {
    val navGraph = NavGraph()
    val pages = Screen.getValues()

    MaterialTheme (colors = DarkColorPalette){
        Row {
            navRail(navGraph, pages)
            when (val screen = navGraph.currentScreen) {
                is Screen.Home -> HomePage(navGraph).Screen()
                is Screen.Register -> PatientRegisterPage(navGraph,
                    screen.patientName,
                    screen.patientSurname,
                    screen.patientAge,
                    screen.patientRUT,
                    screen.date,
                    screen.medicalFindings,
                    screen.isEditing
                    ).Screen()

                is Screen.PatientDetail -> PatientDetailsPage(
                    navGraph, name = screen.patientName, surname = screen.patientSurname, age = screen.patientAge,
                    RUT = screen.RUT, date = screen.date, screen.medicalFindings).Screen()

                is Screen.Patients -> PatientsPage(
                    navGraph,
                    selectedPatient = screen.selectedPatient,
                    isInAppointment = screen.isInAppointment).Screen()

                is Screen.CreateNote -> CreateNotePage(
                    navGraph,
                    title = screen.title,
                    description = screen.description,
                    date = screen.date,
                    isEditing = screen.isEditable
                ).Screen()

                is Screen.Appointments -> AppointmentsPage(navGraph).Screen()
                is Screen.SetAppointments -> SetAppointmentPage(navGraph).Screen()
            }
        }

    }

}


@Composable
fun navRail(navGraph: NavGraph, pages: List<Screen>){

    var selectedItem by remember { mutableStateOf(0) }

    NavigationRail(
        modifier = Modifier.width(100.dp)


    ) {
        pages.forEachIndexed() {
            index, item ->
            NavigationRailItem(
                alwaysShowLabel = false,
                modifier = Modifier.padding(8.dp),
                label = { Text(item.name) },
                icon = { Icon(item.icon, contentDescription = item.name) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navGraph.navigateTo(item)
                }
            )

        }

    }
}



fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MediOrganizer",
    ) {
        MyApp()

    }
}

