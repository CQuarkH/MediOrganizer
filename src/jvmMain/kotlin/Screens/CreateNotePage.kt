package Screens

import SI
import ScreenTiles
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
import controllers.BD
import controllers.Note
import navigation.NavGraph
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CreateNotePage(
    private val navGraph: NavGraph,
    private val title: MutableState<String> = mutableStateOf(""),
    private val description: MutableState<String> = mutableStateOf(""),
    private val date: MutableState<String> = mutableStateOf(""),
    private val isEditing: Boolean = false
) : SI {

    @Composable
    override fun Screen() {

        Scaffold(
            topBar = TopBar(if (isEditing) "${title.value} | ${millisToDate(date.value.toLong())}" else "Create Note")

        ) {
            innerPadding -> Content(innerPadding)
        }
    }

    @Composable
    override fun TopBar(title: String): @Composable () -> Unit {
        return ScreenTiles.SimpleTopBar(title, navigationIcon = Icons.Filled.ArrowBack, navController = navGraph)
    }

    @Composable
    override fun Content(innerPadding: PaddingValues) {

        var noteTitle by remember { mutableStateOf("") }
        var noteDescription by remember { mutableStateOf("") }

        fun createOrUpdateNote(){
            if (isEditing) {BD.editNote(
                Note(
                    title = title.value,
                    description = description.value,
                    date = System.currentTimeMillis().toString(),
                ),
                date.value
            )
                navGraph.back()
            } else
            {BD.createNote(
                    Note(
                        title = noteTitle,
                        description = noteDescription,
                        date = System.currentTimeMillis().toString()
                    )
                )
                navGraph.back()
            }
        }


        Box(modifier = Modifier.padding(innerPadding)){
            Column(

            ) {
                Card(
                    modifier = Modifier.weight(1f).fillMaxSize().padding(8.dp),
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Title")
                        Divider()
                        TextField(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            value = if (isEditing) title.value else noteTitle,
                            onValueChange = {if (isEditing) {title.value = it} else {noteTitle = it}},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            shape = RoundedCornerShape(10),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor =  Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                }
                Card(
                    modifier = Modifier.weight(3f).fillMaxSize().padding(8.dp),
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Description")
                        Divider()
                        TextField(
                            modifier = Modifier.padding(8.dp).fillMaxSize(),
                            value = if (isEditing) description.value else noteDescription,
                            onValueChange = {if (isEditing) {description.value = it} else {noteDescription = it}},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = false,
                            shape = RoundedCornerShape(10),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor =  Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )


                    }

                }

            }
            if ((noteTitle.isNotBlank() && noteDescription.isNotBlank()) ||
                (title.value.isNotBlank() && description.value.isNotBlank()))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ){
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                        modifier = Modifier.padding(10.dp),
                        onClick = { createOrUpdateNote() }
                    ){
                        Icon(Icons.Filled.Done, contentDescription = "Done")
                    }

                }

        }
    }

    fun millisToDate(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val formatter = DateTimeFormatter.ofPattern(Utils.LOCAL_MILIS_FORMAT.value).withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}