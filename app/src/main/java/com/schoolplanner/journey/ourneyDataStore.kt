package com.schoolplanner.journey.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.journeyDataStore by preferencesDataStore(
    name = "journey_data"
)

data class JourneyData(
    val skills: List<String> = emptyList(),
    val levels: Map<String, Double> = emptyMap(),
    val nextSurveyAt: Long = 0L
)

class JourneyDataStore(
    private val context: Context
) {

    private companion object {

        val SKILLS =
            stringSetPreferencesKey("skills")

        val LEVELS =
            stringPreferencesKey("levels")

        val NEXT_SURVEY_AT =
            stringPreferencesKey("next_survey_at")
    }

    val data: Flow<JourneyData> =
        context.journeyDataStore.data.map { preferences ->

            val skills =
                preferences[SKILLS]
                    ?.toList()
                    ?.sorted()
                    ?: emptyList()

            val levels =
                decodeLevels(
                    preferences[LEVELS]
                )

            val nextSurveyAt =
                preferences[NEXT_SURVEY_AT]
                    ?.toLongOrNull()
                    ?: 0L

            JourneyData(
                skills = skills,
                levels = levels,
                nextSurveyAt = nextSurveyAt
            )
        }

    suspend fun saveData(
        skills: List<String>,
        levels: Map<String, Double>,
        nextSurveyAt: Long = 0L
    ) {

        context.journeyDataStore.edit { preferences ->

            preferences[SKILLS] =
                skills.toSet()

            preferences[LEVELS] =
                encodeLevels(levels)

            preferences[NEXT_SURVEY_AT] =
                nextSurveyAt.toString()
        }
    }

    suspend fun addSkill(
        skill: String
    ) {

        context.journeyDataStore.edit { preferences ->

            val currentSkills =
                preferences[SKILLS]
                    ?.toMutableSet()
                    ?: mutableSetOf()

            currentSkills.add(skill)

            preferences[SKILLS] =
                currentSkills

            val currentLevels =
                decodeLevels(
                    preferences[LEVELS]
                ).toMutableMap()

            if (!currentLevels.containsKey(skill)) {

                currentLevels[skill] = 0.0

                preferences[LEVELS] =
                    encodeLevels(currentLevels)
            }
        }
    }

    suspend fun deleteSkill(
        skill: String
    ) {

        context.journeyDataStore.edit { preferences ->

            val currentSkills =
                preferences[SKILLS]
                    ?.toMutableSet()
                    ?: mutableSetOf()

            currentSkills.remove(skill)

            preferences[SKILLS] =
                currentSkills

            val currentLevels =
                decodeLevels(
                    preferences[LEVELS]
                ).toMutableMap()

            currentLevels.remove(skill)

            preferences[LEVELS] =
                encodeLevels(currentLevels)
        }
    }

    suspend fun addTime(
        skill: String,
        minutes: Int
    ) {

        context.journeyDataStore.edit { preferences ->

            val currentLevels =
                decodeLevels(
                    preferences[LEVELS]
                ).toMutableMap()

            val currentLevel =
                currentLevels[skill] ?: 0.0

            /*
             * 30 minutes = +0.05
             * 60 minutes = +0.10
             */
            val addedLevel =
                minutes / 600.0

            currentLevels[skill] =
                currentLevel + addedLevel

            preferences[LEVELS] =
                encodeLevels(currentLevels)
        }
    }

    suspend fun setNextSurveyAt(
        timestamp: Long
    ) {

        context.journeyDataStore.edit { preferences ->

            preferences[NEXT_SURVEY_AT] =
                timestamp.toString()
        }
    }

    private fun encodeLevels(
        levels: Map<String, Double>
    ): String {

        return levels.entries.joinToString("|") { entry ->

            val encodedName =
                entry.key
                    .replace("\\", "\\\\")
                    .replace("|", "\\|")
                    .replace("=", "\\=")

            "$encodedName=${entry.value}"
        }
    }

    private fun decodeLevels(
        stored: String?
    ): Map<String, Double> {

        if (stored.isNullOrEmpty()) {
            return emptyMap()
        }

        val result =
            mutableMapOf<String, Double>()

        val entries =
            splitEscaped(
                stored,
                '|'
            )

        for (entry in entries) {

            val parts =
                splitEscaped(
                    entry,
                    '='
                )

            if (parts.size != 2) {
                continue
            }

            val name =
                unescape(parts[0])

            val level =
                parts[1].toDoubleOrNull()

            if (
                name.isNotEmpty() &&
                level != null
            ) {

                result[name] =
                    level
            }
        }

        return result
    }

    private fun splitEscaped(
        text: String,
        delimiter: Char
    ): List<String> {

        val result =
            mutableListOf<String>()

        val current =
            StringBuilder()

        var escaped = false

        for (char in text) {

            if (escaped) {

                current.append(char)

                escaped = false

            } else if (char == '\\') {

                escaped = true

            } else if (char == delimiter) {

                result.add(
                    current.toString()
                )

                current.clear()

            } else {

                current.append(char)
            }
        }

        if (escaped) {
            current.append('\\')
        }

        result.add(
            current.toString()
        )

        return result
    }

    private fun unescape(
        text: String
    ): String {

        return text
            .replace("\\=", "=")
            .replace("\\|", "|")
            .replace("\\\\", "\\")
    }
}