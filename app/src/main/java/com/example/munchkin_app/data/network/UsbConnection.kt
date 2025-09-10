package com.example.munchkin_app.data.network

import tutorial.PersonKt
import tutorial.person
import tutorial.Pruebas

import java.io.FileOutputStream
import java.nio.ByteBuffer



fun sendPerson(usbOut: FileOutputStream) {
    val myPerson = person {
        id = 1
        name = "Mane"
        email = "mane@example.com"
    }

    // Serializar a bytes
    val bytes = myPerson.toByteArray()

    // Puedes enviar primero la longitud para que el receptor sepa cuánto leer
    val lengthBytes = ByteBuffer.allocate(4).putInt(bytes.size).array()
    usbOut.write(lengthBytes)
    usbOut.write(bytes)
    usbOut.flush()
}





