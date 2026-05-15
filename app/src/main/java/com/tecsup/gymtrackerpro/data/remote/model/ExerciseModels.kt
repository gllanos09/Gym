package com.tecsup.gymtrackerpro.data.remote.model

data class ExerciseResponse(
    val count: Int,
    val results: List<ExerciseInfo>
)

data class ExerciseInfo(
    val id: Int,
    val uuid: String,
    val category: ExerciseCategory?,
    val translations: List<ExerciseTranslation>
) {
    fun getNombre(): String {
        val es = translations.firstOrNull { it.language == 4 }
        val en = translations.firstOrNull { it.language == 2 }
        return es?.name ?: en?.name ?: translations.firstOrNull()?.name ?: "Ejercicio #$id"
    }
}

data class ExerciseCategory(
    val id: Int,
    val name: String
)

data class ExerciseTranslation(
    val id: Int,
    val language: Int,
    val name: String,
    val description: String
)