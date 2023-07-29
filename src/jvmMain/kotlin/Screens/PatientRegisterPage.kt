package Screens

import SI
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import controllers.Patient
import controllers.BD
import navigation.NavGraph
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PatientRegisterPage (
    private val navGraph: NavGraph,
    private val name: MutableState<String> = mutableStateOf(""),
    private val surname: MutableState<String> = mutableStateOf(""),
    private val age: MutableState<String> = mutableStateOf(""),
    private val RUT: MutableState<String> = mutableStateOf(""),
    private val date: String,
    private val editMedicalFindings: MutableState<String> = mutableStateOf(""),
    private val isEditing: Boolean = false
    ) : SI {

    @Composable
    override fun Screen() {
        Scaffold (

            topBar = TopBar(if (isEditing) "Edit Patient Info" else "Patient Register")
        ){
                innerPadding -> Content(innerPadding)
        }

    }
    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title, navController = this.navGraph, navigationIcon = Icons.Filled.ArrowBack)
    }
    @Composable
    override fun Content(innerPadding: PaddingValues) {
        val patientName = remember { mutableStateOf("") }
        val patientSurname = remember { mutableStateOf("") }
        val patientAge = remember { mutableStateOf("") }
        val patientRUT = remember { mutableStateOf("") }
        val medicalFindings = remember { mutableStateOf("") }

        var patientList by remember { mutableStateOf(LinkedList<Patient>()) }

        LaunchedEffect(key1 = null){
            patientList = BD.getPatientsList()

        }


        Box(modifier = Modifier.padding(innerPadding)){
            Column (
                modifier = Modifier.fillMaxSize(),

            ){
                Card(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    backgroundColor = Color.Transparent
                ) {
                    Column {
                        Row {
                            formInput("Patient's Names", ::formatName, name, patientName, Modifier.weight(1f).padding(8.dp))
                            formInput("Patient's Surnames", ::formatName, surname, patientSurname, Modifier.weight(1f).padding(8.dp))
                        }
                        Row{
                            formInput("Age", ::formatAge, age, patientAge, Modifier.weight(1f).padding(8.dp))
                            formInput("Patient's RUT", ::formatRut, RUT, patientRUT, Modifier.weight(1f).padding(8.dp), enabled = !isEditing)
                        }
                    }
                }
               Card(
                   modifier = Modifier.fillMaxSize().weight(1f),
                   backgroundColor = Color.Transparent
               ) {
                   Column {
                       Row {
                           formInput("Medical Findings", {input -> input}, editMedicalFindings, medicalFindings, Modifier.weight(1f).padding(8.dp), singleLine = false)
                       }

                       Spacer(Modifier.height(10.dp))

                   }
               }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ){
                if( verifyIfIsEmpty(name.value, surname.value, age.value, RUT.value, editMedicalFindings.value) ||
                    verifyIfIsEmpty(patientName.value, patientSurname.value, patientAge.value, patientRUT.value, medicalFindings.value))
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                        modifier = Modifier.padding(10.dp),
                        onClick = {
                            if (isEditing)
                            {BD.editPatient(Patient(
                                name = name.value,
                                surname = surname.value,
                                age = age.value.toInt(),
                                RUT = RUT.value,
                                date = date,
                                medicalFindings = editMedicalFindings.value
                            ))


                            }
                            else {
                                if(!patientList.any{ it.getRUT() == patientRUT.value}){
                                    BD.createPatient(Patient(
                                        name = patientName.value,
                                        surname = patientSurname.value,
                                        age = patientAge.value.toInt(),
                                        RUT = patientRUT.value,
                                        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss")),
                                        medicalFindings = medicalFindings.value
                                    ))
                                }
                                else{
                                    println("Patient already exists!");
                                }
                            }
                            if (isEditing) navGraph.back() else navGraph.navigateTo(Screen.Patients())
                        }){
                        Icon(Icons.Filled.Done, "Done")
                    }
            }

        }
    }

    private fun formatRut(rut: String): String {
        val cleaned = rut.filter { it.isDigit() || it.uppercaseChar() == 'K' }

        if (cleaned.length < 2) {
            return cleaned
        }

        val withDash = cleaned.dropLast(1) + "-" + cleaned.last()

        if (withDash.length <= 9) {
            return withDash
        }

        val withPoints = StringBuilder(withDash)
        var i = withPoints.lastIndex - 4
        while (i > 0) {
            withPoints.insert(i, '.')
            i -= 3
        }

        return withPoints.toString()
    }

    private fun formatName(name: String): String {
        return name.filter { it.isLetter() || it.isWhitespace() }
    }

    private fun formatAge(age: String): String {
        val digits = age.filter { it.isDigit() }
        return if (digits.isNotEmpty() && digits.toInt() in 1..99) {
            digits
        } else {
            ""
        }
    }


    private fun verifyIfIsEmpty(name: String, surname: String, age: String, RUT: String, medicalFindings: String): Boolean {
        return name.isNotBlank() && surname.isNotBlank() && age.isNotBlank() && RUT.isNotBlank() && medicalFindings.isNotBlank()
    }

    @Composable
    fun formInput(
        title: String,
        formatter: (String) -> String,
        editingValue: MutableState<String>,
        defaultValue: MutableState<String>,
        modifier: Modifier,
        singleLine: Boolean = true,
        enabled: Boolean = true
    ){
            Card(
                modifier = modifier
            ){
                Column {
                    Text(title, modifier = Modifier.padding(8.dp))
                    Divider()
                    TextField(
                        enabled = enabled,
                        modifier = Modifier.padding(8.dp),
                        value = if (isEditing) editingValue.value else defaultValue.value,
                        onValueChange = {if(isEditing) editingValue.value = formatter(it) else defaultValue.value = formatter(it)},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = singleLine,
                        shape = RoundedCornerShape(10),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor =  Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )

                    )
                }
            }


    }



}