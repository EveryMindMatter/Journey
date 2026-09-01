package com.schoolplanner.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSkills: () -> Unit,
    onAbout: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit
) {

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
                    color = LightBlue,
                    fontSize = 15.sp
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = "SETTINGS",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            SettingsItem(
                title = "SKILLS",
                description = "Create and manage your skills.",
                onClick = onSkills
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            SettingsItem(
                title = "ABOUT",
                description = "The philosophy behind Journey.",
                onClick = onAbout
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            SettingsItem(
                title = "EXPORT BACKUP",
                description = "Save your Journey progress to a file.",
                onClick = onExportBackup
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            SettingsItem(
                title = "IMPORT BACKUP",
                description = "Restore your Journey progress from a file.",
                onClick = onImportBackup
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "JOURNEY · 2026",
                color = MutedBlue,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBlue
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = description,
                    color = MutedBlue,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "›",
                color = LightBlue,
                fontSize = 28.sp
            )
        }
    }
}