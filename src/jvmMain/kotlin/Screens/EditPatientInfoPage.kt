package Screens

import SI
import ScreenTiles
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import navigation.NavGraph

class EditPatientInfoPage (
    private val navGraph: NavGraph,
    private val name: MutableState<String>,
    private val age: MutableState<Int>,
    private val RUT: MutableState<String>,
    private val medicalFindings: MutableState<String>
    ): SI {

    @Composable
    override fun Screen() {
        Scaffold(
            topBar = TopBar("Edit Patient Information")
        ) {
            innerPadding -> Content(innerPadding)

        }
    }

    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title, navController = navGraph, navigationIcon = Icons.Filled.ArrowBack)
    }

    @Composable
    override fun Content(innerPadding: PaddingValues) {


        Box(modifier = Modifier.padding(innerPadding)){
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TextField(
                    value = name.value,
                    onValueChange = { newValue -> name.value = newValue },
                    label = {Text("Name")}
                )
                Button(onClick = {navGraph.back()}){
                    Text("Done")
                }
            }

        }


    }

}