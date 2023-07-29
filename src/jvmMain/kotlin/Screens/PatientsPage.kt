package Screens

import SI
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import controllers.Patient
import controllers.BD
import navigation.NavGraph
import java.util.*

class PatientsPage (
    private val navGraph: NavGraph,
    private val selectedPatient: MutableState<Patient> = mutableStateOf(Patient()),
    private val isInAppointment : Boolean = false
    ): SI {

    @Composable
    override fun Screen() {

        var searchText by remember { mutableStateOf("") }

        Scaffold (
            topBar = { TopAppBar(
                elevation = if (isInAppointment) 0.dp else 8.dp,
                modifier = Modifier.height(70.dp),
                title = { Text(if(isInAppointment) "Select a Patient" else "Patients") },
                backgroundColor = if (isInAppointment) Color.Transparent else MaterialTheme.colors.primarySurface,
                actions = {
                    TextField(
                        value = searchText,
                        onValueChange = { newText -> searchText = newText },
                        placeholder = { Text("Search Patients") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        leadingIcon = {Icon(Icons.Outlined.Search, "Search")},
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.textFieldColors(

                            focusedIndicatorColor =  Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            ) },
            floatingActionButton = {
                if (!isInAppointment) FloatingActionButton(onClick = {
                    navGraph.navigateTo(Screen.Register())
                }){
                    Icon(Icons.Filled.Add, contentDescription = "Add Patient")
                }
            }
        ){
                innerPadding -> searchContent(searchText, innerPadding)
        }
    }




    @Composable
    fun searchContent(searchText: String, innerPadding: PaddingValues) {

        var searchList by remember { mutableStateOf(LinkedList<Patient>()) }

        var patientList by remember { mutableStateOf(LinkedList<Patient>()) }

        var selectedCard by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(key1 = Unit){
            patientList = BD.getPatientsList()
        }

        searchList = LinkedList(patientList.filter { it.getName().contains(searchText, ignoreCase = true) || it.getRUT().contains(searchText) })


        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                if(patientList.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text("No patients created yet")
                    }

                }
                else{

                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(if(isInAppointment) filteredIfAppointment(searchList) else searchList) { patient ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isInAppointment){

                                            selectedCard = patient.getRUT()

                                            selectedPatient.value = patient}

                                        else navGraph.navigateTo(
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
                                elevation = 2.dp,
                                backgroundColor = if (patient.getRUT() == selectedCard)
                                    Color.Gray
                                else
                                    MaterialTheme.colors.surface

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

                                    if (!isInAppointment) IconButton(onClick = {
                                        BD.deletePatient(patient.getRUT())
                                        BD.deleteAppointment(patient.getRUT())
                                        patientList = LinkedList(patientList.filterNot { it.getRUT() == patient.getRUT() })

                                    }){ Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete Patient",
                                        tint = androidx.compose.ui.graphics.Color.Red
                                    ) }
                                }
                            }
                        }


                    }
                }
            }
        }

    }

    fun filteredIfAppointment(patients: List<Patient>): List<Patient> {
        return patients.filter { !BD.hasAppointment(it.getRUT()) }
    }

    @Composable
    override fun Content(innerPadding: PaddingValues) {

    }

    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title)
    }
}