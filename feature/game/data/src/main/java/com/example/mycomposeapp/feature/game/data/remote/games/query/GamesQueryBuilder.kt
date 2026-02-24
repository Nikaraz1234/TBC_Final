package com.example.mycomposeapp.feature.game.data.remote.games.query

object GamesQueryBuilder {
    fun searchGames(search: String, limit: Int = 20): String = """
        fields name, screenshots.image_id;
        search "$search";
        where screenshots != null;
        limit $limit;
    """
    fun popularGamesWithScreenshots(
        limit: Int = 50,
        offset: Int = 0
    ): String = """
        fields 
            name,
            screenshots.image_id,
            rating_count,
            total_rating_count,
            first_release_date,
            genres.name,
            involved_companies.company.name,
            involved_companies.developer;
        where screenshots != null
          & total_rating_count != null
          & total_rating_count > 350
          & first_release_date != null
          & first_release_date < ${System.currentTimeMillis() / 1000};
        sort total_rating_count desc;
        limit $limit;
        offset $offset;
    """

    fun popularGamesWithDescription(
        limit: Int = 50,
        offset: Int = 0
    ): String = """
    fields
        id,
        name,
        summary,
        storyline,
        total_rating_count,
        first_release_date,
        genres.name,
        involved_companies.company.name,
        involved_companies.developer;
    where ((summary != null & summary != "") | (storyline != null & storyline != ""))
      & first_release_date != null
      & first_release_date < ${System.currentTimeMillis() / 1000}
      & total_rating_count != null
      & total_rating_count > 500;
    sort total_rating_count desc;
    limit $limit;
    offset $offset;
""".trimIndent()

}