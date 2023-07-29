# MediOrganizer
A simple medical organizer created with Jetpack Compose

This is a Kotlin-based proyect, trying to help and organizate medical portfolio.

You can create new Patients with their basic data like name, surname, age, ID (in this case, RUT as an ID) and medical findings about the current Patient.

Also, you can create Appointments from the current month and day, until the last month and day of the year, with a schedule from 8 am to 2 pm. Each Appointment is asociated to one single Patient, and takes 1 hour.

Finally, you will be able to create quick notes in the Home of the app. That is a quick note-taking functionality, for some basics tasks.

The navigation between the screens of the app, is working by a simple Stack, in the NavGraph class, because Compose, as of today, still doesn't provide official support for screen navigation.




