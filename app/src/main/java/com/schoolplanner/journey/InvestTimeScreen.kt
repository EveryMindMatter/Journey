package com.schoolplanner.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun InvestTimeScreen(
    skills: List<String>,
    skillLevels: Map<String, Double>,
    onAddTime: (String, Int) -> Unit,
    onBack: () -> Unit
) {

    var selectedSkill by remember {
        mutableStateOf<String?>(null)
    }

    var selectedMinutes by remember {
        mutableStateOf(60)
    }

    var showConfirmation by remember {
        mutableStateOf(false)
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

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "INVEST TIME",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "SELECT SKILL",
                color = LightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (skills.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBlue
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "Create a skill first.",
                        color = SoftBlue,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }

            } else {

                skills.forEach { skill ->

                    val selected = selectedSkill == skill

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                selectedSkill = skill
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (selected) BrightBlue
                                else CardBlue
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (selected) "●" else "○",
                                color = Color.White,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = skill,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Lv. %.2f".format(
                                    skillLevels[skill] ?: 0.0
                                ),
                                color = SoftBlue,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "HOW MUCH?",
                color = LightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                TimeButton(
                    text = "30 MINUTES",
                    selected = selectedMinutes == 30,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedMinutes = 30
                }

                Spacer(modifier = Modifier.width(10.dp))

                TimeButton(
                    text = "1 HOUR",
                    selected = selectedMinutes == 60,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedMinutes = 60
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedSkill != null) {
                        showConfirmation = true
                    }
                },
                enabled = selectedSkill != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightBlue,
                    disabledContainerColor = Color(0xFF234665)
                )
            ) {

                Text(
                    text = "ADD TIME",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showConfirmation) {

            ConfirmationDialog(
                onConfirm = {

                    selectedSkill?.let { skill ->
                        onAddTime(
                            skill,
                            selectedMinutes
                        )
                    }

                    showConfirmation = false
                    selectedSkill = null
                },
                onCancel = {
                    showConfirmation = false
                }
            )
        }
    }
}


/* =========================
   TIME BUTTON
   ========================= */

@Composable
fun TimeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier.height(55.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected) BrightBlue
                else CardBlue
        )
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


/* =========================
   CONFIRMATION
   ========================= */

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {

    var secondsLeft by remember {
        mutableStateOf(5)
    }

    LaunchedEffect(Unit) {

        while (secondsLeft > 0) {

            delay(1000)

            secondsLeft--
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,

        containerColor = CardBlue,

        title = {

            Text(
                text = "ARE YOU SURE YOU WORKED HARD ENOUGH?",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Take a moment. Be honest.",
                    color = SoftBlue,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = secondsLeft.toString(),
                    color = LightBlue,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        confirmButton = {

            Button(
                onClick = onConfirm,
                enabled = secondsLeft == 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightBlue,
                    disabledContainerColor = Color(0xFF234665)
                )
            ) {

                Text(
                    text = "YES, I DID",
                    color = Color.White
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onCancel
            ) {

                Text(
                    text = "CANCEL",
                    color = LightBlue
                )
            }
        }
    )
}