package Screens

import SI
import ScreenTiles
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import controllers.Patient
import controllers.BD
import navigation.NavGraph
import java.util.LinkedList

class SearchPage (val navGraph: NavGraph): SI {
    @Composable
    override fun Screen() {
        var searchText by remember { mutableStateOf("") }
        var patientList by remember { mutableStateOf(LinkedList<Patient>()) }

        LaunchedEffect(key1 = Unit){
            patientList = BD.getPatientsList()
        }

        Scaffold(
            topBar = {TopAppBar(
                title = { Text("Search") },
                actions = {
                    TextField(
                        value = searchText,
                        onValueChange = { newText -> searchText = newText },
                        placeholder = { Text("Search Here") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    )
                }
            )},
        ) {
            innerPadding -> searchContent(searchText, innerPadding, patientList)
        }
    }



    @Composable
    fun searchContent(searchText: String, innerPadding: PaddingValues, patientList: LinkedList<Patient>){
        var searchList = remember { mutableStateOf(mutableListOf<Patient>()) }

        patientList.filter { it.getName().contains(searchText, ignoreCase = true) }


        Box(modifier = Modifier.padding(innerPadding)){
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(patientList){ patient ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {


                                }
                            ,
                            elevation = 2.dp
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ){
                                Text(text = patient.getName(), style = MaterialTheme.typography.h6)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "RUT: ${patient.getRUT()}", style = MaterialTheme.typography.body1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Edad: ${patient.getAge()}", style = MaterialTheme.typography.body1)
                            }
                        }
                    }


                }
            }
        }

    }






    @Composable
    override fun Content(innerPadding: PaddingValues) {



    }
    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title)
    }



}