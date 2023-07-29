package controllers

class Appointment (
    private val time: String,
    private val date: String,
    private val patient: Patient
    ) {

    fun getTime(): String{
        return this.time
    }

    fun getDate(): String{
        return this.date
    }

    fun getPatient(): Patient{
        return this.patient
    }
}