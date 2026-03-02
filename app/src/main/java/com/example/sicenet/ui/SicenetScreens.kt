package com.example.sicenet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sicenet.data.local.entity.CargaAcademicaEntity
import java.text.SimpleDateFormat
import java.util.*

val SicenetGreen = Color(0xFF6AB023)
val SicenetCream = Color(0xFFFDFBF0)
val SicenetOrange = Color(0xFFFF9800)
val SicenetBlueText = Color(0xFF5D6D7E)
val SicenetDivider = Color(0xFF8BC34A)


@Composable
fun SicenetApp(viewModel: SicenetViewModel = viewModel()) {

    if (viewModel.isLoggedIn && viewModel.alumno != null) {
        // Se implementa un Scaffold para tener el menú de navegación en la parte inferior
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = SicenetCream) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = viewModel.currentScreen == "PERFIL",
                        onClick = { viewModel.currentScreen = "PERFIL" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Carga") },
                        label = { Text("Carga") },
                        selected = viewModel.currentScreen == "CARGA",
                        onClick = {
                            viewModel.currentScreen = "CARGA"
                            // Lanzamos el worker de carga al entrar a la pantalla
                            viewModel.obtenerCargaAcademica()
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (viewModel.currentScreen) {
                    "PERFIL" -> ProfileScreen(
                        alumno = viewModel.alumno!!,
                        onLogout = { viewModel.logout() }
                    )
                    "CARGA" -> CargaAcademicaScreen(
                        carga = viewModel.cargaAcademica,
                        ultimaActualizacion = viewModel.ultimaActualizacion
                    )
                }
            }
        }
    } else {
        LoginScreen(
            matricula = viewModel.matricula,
            onMatriculaChange = { viewModel.matricula = it },
            password = viewModel.password,
            onPasswordChange = { viewModel.password = it },
            onLoginClick = { viewModel.onLoginClick() },
            isLoading = viewModel.isLoading,
            errorMsg = viewModel.errorMessage
        )
    }
}


@Composable
fun LoginScreen(
    matricula: String,
    onMatriculaChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    errorMsg: String?
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido a SICENET",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = SicenetBlueText
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = onMatriculaChange,
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = SicenetGreen)
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SicenetGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp)
            }
        }

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun ProfileScreen(alumno: Alumno, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SicenetGreen)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Datos del alumno",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = alumno.fotoUrl,
                contentDescription = "Foto Alumno",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.Person)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = alumno.matricula,
                color = SicenetBlueText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = alumno.carrera,
                color = SicenetBlueText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = alumno.nombre,
                color = SicenetBlueText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = SicenetDivider, thickness = 3.dp)

        val sdf = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val currentTime = sdf.format(Date()).uppercase()

        Text(
            text = currentTime,
            color = SicenetOrange,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )

        Divider(color = SicenetDivider, thickness = 3.dp)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SicenetCream),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "▼ ", color = SicenetGreen, fontSize = 14.sp)
                    Text(
                        text = "Estatus Académico",
                        color = SicenetGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Especialidad", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = alumno.especialidad,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(horizontal = 40.dp))
                Spacer(modifier = Modifier.height(16.dp))

                DatosItem("Cdts. Reunidos:", alumno.creditosAcumulados.toString())
                DatosItem("Cdts. Actuales:", alumno.creditosActuales.toString())
                DatosItem("Sem. Actual:", alumno.semestre.toString())
                DatosItem("Inscrito:", alumno.inscrito)
                DatosItem("Estatus:", alumno.estatus)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Cerrar Sesión")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DatosItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.4f),
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

// Nueva pantalla para la Carga Académica
@Composable
fun CargaAcademicaScreen(carga: List<CargaAcademicaEntity>, ultimaActualizacion: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SicenetGreen)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Carga Académica", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Última act: $ultimaActualizacion", color = Color.White, fontSize = 12.sp)
            }
        }

        if (carga.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando o sin datos...", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(carga) { materia ->
                    MateriaCard(materia)
                }
            }
        }
    }
}

@Composable
fun MateriaCard(materia: CargaAcademicaEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SicenetCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = materia.materia, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SicenetBlueText)
            Text(text = "Docente: ${materia.docente}", fontSize = 14.sp, color = Color.DarkGray)

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Grupo: ${materia.grupo}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Créditos: ${materia.creditos}", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Horario:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (materia.lunes.isNotBlank()) Text("L: ${materia.lunes}", fontSize = 12.sp)
            if (materia.martes.isNotBlank()) Text("M: ${materia.martes}", fontSize = 12.sp)
            if (materia.miercoles.isNotBlank()) Text("Mi: ${materia.miercoles}", fontSize = 12.sp)
            if (materia.jueves.isNotBlank()) Text("J: ${materia.jueves}", fontSize = 12.sp)
            if (materia.viernes.isNotBlank()) Text("V: ${materia.viernes}", fontSize = 12.sp)
        }
    }
}