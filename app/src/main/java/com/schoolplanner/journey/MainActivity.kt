package com.schoolplanner.journey

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolplanner.journey.data.BackupManager
import com.schoolplanner.journey.data.JourneyData
import com.schoolplanner.journey.data.JourneyDataStore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JourneyApp()
        }
    }
}


// =====================================================
// COLORS
// =====================================================

val DeepBlue = Color(0xFF06152B)
val DarkBlue = Color(0xFF0A2342)
val CardBlue = Color(0xFF0D2D52)
val BrightBlue = Color(0xFF42A5F5)
val LightBlue = Color(0xFF90CAF9)
val SoftBlue = Color(0xFFB8D5F2)
val MutedBlue = Color(0xFF6E91B8)


// =====================================================
// JOURNEY APP
// =====================================================

@Composable
fun JourneyApp() {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // =================================================
    // DATA STORE
    // =================================================

    val dataStore = remember {
        JourneyDataStore(
            context.applicationContext
        )
    }

    val savedData by dataStore.data.collectAsState(
        initial = JourneyData()
    )

    val skills = savedData.skills
    val skillLevels = savedData.levels


    // =================================================
    // SYSTEM BACK BUTTON
    // =================================================

    BackHandler {

        when (currentScreen) {

            "home" -> {
                // На головному екрані нічого не робимо.
            }

            "settings" -> {
                currentScreen = "home"
            }

            "about" -> {
                currentScreen = "settings"
            }

            "skills" -> {
                currentScreen = "settings"
            }

            "addSkill" -> {
                currentScreen = "skills"
            }

            "timer" -> {
                currentScreen = "home"
            }

            "survey" -> {
                currentScreen = "home"
            }
        }
    }


    // =================================================
    // SURVEY SYSTEM
    // =================================================

    val surveyPreferences = remember {

        context.getSharedPreferences(
            "journey_survey",
            Context.MODE_PRIVATE
        )
    }

    var showSurvey by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        val nextSurveyTime =
            surveyPreferences.getLong(
                "next_survey_time",
                0L
            )

        if (nextSurveyTime == 0L) {

            val fifteenDays =
                15L *
                        24L *
                        60L *
                        60L *
                        1000L

            val firstSurveyTime =
                System.currentTimeMillis() +
                        fifteenDays

            surveyPreferences
                .edit()
                .putLong(
                    "next_survey_time",
                    firstSurveyTime
                )
                .apply()

            showSurvey = false

        } else {

            showSurvey =
                System.currentTimeMillis() >=
                        nextSurveyTime
        }
    }


    // =================================================
    // EXPORT BACKUP
    // =================================================

    val exportLauncher =
        rememberLauncherForActivityResult(

            ActivityResultContracts.CreateDocument(
                "application/json"
            )

        ) { uri ->

            if (uri != null) {

                scope.launch {

                    try {

                        BackupManager.exportBackup(
                            context = context,
                            uri = uri,
                            data = savedData
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }
        }


    // =================================================
    // IMPORT BACKUP
    // =================================================

    val importLauncher =
        rememberLauncherForActivityResult(

            ActivityResultContracts.OpenDocument()

        ) { uri ->

            if (uri != null) {

                scope.launch {

                    try {

                        val importedData =
                            BackupManager.importBackup(
                                context = context,
                                uri = uri
                            )

                        dataStore.saveData(
                            skills = importedData.skills,
                            levels = importedData.levels
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }
        }


    // =================================================
    // THEME
    // =================================================

    MaterialTheme(

        colorScheme = darkColorScheme(

            primary = BrightBlue,
            secondary = LightBlue,
            background = DeepBlue,
            surface = CardBlue
        )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {


            // =================================================
            // MAIN NAVIGATION
            // =================================================

            when (currentScreen) {

                // =================================================
                // HOME
                // =================================================

                "home" -> {

                    HomeScreen(

                        skills = skills,

                        skillLevels = skillLevels,

                        onSettings = {
                            currentScreen = "settings"
                        },

                        onInvestTime = {
                            currentScreen = "timer"
                        }
                    )
                }


                // =================================================
                // SETTINGS
                // =================================================

                "settings" -> {

                    SettingsScreen(

                        onBack = {
                            currentScreen = "home"
                        },

                        onSkills = {
                            currentScreen = "skills"
                        },

                        onAbout = {
                            currentScreen = "about"
                        },

                        onExportBackup = {

                            exportLauncher.launch(
                                "journey_backup.json"
                            )
                        },

                        onImportBackup = {

                            importLauncher.launch(

                                arrayOf(
                                    "application/json",
                                    "text/plain"
                                )
                            )
                        }
                    )
                }


                // =================================================
                // ABOUT
                // =================================================

                "about" -> {

                    AboutScreen(

                        onBack = {
                            currentScreen = "settings"
                        }
                    )
                }


                // =================================================
                // SKILLS
                // =================================================

                "skills" -> {

                    SkillsScreen(

                        skills = skills,

                        skillLevels = skillLevels,

                        onBack = {
                            currentScreen = "settings"
                        },

                        onAddSkill = {
                            currentScreen = "addSkill"
                        },

                        onDeleteSkill = { skill ->

                            scope.launch {

                                dataStore.deleteSkill(
                                    skill
                                )
                            }
                        }
                    )
                }


                // =================================================
                // ADD SKILL
                // =================================================

                "addSkill" -> {

                    AddSkillScreen(

                        onBack = {
                            currentScreen = "skills"
                        },

                        onCreate = { skillName ->

                            val cleanName =
                                skillName.trim()

                            if (
                                cleanName.isNotEmpty() &&
                                !skills.contains(cleanName)
                            ) {

                                scope.launch {

                                    dataStore.addSkill(
                                        cleanName
                                    )
                                }
                            }

                            currentScreen = "skills"
                        }
                    )
                }


                // =================================================
                // INVEST TIME
                // =================================================

                "timer" -> {

                    InvestTimeScreen(

                        skills = skills,

                        skillLevels = skillLevels,

                        onAddTime = {
                                skill,
                                minutes ->

                            scope.launch {

                                dataStore.addTime(
                                    skill,
                                    minutes
                                )
                            }
                        },

                        onBack = {
                            currentScreen = "home"
                        }
                    )
                }


                // =================================================
                // SURVEY
                // =================================================

                "survey" -> {

                    SurveyScreen(

                        onCancel = {
                            currentScreen = "home"
                        },

                        onSurveyOpened = {

                            val fifteenDays =
                                15L *
                                        24L *
                                        60L *
                                        60L *
                                        1000L

                            val nextSurveyTime =
                                System.currentTimeMillis() +
                                        fifteenDays

                            surveyPreferences
                                .edit()
                                .putLong(
                                    "next_survey_time",
                                    nextSurveyTime
                                )
                                .apply()

                            showSurvey = false

                            currentScreen = "home"
                        }
                    )
                }
            }


            // =================================================
            // AUTOMATIC SURVEY
            // =================================================

            if (
                showSurvey &&
                currentScreen == "home"
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Color.Black.copy(
                                    alpha = 0.65f
                                )
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    SurveyScreen(

                        onCancel = {

                            showSurvey = false
                        },

                        onSurveyOpened = {

                            val fifteenDays =
                                15L *
                                        24L *
                                        60L *
                                        60L *
                                        1000L

                            val nextSurveyTime =
                                System.currentTimeMillis() +
                                        fifteenDays

                            surveyPreferences
                                .edit()
                                .putLong(
                                    "next_survey_time",
                                    nextSurveyTime
                                )
                                .apply()

                            showSurvey = false
                        }
                    )
                }
            }
        }
    }
}


// =====================================================
// HOME SCREEN
// =====================================================

@Composable
fun HomeScreen(

    skills: List<String>,

    skillLevels: Map<String, Double>,

    onSettings: () -> Unit,

    onInvestTime: () -> Unit
) {

    val background =
        Brush.verticalGradient(

            colors = listOf(

                DeepBlue,

                DarkBlue,

                Color(0xFF0B315A)
            )
        )


    val journeyLevel =

        if (skills.isEmpty()) {

            0.0

        } else {

            skills
                .map {
                    skillLevels[it] ?: 0.0
                }
                .average()
        }


    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(background)
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // =================================================
            // HEADER
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text = "JOURNEY",

                    color = Color.White,

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text = "⚙",

                    color = LightBlue,

                    fontSize = 26.sp,

                    modifier =
                        Modifier.clickable {

                            onSettings()
                        }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )


            // =================================================
            // JOURNEY LEVEL
            // =================================================

            Text(

                text = "YOUR JOURNEY",

                color = LightBlue,

                fontSize = 14.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    "LEVEL %.2f".format(
                        journeyLevel
                    ),

                color =
                    Color.White,

                fontSize =
                    42.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            val progress =
                (journeyLevel % 1.0)
                    .toFloat()


            LinearProgressIndicator(

                progress = {
                    progress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),

                color =
                    BrightBlue,

                trackColor =
                    Color(0xFF173A60)
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // =================================================
            // YOUR SKILLS
            // =================================================

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                shape =
                    RoundedCornerShape(24.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            CardBlue
                    )
            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                ) {

                    Text(

                        text =
                            "YOUR SKILLS",

                        color =
                            LightBlue,

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    if (skills.isEmpty()) {

                        Column(
                            modifier =
                                Modifier.fillMaxSize(),
                            verticalArrangement =
                                Arrangement.Center,
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(

                                text =
                                    "No skills yet",

                                color =
                                    Color.White,

                                fontSize =
                                    17.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )


                            Text(

                                text =
                                    "Create your first skill in Settings.",

                                color =
                                    MutedBlue,

                                fontSize =
                                    13.sp
                            )
                        }

                    } else {

                        // =================================================
                        // UNLIMITED SCROLLABLE SKILLS
                        // =================================================

                        LazyColumn(

                            modifier =
                                Modifier.fillMaxSize(),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    4.dp
                                ),

                            contentPadding =
                                PaddingValues(
                                    bottom = 8.dp
                                )
                        ) {

                            items(

                                items = skills,

                                key = {
                                    it
                                }

                            ) { skill ->

                                Row(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 8.dp
                                            ),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Text(

                                        text =
                                            skill,

                                        color =
                                            Color.White,

                                        fontSize =
                                            16.sp,

                                        modifier =
                                            Modifier.weight(
                                                1f
                                            )
                                    )


                                    Text(

                                        text =
                                            "Lv. %.2f".format(

                                                skillLevels[
                                                    skill
                                                ] ?: 0.0
                                            ),

                                        color =
                                            LightBlue,

                                        fontSize =
                                            13.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // =================================================
            // INVEST TIME
            // =================================================

            Button(

                onClick =
                    onInvestTime,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(62.dp),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            BrightBlue
                    )
            ) {

                Text(

                    text =
                        "INVEST TIME",

                    color =
                        Color.White,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =================================================
            // FOOTER
            // =================================================

            Text(

                text =
                    "Created by Holovatenko Artem Andriiovych · 2026",

                color =
                    MutedBlue,

                fontSize =
                    10.sp
            )
        }
    }
}
