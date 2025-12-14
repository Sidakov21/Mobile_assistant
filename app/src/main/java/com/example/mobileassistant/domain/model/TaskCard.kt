package com.example.mobileassistant.domain.model

data class TaskCard(
    val id: String = "",
    val title: String,
    val note: String,
    val stats: TaskStats = TaskStats()
)

data class TaskStats(
    val totalCompleted: Int = 0,
    val baseCompleted: Int = 0,
    val techniqueScore: Int = 0,
    val attempts: Int = 0,
    val plancheStats: PlancheStats = PlancheStats(),
    val pullupStats: PullupStats = PullupStats(),
    val edvolg: String = ""
)

data class PlancheStats(
    val closedPlanche: Int = 0,
    val semiClosedPlanche: Int = 0,
    val lotus: Int = 0,
    val domino: Int = 0
)

data class PullupStats(
    val classic: ClassicPullup = ClassicPullup(),
    val carryOut: PullupType = PullupType(),
    val strength: PullupType = PullupType()
)

data class ClassicPullup(
    val lowerGrip: List<Int> = listOf(0, 0, 0),
    val narrowLowerGrip: Int = 0,
    val mixedGrip: Int = 0
)

data class PullupType(
    val grip1: Int = 0,
    val grip2: Int = 0,
    val grip3: Int = 0
)