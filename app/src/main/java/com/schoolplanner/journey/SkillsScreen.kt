package com.schoolplanner.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun SkillsScreen(
    skills: List<String>,
    skillLevels: Map<String, Double>,
    onBack: () -> Unit,
    onAddSkill: () -> Unit,
    onDeleteSkill: (String) -> Unit
) {

    /*
     * Skills currently waiting for deletion.
     *
     * Key   = skill name
     * Value = remaining seconds
     */
    var deletionTimers by remember {
        mutableStateOf<Map<String, Int>>(emptyMap())
    }

    /*
     * Countdown.
     *
     * Every second the timer decreases by 1.
     */
    LaunchedEffect(deletionTimers) {

        if (deletionTimers.isNotEmpty()) {

            delay(1000L)

            deletionTimers =
                deletionTimers
                    .mapValues { (_, seconds) ->
                        seconds - 1
                    }
        }
    }

    /*
     * When the 60 seconds are over,
     * delete the skill.
     */
    LaunchedEffect(deletionTimers) {

        val readyToDelete =
            deletionTimers
                .filterValues { it <= 0 }
                .keys

        readyToDelete.forEach { skill ->

            onDeleteSkill(skill)

            deletionTimers =
                deletionTimers - skill
        }
    }

    val background =
        Brush.verticalGradient(
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

            // =========================
            // BACK
            // =========================

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

            // =========================
            // TITLE
            // =========================

            Text(
                text = "SKILLS",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Choose what you want to develop.",
                color = MutedBlue,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            // =========================
            // SKILLS LIST (SCROLLABLE, UNLIMITED)
            // =========================

            if (skills.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBlue
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(25.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "NO SKILLS YET",
                            color = LightBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Add your first skill and start your journey.",
                            color = MutedBlue,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

            } else {

                // The list itself scrolls independently and takes
                // all remaining vertical space, no matter how many
                // skills are added.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    contentPadding =
                        PaddingValues(bottom = 8.dp)
                ) {

                    items(
                        items = skills,
                        key = { it }
                    ) { skill ->

                        val remainingSeconds =
                            deletionTimers[skill]

                        val level =
                            skillLevels[skill] ?: 0.0

                        SkillCard(
                            skillName = skill,
                            level = level,
                            remainingSeconds =
                                remainingSeconds,

                            onDelete = {

                                /*
                                 * Start the 60-second
                                 * thinking period.
                                 */
                                if (remainingSeconds == null) {

                                    deletionTimers =
                                        deletionTimers + (
                                                skill to 60
                                                )
                                }
                            },

                            onCancelDelete = {

                                /*
                                 * Cancel deletion
                                 * and restore the skill.
                                 */
                                deletionTimers =
                                    deletionTimers - skill
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // =========================
            // ADD SKILL (ALWAYS FIXED AT THE BOTTOM)
            // =========================

            Button(
                onClick = onAddSkill,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            BrightBlue
                    )
            ) {

                Text(
                    text = "+ ADD SKILL",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =====================================================
// SKILL CARD
// =====================================================

@Composable
fun SkillCard(
    skillName: String,
    level: Double,
    remainingSeconds: Int?,
    onDelete: () -> Unit,
    onCancelDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBlue
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // =========================
            // SKILL INFORMATION
            // =========================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = skillName,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "LEVEL %.2f"
                                .format(level),

                        color =
                            LightBlue,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                /*
                 * Normal state:
                 * show DELETE.
                 */
                if (remainingSeconds == null) {

                    TextButton(
                        onClick = onDelete
                    ) {

                        Text(
                            text = "DELETE",

                            color =
                                Color(0xFFFF8A80),

                            fontSize =
                                12.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            // =========================
            // DELETE CONFIRMATION
            // =========================

            if (remainingSeconds != null) {

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                Text(
                    text =
                        "Are you sure you want to delete this skill?",

                    color =
                        Color.White,

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Take a moment to think about your decision.",

                    color =
                        MutedBlue,

                    fontSize =
                        12.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =========================
                // COUNTDOWN
                // =========================

                val minutes =
                    remainingSeconds / 60

                val seconds =
                    remainingSeconds % 60

                val timeText =
                    String.format(
                        "%02d:%02d",
                        minutes,
                        seconds
                    )

                Text(
                    text =
                        "The skill will be deleted in $timeText",

                    color =
                        Color(0xFFFFAB91),

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Medium
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                LinearProgressIndicator(
                    progress = {
                        remainingSeconds / 60f
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(5.dp),

                    color =
                        BrightBlue,

                    trackColor =
                        Color(0xFF173A60)
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                // =========================
                // CANCEL
                // =========================

                OutlinedButton(
                    onClick = onCancelDelete,

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                LightBlue
                        )
                ) {

                    Text(
                        text = "CANCEL DELETION",
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}