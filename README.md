# MediOrganizer

## A Simple Medical Organizer Created with Jetpack Compose

This is a Kotlin-based project, aimed to help organize a medical portfolio.

You can create new patients with their basic data like name, surname, age, ID (in this case, RUT as an ID), and medical findings about the current patient.

Also, you are able to create appointments from the current month and day, until the last month and day of the year, with a schedule from 8 AM to 2 PM. Each appointment is associated with a single patient and takes 1 hour.

Finally, you will be able to create quick notes on the home screen of the app. This is a quick note-taking functionality, for some basic tasks.

The navigation between the screens of the app is handled by a simple stack in the `NavGraph` class, because Compose, as of today, still doesn't provide official support for screen navigation.

To execute this project, it is necessary that within the `MediOrganizer` directory, you execute the corresponding wrappers. For Linux or Mac, you should run `./gradlew run`.

On the other hand, for Windows, you should run `gradlew.bat`.

> **Note:** This process may take some time due to the necessary installation of the project dependencies.
