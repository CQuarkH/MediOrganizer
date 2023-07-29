package Screens

import SI
import ScreenTiles
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controllers.BD
import navigation.NavGraph


class PatientDetailsPage(
    private val navGraph: NavGraph,
    private val name: MutableState<String>,
    private val surname: MutableState<String>,
    private val age: MutableState<String>,
    private val RUT: MutableState<String>,
    private val date: String = "",
    private val medicalFindings: MutableState<String>
    ): SI {
    @Composable
    override fun Screen() {
        Scaffold(
            topBar = TopBar("Patient Detail"),
        ) {
            innerPadding -> Content(innerPadding)
        }

    }
    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title, navController = this.navGraph, navigationIcon = Icons.Filled.ArrowBack)
    }
    @Composable
    override fun Content(innerPadding: PaddingValues) {

        Box(modifier = Modifier.padding(innerPadding)){
            Column (){
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp).clickable {
                                navGraph.navigateTo(Screen.Register(
                                    name,
                                    surname,
                                    age,
                                    RUT,
                                    date,
                                    medicalFindings,
                                    isEditing = true
                                ))
                            },
                        elevation = 4.dp
                    ) {
                        Column {
                            Text("Patient Data", modifier = Modifier.padding(16.dp))
                            Divider()
                            Text("Name: ${name.value}", modifier = Modifier.padding(8.dp))
                            Text("Age: ${age.value}", modifier = Modifier.padding(8.dp))
                            Text("RUT: ${RUT.value}", modifier = Modifier.padding(8.dp))
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column() {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                elevation = 4.dp
                            ) {
                               Column {
                                   Text("Patient Admission Date", modifier = Modifier.padding(16.dp))
                                   Divider()
                                   Text(date, modifier = Modifier.padding(16.dp))
                               }
                            }
                            Card(modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                                elevation = 4.dp){
                                Column {
                                    Text(
                                        "Next Appointment Date", modifier = Modifier.padding(16.dp))
                                    Divider()
                                    Text(BD.getAppointmentByRut(RUT.value).getDate(), modifier = Modifier.padding(16.dp))
                                }

                            }
                        }
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column {
                            Text("Medical Findings", modifier = Modifier.padding(16.dp))
                            Divider()
                            Text(medicalFindings.value, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

            }

        }
    }
}