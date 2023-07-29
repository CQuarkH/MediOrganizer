package controllers

class Patient(
    private val name: String = "",
    private val surname: String = "",
    private val RUT: String = "",
    private val age: Int = 0,
    private val date: String = "",
    private val medicalFindings: String = ""

   ) {


    fun getName(): String{
        return this.name
    }

    fun getRUT(): String{
        return this.RUT
    }

    fun getAge(): Int{
        return this.age
    }

    fun getDate() : String{
        return this.date
    }

    fun getMedicalFindings() : String{
        return this.medicalFindings
    }

    fun getSurname() : String{
        return this.surname
    }




}