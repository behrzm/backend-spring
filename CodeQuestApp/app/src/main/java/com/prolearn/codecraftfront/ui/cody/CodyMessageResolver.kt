package com.prolearn.codecraftfront.ui.cody

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.prolearn.codecraftfront.R
import com.prolearn.codecraftfront.data.UserDisplayNameStore
import com.prolearn.codecraftfront.ui.navigation.CodeQuestDestination

sealed class CodyMessage {
    data class Plain(val text: String) : CodyMessage()
    data class Quote(val author: String, val body: String) : CodyMessage()
}

object CodyMessageResolver {

    fun resolve(route: String, context: Context, languagesQuote: CodyQuotes.Quote?): CodyMessage {
        val userLabel = displayName()
        return when {
            route == CodeQuestDestination.Home.route -> CodyMessage.Plain(
                context.getString(R.string.cody_home_greeting, userLabel),
            )
            route == CodeQuestDestination.Play.route -> CodyMessage.Plain(
                context.getString(R.string.cody_play_prompt),
            )
            route == CodeQuestDestination.Languages.route -> {
                val q = languagesQuote ?: CodyQuotes.random()
                CodyMessage.Quote(q.author, q.text)
            }
            route == CodeQuestDestination.Stats.route -> CodyMessage.Plain(
                context.getString(R.string.cody_stats_encourage),
            )
            route == CodeQuestDestination.Profile.route -> CodyMessage.Plain(
                context.getString(R.string.cody_profile_hint),
            )
            route.startsWith("levels/") -> CodyMessage.Plain(
                context.getString(R.string.cody_levels_hint),
            )
            route.startsWith("mission/") -> CodyMessage.Plain(
                context.getString(R.string.cody_mission_cheer),
            )
            route == CodeQuestDestination.Leaderboard.route -> CodyMessage.Plain(
                context.getString(R.string.cody_leaderboard),
            )
            else -> CodyMessage.Plain(context.getString(R.string.cody_default))
        }
    }

    fun tabIndexForRoute(route: String): Int? = when {
        route == CodeQuestDestination.Home.route -> 0
        route == CodeQuestDestination.Languages.route -> 1
        route == CodeQuestDestination.Play.route -> 2
        route == CodeQuestDestination.Stats.route -> 3
        route == CodeQuestDestination.Profile.route -> 4
        else -> null
    }

    private fun displayName(): String {
        UserDisplayNameStore.displayName.value?.let { return it }
        val user = FirebaseAuth.getInstance().currentUser ?: return "coder"
        user.displayName?.takeIf { it.isNotBlank() }?.let { return it }
        user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }?.let { return it }
        return "coder"
    }
}
