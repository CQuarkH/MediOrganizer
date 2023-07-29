package Screens

import SI
import ScreenTiles
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controllers.Appointment
import controllers.BD
import getMilliseconds
import kotlinx.coroutines.launch
import navigation.NavGraph
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class AppointmentsPage(private val navGraph: NavGraph) : SI {

    @Composable
    override fun Screen() {
        Scaffold (
            topBar = TopBar("Appointments"),
            floatingActionButton = {FloatingActionButton(
                onClick = {
                    navGraph.navigateTo(Screen.SetAppointments)
                }
            ) {
                Icon(Icons.Filled.Add, "Add Appointment")
            }}
        ){
            innerPadding -> Content(innerPadding)

        }
    }
    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title)
    }
    @Composable
    override fun Content(innerPadding: PaddingValues) {
        var appoinmentsList by remember { mutableStateOf(LinkedList<Appointment>()) }

        val scrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = Unit){
            appoinmentsList = BD.getAppointmentsList()
        }

        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            Column(
                modifier = Modifier.padding(8.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Card(
                    modifier = Modifier.weight(2f).padding(8.dp),
                    elevation = 0.dp,
                    backgroundColor = Color.Transparent

                ){
                    Column(
                        modifier = Modifier.padding(8.dp)

                    ) {
                        Text("Appointments", modifier = Modifier.padding(8.dp))
                        Divider()
                        if(appoinmentsList.isEmpty()){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                Text("No Appointments yet")

                            }

                        }
                        else{
                            LazyRow(
                                state = scrollState,
                                modifier = Modifier.padding(8.dp)
                                    .draggable(
                                        orientation = Orientation.Horizontal,
                                        state = rememberDraggableState { delta ->
                                            coroutineScope.launch {
                                                scrollState.scrollBy(-delta)
                                            }
                                        },
                                    )

                            ) {
                                items(getSortedAppointments(appoinmentsList)){
                                        item ->
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
                                                Text("${item.getTime()} -> ${item.getDate()}", modifier = Modifier.padding(8.dp))
                                                Divider()
                                                Text(item.getPatient().getName())
                                                Text(item.getPatient().getRUT())
                                            }
                                            IconButton(
                                                onClick = {
                                                    BD.deleteAppointment(item.getPatient().getRUT())
                                                    appoinmentsList =
                                                        LinkedList(appoinmentsList.filterNot {
                                                            getMilliseconds(it.getDate(), it.getTime()) == getMilliseconds(item.getDate(), item.getTime()) })
                                                }
                                            ){
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete Appointment",
                                                    tint = androidx.compose.ui.graphics.Color.Red
                                                )

                                            }


                                        }

                                    }
                                }
                            }

                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).padding(8.dp),
                    elevation = 0.dp,
                    backgroundColor = Color.Transparent

                ){
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ){
                        Card(
                            modifier = Modifier.weight(1f).padding(8.dp).fillMaxSize()

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            ) {
                                Text("Total Appointments")
                                Divider()
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(appoinmentsList.size.toString())
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f).padding(8.dp).fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            ) {
                                Text("Total Hours")
                                Divider()
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(appoinmentsList.size.toString())
                                }

                            }

                        }
                    }

                }

            }




        }
    }


    fun getSortedAppointments(appointments: List<Appointment>): List<Appointment> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        return appointments.sortedBy {
            LocalDateTime.parse("${it.getDate()} ${it.getTime()}", formatter)
        }
    }
}