
package com.schoolplanner.journey.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    private const val BACKUP_VERSION = 2

    /*
     * Creates the backup JSON.
     */
    fun createBackup(
        data: JourneyData
    ): String {

        val root = JSONObject()

        root.put(
            "version",
            BACKUP_VERSION
        )

        root.put(
            "app",
            "Journey"
        )

        /*
         * Save the next survey date/time.
         */
        root.put(
            "nextSurveyAt",
            data.nextSurveyAt
        )

        val skillsArray =
            JSONArray()

        data.skills.forEach { skill ->

            val skillObject =
                JSONObject()

            skillObject.put(
                "name",
                skill
            )

            skillObject.put(
                "level",
                data.levels[skill] ?: 0.0
            )

            skillsArray.put(
                skillObject
            )
        }

        root.put(
            "skills",
            skillsArray
        )

        return root.toString(2)
    }


    /*
     * Saves backup to the selected file.
     */
    fun exportBackup(
        context: Context,
        uri: Uri,
        data: JourneyData
    ) {

        val json =
            createBackup(data)

        context.contentResolver
            .openOutputStream(uri)
            ?.use { outputStream ->

                outputStream.write(
                    json.toByteArray(
                        Charsets.UTF_8
                    )
                )
            }
            ?: throw Exception(
                "Could not open backup file."
            )
    }


    /*
     * Reads and validates a backup file.
     */
    fun importBackup(
        context: Context,
        uri: Uri
    ): JourneyData {

        val jsonText =
            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    inputStream
                        .bufferedReader(
                            Charsets.UTF_8
                        )
                        .readText()

                }
                ?: throw Exception(
                    "Could not read backup file."
                )

        val root =
            JSONObject(jsonText)

        val version =
            root.optInt(
                "version",
                -1
            )

        /*
         * Version 1 backups are still accepted.
         * They simply don't contain survey timing data.
         */
        if (
            version != 1 &&
            version != BACKUP_VERSION
        ) {

            throw Exception(
                "Unsupported backup version."
            )
        }

        val skillsArray =
            root.optJSONArray(
                "skills"
            )
                ?: throw Exception(
                    "Backup contains no skills."
                )

        val skills =
            mutableListOf<String>()

        val levels =
            mutableMapOf<String, Double>()

        for (
        i in 0 until skillsArray.length()
        ) {

            val skillObject =
                skillsArray.getJSONObject(i)

            val name =
                skillObject
                    .optString("name")
                    .trim()

            val level =
                skillObject.optDouble(
                    "level",
                    0.0
                )

            if (name.isNotEmpty()) {

                if (!skills.contains(name)) {

                    skills.add(name)

                    levels[name] =
                        level
                }
            }
        }

        /*
         * Version 1 backups have no survey date.
         */
        val nextSurveyAt =
            root.optLong(
                "nextSurveyAt",
                0L
            )

        return JourneyData(
            skills = skills,
            levels = levels,
            nextSurveyAt = nextSurveyAt
        )
    }
}
