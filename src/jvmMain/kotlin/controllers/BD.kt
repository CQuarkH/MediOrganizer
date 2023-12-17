package controllers

import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val PATIENT_PATH = "src/jvmMain/kotlin/bd/patients"
const val APPOINTMENT_PATH = "src/jvmMain/kotlin/bd/appointments"
const val NOTES_PATH = "src/jvmMain/kotlin/bd/notes"

class BD {

    companion object {

        fun createPatient(patient: Patient) {
            val file = getFile(patient.getRUT(), PATIENT_PATH)
            file.bufferedWriter().use { out ->
                out.write("Patient's Names: ${patient.getName()}\n")
                out.write("Patient's Surnames: ${patient.getSurname()}\n")
                out.write("Age: ${patient.getAge()}\n")
                out.write("RUT: ${patient.getRUT()}\n")
                out.write("Admission Date: ${patient.getDate()}\n")
                out.write("Medical Findings: ${patient.getMedicalFindings()}\n")
            }
        }

        fun createAppointment(appointment: Appointment) {
            val file = getFile(appointment.getPatient().getRUT(), APPOINTMENT_PATH)
            file.bufferedWriter().use { out ->
                out.write("Date: ${appointment.getDate()}\n")
                out.write("Time: ${appointment.getTime()}\n")
                out.write("RUT: ${appointment.getPatient().getRUT()}")
            }
        }

        fun createNote(note: Note) {
            val file = getFile(note.getDate(), NOTES_PATH)
            file.bufferedWriter().use { out ->
                out.write("Title: ${note.getTitle()}\n")
                out.write("Date: ${note.getDate()}\n")
                out.write("Description: ${note.getDescription()}")
            }
        }

        fun deleteNoteByDate(date: String) {
            val file = getFile(date, NOTES_PATH)
            if (file.exists()) {
                file.delete()
            }
        }

        fun editNote(note: Note, pastNoteID: String) {
            deleteNoteByDate(pastNoteID)
            createNote(note)
        }

        fun deleteAppointment(rut: String) {
            val file = getFile(rut, APPOINTMENT_PATH)
            if (file.exists()) {
                file.delete()
            }
        }

        fun hasAppointment(rut: String): Boolean {
            val file = getFile(rut, APPOINTMENT_PATH)
            return file.exists()
        }

        fun editAppointment(appointment: Appointment) {
            deleteAppointment(appointment.getPatient().getRUT())
            createAppointment(appointment)
        }

        fun getAppointmentByRut(rut: String): Appointment {
            val file = getFile(rut, APPOINTMENT_PATH)

            if (file.exists()) {
                val lines = file.readLines()
                val date = lines[0].removePrefix("Date: ")
                val time = lines[1].removePrefix("Time: ")
                return Appointment(date = date, time = time, patient = getPatientByRut(rut))
            } else {
                return Appointment(
                        date = "The patient don't have an appointment",
                        time = "",
                        patient = Patient()
                )
            }
        }

        fun getPatientByRut(id: String): Patient {
            val fileName = "$id.txt"
            val patientFile = File(Paths.get(PATIENT_PATH, fileName).toString())
            val lines = patientFile.readLines()
            return Patient(
                    name = lines[0].removePrefix("Patient's Names: "),
                    surname = lines[1].removePrefix("Patient's Surnames: "),
                    age = lines[2].removePrefix("Age: ").toInt(),
                    RUT = lines[3].removePrefix("RUT: "),
                    date = lines[4].removePrefix("Admission Date: "),
                    medicalFindings =
                            lines.drop(5).joinToString("\n").removePrefix("Medical Findings: ")
            )
        }

        fun editPatient(paciente: Patient) {
            deletePatient(paciente.getRUT())
            createPatient(paciente)
        }

        fun deletePatient(rut: String) {
            val archivo = getFile(rut, PATIENT_PATH)
            if (archivo.exists()) {
                archivo.delete()
            } else {
                println("The file " + rut + "doesn't exist!'")
            }
        }

        private fun getFile(rut: String, path: String): File {
            val fileName = "$rut.txt"
            val fullPath = Paths.get(path, fileName).toString()
            return File(fullPath)
        }

        suspend fun getPatientsList(): LinkedList<Patient> =
                withContext(Dispatchers.IO) {
                    val patientList = LinkedList<Patient>()
                    val patientPath = File(PATIENT_PATH)

                    if (patientPath.exists()) {
                        patientPath.listFiles()?.forEach { archivo ->
                            val lines = archivo.readLines()
                            if (lines.size >= 3) {
                                val name = lines[0].removePrefix("Patient's Names: ")
                                val surname = lines[1].removePrefix("Patient's Surnames: ")
                                val age = lines[2].removePrefix("Age: ").toIntOrNull()
                                val rut = lines[3].removePrefix("RUT: ")
                                val date = lines[4].removePrefix("Admission Date: ")
                                val medicalFindings =
                                        lines.drop(5)
                                                .joinToString("\n")
                                                .removePrefix("Medical Findings: ")

                                if (age != null) {
                                    patientList.add(
                                            Patient(
                                                    name = name,
                                                    surname = surname,
                                                    age = age,
                                                    RUT = rut,
                                                    date = date,
                                                    medicalFindings = medicalFindings
                                            )
                                    )
                                }
                            }
                        }
                    }

                    return@withContext patientList
                }

        suspend fun getAppointmentsList(): LinkedList<Appointment> =
                withContext(Dispatchers.IO) {
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                    val appointmentsList = LinkedList<Appointment>()
                    val appointmentPath = File(APPOINTMENT_PATH)

                    if (appointmentPath.exists()) {
                        appointmentPath.listFiles()?.forEach { appointment ->
                            val lines = appointment.readLines()
                            val date = lines[0].removePrefix("Date: ")
                            val time = lines[1].removePrefix("Time: ")
                            val rut = lines[2].removePrefix("RUT: ")

                            appointmentsList.add(
                                    Appointment(time = time, date = date, getPatientByRut(rut))
                            )
                        }
                    }

                    appointmentsList.forEach { appointment ->
                        val dateTimeString = "${appointment.getDate()} ${appointment.getTime()}"
                        val appointmentDateTime = LocalDateTime.parse(dateTimeString, formatter)
                        appointmentDateTime.isAfter(LocalDateTime.now())
                    }

                    return@withContext appointmentsList
                }

        suspend fun getNotesList(): LinkedList<Note> =
                withContext(Dispatchers.IO) {
                    val notesList = LinkedList<Note>()
                    val notesPath = File(NOTES_PATH)

                    if (notesPath.exists()) {
                        notesPath.listFiles()?.forEach { note ->
                            val lines = note.readLines()
                            notesList.add(
                                    Note(
                                            title = lines[0].removePrefix("Title: "),
                                            date = lines[1].removePrefix("Date: "),
                                            description =
                                                    lines.drop(2)
                                                            .joinToString("\n")
                                                            .removePrefix("Description: ")
                                    )
                            )
                        }
                    }

                    return@withContext notesList
                }
    }
}
