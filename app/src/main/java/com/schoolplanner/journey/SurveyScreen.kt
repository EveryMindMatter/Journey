
package com.schoolplanner.journey

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SurveyScreen(
    onCancel: () -> Unit,
    onSurveyOpened: () -> Unit
) {

    val context = LocalContext.current

    var unlockTime by remember {
        mutableLongStateOf(
            System.currentTimeMillis() + 15_000L
        )
    }

    var remainingSeconds by remember {
        mutableIntStateOf(15)
    }

    /*
     * Real-time 15-second countdown.
     *
     * Because it uses System.currentTimeMillis(),
     * it continues while Google Forms is open.
     */
    LaunchedEffect(unlockTime) {

        while (true) {

            val remaining =
                unlockTime -
                        System.currentTimeMillis()

            if (remaining <= 0L) {

                remainingSeconds = 0

                break
            }

            remainingSeconds =
                ((remaining + 999L) / 1000L)
                    .toInt()

            delay(100L)
        }
    }

    val canCancel =
        remainingSeconds <= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),

        shape =
            RoundedCornerShape(28.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBlue
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "QUICK SURVEY",
                color = LightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text = "Help improve Journey.",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "Take a quick survey about your experience with Journey.",
                color = MutedBlue,
                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            /*
             * TAKE SURVEY
             */
            Button(
                onClick = {

                    /*
                     * Every time TAKE SURVEY is pressed:
                     *
                     * 1. Reset the 15-second timer.
                     * 2. Schedule the next survey for 15 days.
                     * 3. Open Google Forms.
                     */
                    unlockTime =
                        System.currentTimeMillis() +
                                15_000L

                    onSurveyOpened()

                    val intent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://docs.google.com/forms/d/e/1FAIpQLSd89RS9RK906Jwgt5bqDJV8cu5dv7Gwpwp-fnNsnfuB4R5XOQ/viewform?usp=publish-editor"
                            )
                        )

                    context.startActivity(intent)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            BrightBlue
                    )
            ) {

                Text(
                    text = "TAKE SURVEY",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            /*
             * CANCEL
             */
            Button(
                onClick = {

                    if (canCancel) {
                        onCancel()
                    }
                },

                enabled = canCancel,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            if (canCancel) {
                                BrightBlue
                            } else {
                                Color(0xFF425466)
                            },

                        disabledContainerColor =
                            Color(0xFF425466),

                        contentColor =
                            Color.White,

                        disabledContentColor =
                            Color(0xFF9EABB8)
                    )
            ) {

                Text(
                    text =
                        if (canCancel) {
                            "CANCEL"
                        } else {
                            "CANCEL ($remainingSeconds)"
                        },

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}
