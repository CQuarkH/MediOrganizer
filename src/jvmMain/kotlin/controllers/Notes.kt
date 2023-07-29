package controllers

class Note(
    private val title: String,
    private val description: String,
    private val date: String
) {
    fun getTitle(): String = title
    fun getDescription(): String = description
    fun getDate(): String = date

}