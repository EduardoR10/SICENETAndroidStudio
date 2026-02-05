package com.example.sicenet.data.remote

object SoapBodyUtils {
    fun getLoginBody(matricula: String, contrasenia: String, tipoUsuario: String = "ALUMNO"): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <accesoLogin xmlns="http://tempuri.org/">
      <strMatricula>$matricula</strMatricula>
      <strContrasenia>$contrasenia</strContrasenia>
      <tipoUsuario>$tipoUsuario</tipoUsuario>
    </accesoLogin>
  </soap:Body>
</soap:Envelope>""".trimIndent()
    }

    fun getPerfilBody(): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
  </soap:Body>
</soap:Envelope>""".trimIndent()
    }
}