package com.example.learncook.utilidades

import android.annotation.SuppressLint
import android.util.Log
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Email {

    @SuppressLint("SuspiciousIndentation")
    suspend fun sendEmail(to: String, subject: String, body: String): Boolean {
        var bandera = false
            try {
                val properties = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "465")
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.socketFactory.port", "465")
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                }

                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        // Aquí se deben proporcionar las credenciales de correo electrónico desde las que se enviará el correo
                        return PasswordAuthentication("drannet9@gmail.com", "uwsccwxluhzmibic")
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress("drannet9@gmail.com", "Dran Net"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
                message.subject = subject
                message.setText(body)

                Transport.send(message)
                bandera = true
            } catch (e: Exception) {
                Log.e("Envio", "Error al enviar el correo a $to con asunto '$subject'", e)
            }
        return bandera
    }
}
