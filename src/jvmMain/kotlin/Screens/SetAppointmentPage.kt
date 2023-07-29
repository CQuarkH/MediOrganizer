package Screens

import SI
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controllers.Appointment
import controllers.BD
import controllers.Patient
import navigation.NavGraph
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.LinkedList



class SetAppointmentPage (private val navGraph: NavGraph) : SI {



    @Composable
    override fun Screen() {


        Scaffold (
            topBar = TopBar("Set Appointment")
        ){
            innerPadding -> Content(innerPadding)
        }
    }

    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title, navController = navGraph, navigationIcon = Icons.Filled.ArrowBack)
    }

    @Composable
    override fun Content(innerPadding: PaddingValues) {

        var selectedPatient = remember { mutableStateOf(Patient())}

        var daySelected = remember {
            val now = LocalDateTime.now()
            if (now.hour >= 14) {
                mutableStateOf(now.toLocalDate().plusDays(1))
            } else {
                mutableStateOf(now.toLocalDate())
            }
        }

        var monthSelected = remember { mutableStateOf(LocalDate.now().month)}
        var timeSelected = remember { mutableStateOf("")}
        var appointmentsList = remember { mutableStateOf(LinkedList<Appointment>()) }


        LaunchedEffect(key1 = Unit){
            appointmentsList.value = BD.getAppointmentsList()
        }


        Box(modifier = Modifier.padding(innerPadding)){
            Row (
                modifier = Modifier.fillMaxSize(),
                    ){
                //patients column
                Column (
                    modifier = Modifier.weight(1f).fillMaxSize()
                ){
                    PatientsPage(navGraph, selectedPatient, true).Screen()
                }

                //hour and confirm column
                Column(
                    modifier = Modifier.weight(1f).fillMaxSize().padding(8.dp),
                ) {
                    Calendar(daySelected, monthSelected, timeSelected, appointmentsList.value,Modifier.weight(1f).padding(8.dp))
                    Divider()
                    AppointmentPreview(daySelected, monthSelected, timeSelected ,selectedPatient, Modifier.weight(1f).padding(8.dp))


                }
            }


        }
    }

    @Composable
    fun Calendar(
        daySelected: MutableState<LocalDate>,
        monthSelected: MutableState<Month>,
        timeSelected: MutableState<String>,
        appointmentsList: LinkedList<Appointment>,
        modifier: Modifier){

        var daySelector by remember { mutableStateOf(false)}
        var monthSelector by remember { mutableStateOf(false)}
        var timeSelector by remember { mutableStateOf(false)}


        val daysList = getAvailableDays(monthSelected.value, appointmentsList)
        val monthList = getAvailableMonths(appointmentsList)
        val timesList = getAvailableTimes(daySelected.value, appointmentsList)


       Card(
           modifier = modifier,
           backgroundColor = Color.Transparent,
           elevation = 0.dp
       ){
           Column (
               modifier = Modifier.padding(8.dp),
               horizontalAlignment = Alignment.CenterHorizontally,
                   ){
               Text("Set Date and Time")
               Divider()
               Row (
                   modifier = Modifier.padding(8.dp)
               ){
                   Card (
                       modifier = Modifier.weight(1f).padding(4.dp),
                       backgroundColor = Color.Transparent
                   ){
                       Button(
                           modifier = Modifier.padding(8.dp),
                           onClick = { monthSelector = !monthSelector }) {
                           Text(monthSelected.value.toString().ifEmpty { "Select Month" })
                       }
                       DropdownMenu(
                           expanded = monthSelector,
                           onDismissRequest = {monthSelector = !monthSelector}
                       ){
                           monthList.forEach {
                                   month ->
                               DropdownMenuItem(
                                   onClick = {
                                       monthSelected.value = month
                                       monthSelector = !monthSelector
                                   }

                               ){
                                   Text(month.toString())

                               }
                           }
                       }

                   }
                   Card (
                       modifier = Modifier.weight(1f).padding(4.dp),
                       backgroundColor = Color.Transparent
                   ){
                       Button(
                           modifier = Modifier.padding(8.dp),
                           onClick = {daySelector = !daySelector}){
                           Text(daySelected.value.dayOfMonth.toString().ifEmpty { "Select Day" })
                       }
                       DropdownMenu(
                           expanded = daySelector,
                           onDismissRequest = {daySelector = !daySelector}
                       ){
                           daysList.forEach {
                                   day ->
                               DropdownMenuItem(
                                   onClick = {
                                       daySelected.value = day
                                       daySelector = !daySelector
                                   }
                               ){
                                   Text(day.dayOfMonth.toString())

                               }
                           }
                       }

                   }



               }
               Row {
                   Card (
                       modifier = Modifier.weight(1f).padding(4.dp),
                       backgroundColor = Color.Transparent
                   ){
                       Button(
                           modifier = Modifier.padding(8.dp),
                           onClick = { timeSelector = !timeSelector}){
                           Text(timeSelected.value.toString().ifEmpty { "Select Time" })
                       }
                       DropdownMenu(
                           expanded = timeSelector,
                           onDismissRequest = {timeSelector = !timeSelector}
                       ){
                           timesList.forEach {
                                   time ->
                               DropdownMenuItem(
                                   onClick = {
                                       timeSelected.value = time.toString()
                                       timeSelector = !timeSelector
                                   }
                               ){
                                   Text(time.toString())

                               }
                           }
                       }

                   }
               }
           }

       }
    }

    @Composable
    fun AppointmentPreview(
        daySelected: MutableState<LocalDate>,
        monthSelected: MutableState<Month>,
        timeSelected: MutableState<String>,
        patientSelected: MutableState<Patient>,
        modifier: Modifier
    ){

        var isDone by remember { mutableStateOf(false) }

        fun checkFields() {
            isDone = daySelected.value.toString().isNotEmpty() &&
                    monthSelected.value.toString().isNotEmpty() &&
                    timeSelected.value.toString().isNotEmpty() &&
                    patientSelected.value.getRUT().isNotEmpty()
        }

        LaunchedEffect(daySelected.value, monthSelected.value, timeSelected.value, patientSelected.value.getRUT()) {
            checkFields()
        }


        fun createAppointment(){
            val date = LocalDate.of(LocalDate.now().year, monthSelected.value, daySelected.value.dayOfMonth)
            BD.createAppointment(
                Appointment(
                    timeSelected.value,
                    date.format(DateTimeFormatter.ofPattern(Utils.LOCAL_DATE_FORMAT.value)).toString(),
                    patientSelected.value
                ))
            navGraph.back()
        }


        Card (
            modifier = modifier,
            backgroundColor = Color.Transparent,
            elevation = 0.dp
                ){
           Column(
               modifier = Modifier.padding(8.dp),
               horizontalAlignment = Alignment.CenterHorizontally,
           ) {
               Text("Preview")
               Divider()
               //patient's info card
               Row (
                   modifier = Modifier.fillMaxSize().padding(8.dp)
                       ){
                   Card (
                       modifier = Modifier.padding(8.dp).weight(1f),
                   ){
                       Column(
                           modifier = Modifier.fillMaxSize().padding(10.dp),
                       ) {
                           Text("Patient")
                           if(patientSelected.value.getRUT() == "")
                               Box(
                                   modifier = Modifier.fillMaxSize(),
                                   contentAlignment = Alignment.Center
                               ){
                                   Text("No selected yet")
                               }
                           else
                               PatientCard(patientSelected.value, Modifier.weight(1f).fillMaxSize().padding(8.dp))
                       }

                   }
                   Card (
                       modifier = Modifier.padding(8.dp).weight(1f).fillMaxSize(),
                   ){
                       Column(
                           modifier = Modifier.padding(10.dp)
                       ) {
                           Text("Date")
                           Box(
                               modifier = Modifier.fillMaxSize(),
                               contentAlignment = Alignment.Center
                           ){
                               Column(
                                   modifier = Modifier.padding(8.dp)
                               ) {
                                   Text("${daySelected.value.dayOfMonth} of ${monthSelected.value}")
                                   Text("--> ${timeSelected.value}")
                               }
                           }

                       }
                   }
               }
           }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (isDone) FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    onClick = { createAppointment() },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Done")
                }
            }


        }

    }


    @Composable
    fun PatientCard(patient: Patient, modifier: Modifier){
        Card (
            modifier = modifier,
            backgroundColor = Color.Transparent
                ){
            Column (
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
                    ){
                Text("${patient.getName()} ${patient.getSurname()}")
                Text("\n")
                Text(patient.getRUT())
            }
        }

    }

    fun getAvailableDays(month: Month, appointments: List<Appointment>): List<LocalDate> {
        val currentYear = Year.now().value
        val startOfGivenMonth = LocalDate.of(currentYear, month, 1)
        val endOfGivenMonth = startOfGivenMonth.with(TemporalAdjusters.lastDayOfMonth())
        val today = LocalDate.now()

        val firstDayToConsider = if (startOfGivenMonth.isBefore(today)) today else startOfGivenMonth

        val totalDays = ChronoUnit.DAYS.between(firstDayToConsider, endOfGivenMonth).toInt() + 1

        val allPossibleTimes = listOf(
            LocalTime.of(8, 0),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0)
        )

        return generateSequence(firstDayToConsider) { it.plusDays(1) }
            .take(totalDays)
            .filter { it.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }
            .filter { day ->
                if(day.isEqual(today) && LocalTime.now().isAfter(LocalTime.of(14, 0))) {
                    false
                } else {
                    val timesOnThisDay = appointments.filter { it.getDate() == day.format(DateTimeFormatter.ofPattern(Utils.LOCAL_DATE_FORMAT.value)) }.map {
                        LocalTime.parse(it.getTime(), DateTimeFormatter.ofPattern(Utils.LOCAL_TIME_FORMAT.value))
                    }
                    (allPossibleTimes - timesOnThisDay).isNotEmpty()
                }
            }
            .toList()

    }

    fun getAvailableMonths(appointments: List<Appointment>): List<Month> {

        val currentMonth = LocalDate.now().month

        return (currentMonth.value..12)
            .map { Month.of(it) }
            .filter { month ->
                val regex = Regex("/${if (month.value < 10) "0" else ""}${month.value}/")
                val daysInThisMonth = appointments.filter { regex.containsMatchIn(it.getDate()) }.map { it.getDate() }
                val availableDays = getAvailableDays(month, appointments).map { it.toString() }
                availableDays - daysInThisMonth.toSet() == availableDays
            }
    }

    fun getAvailableTimes(day: LocalDate, appointments: List<Appointment>): List<LocalTime> {

        val allPossibleTimes = listOf(
            LocalTime.of(8, 0),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0)
        )

        val timesOnThisDay = appointments
            .filter { it.getDate() == day.format(DateTimeFormatter.ofPattern(Utils.LOCAL_DATE_FORMAT.value)) }
            .map { LocalTime.parse(it.getTime(), DateTimeFormatter.ofPattern(Utils.LOCAL_TIME_FORMAT.value)) }


        val now = LocalDateTime.now()

        val futureTimes = if (day.isEqual(now.toLocalDate())) {
            allPossibleTimes.filter { it.isAfter(now.toLocalTime()) }
        } else {
            allPossibleTimes
        }

        return futureTimes - timesOnThisDay.toSet()
    }




}