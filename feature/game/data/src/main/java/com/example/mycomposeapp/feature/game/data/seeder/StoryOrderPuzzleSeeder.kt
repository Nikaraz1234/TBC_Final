package com.example.mycomposeapp.feature.game.data.seeder

object StoryOrderPuzzleSeeder {
    val ALL: List<Map<String, Any?>> = listOf(
        mapOf(
            "date" to "2026-02-23",
            "bookTitle" to "The Vanishing Key",
            "genre" to "Mystery",
            "mainCharacter" to "Elian Morrow",
            "puzzleId" to 1L,
            "timeLimitSec" to 60L,
            "answer" to listOf(1L, 2L, 3L, 4L, 5L, 6L),
            "events" to listOf(
                mapOf("eventId" to 1L, "text" to "A stranger leaves a sealed envelope at the door"),
                mapOf("eventId" to 2L, "text" to "Inside is a coded note pointing to an old library."),
                mapOf("eventId" to 3L, "text" to "Elian finds a hidden compartment behind a shelf."),
                mapOf("eventId" to 4L, "text" to "A rival steals the document before Elian can read it."),
                mapOf("eventId" to 5L, "text" to "Elian sets a trap using a fake copy of the document."),
                mapOf("eventId" to 6L, "text" to "The thief is caught and the real secret is revealed.")
            )
        ),
        mapOf(
            "date" to "2026-02-24",
            "bookTitle" to "Shadows of Blackwood Manor",
            "genre" to "Mystery",
            "mainCharacter" to "Clara Whitmore",
            "puzzleId" to 2L,
            "timeLimitSec" to 60L,
            "answer" to listOf(1L, 2L, 3L, 4L, 5L, 6L),
            "events" to listOf(
                mapOf("eventId" to 1L, "text" to "Clara inherits a remote manor after her uncle's death."),
                mapOf("eventId" to 2L, "text" to "A sealed room is discovered behind a false wall."),
                mapOf("eventId" to 3L, "text" to "A diary hints at a missing heir and a forged will."),
                mapOf("eventId" to 4L, "text" to "A servant is caught burning letters in the courtyard."),
                mapOf("eventId" to 5L, "text" to "Clara finds a passage leading under the main hall."),
                mapOf("eventId" to 6L, "text" to "The hidden heir is revealed and the motive exposed.")
            )
        ),
        mapOf(
            "date" to "2026-02-22",
            "bookTitle" to "The Clockmaker’s Cipher",
            "genre" to "Mystery",
            "mainCharacter" to "Jasper Vale",
            "puzzleId" to 3L,
            "timeLimitSec" to 60L,
            "answer" to listOf(1L, 2L, 3L, 4L, 5L, 6L),
            "events" to listOf(
                mapOf("eventId" to 1L, "text" to "Jasper receives a broken pocket watch in the mail."),
                mapOf("eventId" to 2L, "text" to "A hidden engraving points to an abandoned workshop."),
                mapOf("eventId" to 3L, "text" to "He finds a ledger with coded timestamps and names."),
                mapOf("eventId" to 4L, "text" to "A suspect swaps the ledger for a decoy during a blackout."),
                mapOf("eventId" to 5L, "text" to "Jasper sets a sting using a replicated watch mechanism."),
                mapOf("eventId" to 6L, "text" to "The cipher reveals the mastermind behind the thefts.")
            )
        )
    )
}