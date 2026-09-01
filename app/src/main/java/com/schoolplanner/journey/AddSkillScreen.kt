package com.schoolplanner.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddSkillScreen(
    onBack: () -> Unit,
    onCreate: (String) -> Unit
) {

    var skillName by remember {
        mutableStateOf("")
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            DeepBlue,
            DarkBlue,
            Color(0xFF0B315A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "← BACK",
                    color = LightBlue
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "ADD SKILL",
                color = Color.White,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = skillName,
                onValueChange = {
                    skillName = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Skill name")
                },
                placeholder = {
                    Text("e.g. Programming")
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrightBlue,
                    unfocusedBorderColor = LightBlue,
                    focusedLabelColor = LightBlue,
                    unfocusedLabelColor = MutedBlue
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    if (skillName.trim().isNotEmpty()) {
                        onCreate(skillName.trim())
                    }
                },
                enabled = skillName.trim().isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightBlue
                )
            ) {
                Text(
                    text = "CREATE SKILL",
                    fontSize = 17.sp
                )
            }
        }
    }
}