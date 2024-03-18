package eu.tutorials.shaproject

data class RequestBody(
    val lecture_id: Int,
    val students: List<Int>
)

