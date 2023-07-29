package Screens

import SI
import ScreenTiles
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import controllers.Appointment
import controllers.BD
import controllers.Note
import controllers.Patient
import getMilliseconds
import kotlinx.coroutines.launch
import navigation.NavGraph
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.LinkedList

class HomePage (private val navGraph: NavGraph): SI {

    @Composable
    override fun Screen(){

            Scaffold (
                topBar = TopBar("Home"),
                floatingActionButton = {FloatingActionButton(
                    onClick = {
                        navGraph.navigateTo(Screen.CreateNote())
                    }
                ){
                    Icon(Icons.Filled.Add, "Add Note")
                }
                }

            ){
                innerPadding -> Content(innerPadding)
            }


    }
    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {

        var iconState by remember { mutableStateOf(false) }

        return ScreenTiles.SimpleTopBar(title)
    }
    @Composable
    override fun Content(innerPadding: PaddingValues){

        var appointmentsList by remember { mutableStateOf(LinkedList<Appointment>()) }
        var patientsList by remember { mutableStateOf(LinkedList<Patient>())}
        var notesList by remember { mutableStateOf(LinkedList<Note>())}

        val notesScrollState = rememberLazyListState()
        val notesCoroutineScope = rememberCoroutineScope()

        val appointmentsScrollState = rememberLazyListState()
        val appointmentsCoroutineScope = rememberCoroutineScope()

        val patientsScrollState = rememberLazyListState()
        val patientsCoroutineScope = rememberCoroutineScope()


        LaunchedEffect(key1 = Unit){
            appointmentsList = BD.getAppointmentsList()
            appointmentsList = LinkedList(getTodaysAppointments(appointmentsList))

            patientsList = BD.getPatientsList()

            notesList = BD.getNotesList()
            notesList = LinkedList(sortNotesByDate(notesList))
        }


        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()){
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                       Card(
                           modifier = Modifier.padding(10.dp).fillMaxSize(),
                           backgroundColor = Color.Transparent,
                           elevation = 0.dp

                       ) {
                           Column(
                               modifier = Modifier.padding(8.dp)
                           ) {
                               Text("Today's Appointments")
                               Divider()
                               if(appointmentsList.isEmpty()){
                                   Box(
                                       modifier = Modifier.fillMaxWidth().height(100.dp),
                                       contentAlignment = Alignment.Center
                                   ){
                                       Text("Nothing to do today", modifier = Modifier.padding(8.dp))
                                   }

                               }
                               else{
                                   LazyRow(
                                       state = appointmentsScrollState,
                                       modifier = Modifier
                                           .draggable(
                                               orientation = Orientation.Horizontal,
                                               state = rememberDraggableState { delta ->
                                                   appointmentsCoroutineScope.launch {
                                                       appointmentsScrollState.scrollBy(-delta)
                                                   }
                                               },
                                           )
                                   ){
                                       items(appointmentsList){
                                               appointment ->
                                           Card(
                                               modifier = Modifier
                                                   .padding(8.dp)
                                                   .fillMaxWidth()

                                           ){
                                               Row(
                                                   modifier = Modifier
                                                       .fillMaxWidth()
                                                       .padding(16.dp),
                                                   verticalAlignment = Alignment.CenterVertically,
                                                   horizontalArrangement = Arrangement.SpaceBetween
                                               ) {
                                                   Column(
                                                       modifier = Modifier.padding(8.dp)
                                                   ) {
                                                       Text("${appointment.getTime()} -> ${appointment.getDate()}", modifier = Modifier.padding(8.dp))
                                                       Divider()
                                                       Text(appointment.getPatient().getName())
                                                       Text(appointment.getPatient().getRUT())
                                                   }

                                                   IconButton(
                                                       onClick = {
                                                           BD.deleteAppointment(appointment.getPatient().getRUT())
                                                           appointmentsList = LinkedList(appointmentsList.filterNot {
                                                               getMilliseconds(it.getDate(), it.getTime()) == getMilliseconds(appointment.getDate(), appointment.getTime()) })

                                                       }
                                                   ){
                                                       Icon(Icons.Filled.Done, "Mark As Done")
                                                   }



                                               }

                                           }
                                       }
                                   }
                               }
                           }

                       }
                       Card(
                           modifier = Modifier.padding(10.dp).fillMaxSize(),
                           backgroundColor = Color.Transparent,
                           elevation = 0.dp

                       ) {
                           Column(
                               modifier = Modifier.padding(8.dp)
                           ) {
                               Text("Personal Notes")
                               Divider()
                               if(notesList.isEmpty()){
                                   Box(
                                       modifier = Modifier.fillMaxWidth().height(100.dp),
                                       contentAlignment = Alignment.Center
                                   ){
                                       Text("No Personal Notes Available", modifier = Modifier.padding(8.dp))
                                   }

                               }
                               else{
                                   LazyRow(
                                       state = notesScrollState,
                                       modifier = Modifier
                                           .draggable(
                                               orientation = Orientation.Horizontal,
                                               state = rememberDraggableState { delta ->
                                                   notesCoroutineScope.launch {
                                                       notesScrollState.scrollBy(-delta)
                                                   }
                                               }
                                           )
                                   ) {
                                       items(notesList){
                                               note ->
                                           Card(
                                               modifier = Modifier
                                                   .padding(8.dp)
                                                   .sizeIn(maxWidth = 300.dp)
                                                   .fillMaxWidth()
                                                   .clickable {
                                                       navGraph.navigateTo(Screen.CreateNote(
                                                           isEditable = true,
                                                           title = mutableStateOf(note.getTitle()),
                                                           description = mutableStateOf(note.getDescription()),
                                                           date = mutableStateOf(note.getDate())

                                                       ))
                                                   }
                                           ) {
                                               Row(
                                                   modifier = Modifier.padding(16.dp),
                                                   verticalAlignment = Alignment.CenterVertically,

                                               ) {
                                                   Column(
                                                       modifier = Modifier
                                                           .weight(1f)
                                                           .padding(end = 16.dp)
                                                   ) {
                                                       Text(
                                                           note.getTitle(),
                                                           style = MaterialTheme.typography.h6,
                                                           maxLines = 2,
                                                           overflow = TextOverflow.Ellipsis,
                                                           modifier = Modifier.padding(4.dp)
                                                       )

                                                       Divider()

                                                       Text(
                                                           note.getDescription(),
                                                           style =  MaterialTheme.typography.body2,
                                                           maxLines = 4,
                                                           overflow = TextOverflow.Ellipsis,
                                                           modifier = Modifier.padding(4.dp))
                                                   }
                                                   IconButton(onClick = {
                                                       BD.deleteNoteByDate(note.getDate())
                                                       notesList = LinkedList(notesList.filterNot { it.getDate() == note.getDate() })

                                                   }){ Icon(
                                                       imageVector = Icons.Filled.Delete,
                                                       contentDescription = "Delete Note",
                                                       tint = androidx.compose.ui.graphics.Color.Red
                                                   ) }
                                               }

                                           }
                                       }

                                   }
                               }

                           }

                       }
                       Card(
                           modifier = Modifier.padding(10.dp).fillMaxSize(),
                           backgroundColor = Color.Transparent,
                           elevation = 0.dp

                       ){
                           Column(
                               modifier = Modifier.padding(8.dp)
                           ) {
                               Text("Latest Admissions")
                               Divider()
                               if(patientsList.isEmpty()){
                                   Box(
                                       modifier = Modifier.fillMaxWidth().height(100.dp),
                                       contentAlignment = Alignment.Center
                                   ){
                                       Text("Nothing to show here", modifier = Modifier.padding(8.dp))
                                   }

                               }
                               else{
                                   LazyRow(
                                       state = patientsScrollState,
                                       modifier = Modifier
                                           .draggable(
                                               orientation = Orientation.Horizontal,
                                               state = rememberDraggableState { delta ->
                                                   patientsCoroutineScope.launch {
                                                       patientsScrollState.scrollBy(-delta)
                                                   }
                                               },
                                           )
                                   ) {
                                       items(getLastPatients(patientsList)){
                                               patient ->
                                           Card(
                                               modifier = Modifier
                                                   .padding(8.dp)
                                                   .fillMaxWidth()
                                                   .clickable {
                                                       navGraph.navigateTo(
                                                           Screen.PatientDetail(
                                                               mutableStateOf(patient.getName()),
                                                               mutableStateOf(patient.getSurname()),
                                                               mutableStateOf(patient.getAge().toString()),
                                                               mutableStateOf(patient.getRUT()),
                                                               patient.getDate(),
                                                               mutableStateOf(patient.getMedicalFindings())
                                                           )
                                                       )

                                                   },
                                               elevation = 2.dp
                                           ) {
                                               Row (
                                                   modifier = Modifier
                                                       .fillMaxWidth()
                                                       .padding(16.dp),
                                                   verticalAlignment = Alignment.CenterVertically,
                                                   horizontalArrangement = Arrangement.SpaceBetween

                                               ){
                                                   Column(
                                                       modifier = Modifier
                                                           .padding(16.dp)
                                                           .fillMaxHeight()
                                                   ) {
                                                       Text(text = "${patient.getName()} ${patient.getSurname()}", style = MaterialTheme.typography.h6)
                                                       Spacer(modifier = Modifier.height(4.dp))
                                                       Text(text = "RUT: ${patient.getRUT()}", style = MaterialTheme.typography.body1)
                                                       Spacer(modifier = Modifier.height(4.dp))
                                                       Text(text = "Age: ${patient.getAge()}", style = MaterialTheme.typography.body1)
                                                   }


                                               }
                                           }
                                       }


                                   }
                               }

                       }
                   }


                }

            }

    }

    fun getTodaysAppointments(appointments: List<Appointment>): List<Appointment> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val today = LocalDate.now()

        return appointments.filter { appointment ->
            LocalDate.parse(appointment.getDate(), formatter).isEqual(today)
        }
    }

    fun getLastPatients(patients: List<Patient>): List<Patient>{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss")
        return patients.sortedByDescending { patient ->  LocalDate.parse(patient.getDate(), formatter)}.take(5)
    }

    fun sortNotesByDate(notes: List<Note>) : List<Note>{
        return notes.sortedByDescending { note -> note.getDate().toLong() }
    }


}