package com.prolearn.codecraftfront.data.api

import retrofit2.Response
import retrofit2.http.*

interface CodeQuestApi {

    @GET("health")
    suspend fun getHealth(): Response<String>

    // --- Profile ---
    @GET("profiles/me")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("profiles/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>

    @POST("profiles/me/xp")
    suspend fun addXp(@Body request: AddXpRequest): Response<ProfileResponse>

    @POST("profiles/me/claim-reward")
    suspend fun claimDailyReward(): Response<ProfileResponse>

    @POST("profiles/me/daily-challenge")
    suspend fun completeDailyChallenge(): Response<ProfileResponse>
    
    @POST("profiles/me/elo")
    suspend fun updateElo(@Query("delta") delta: Int): Response<ProfileResponse>

    // --- Friends ---
    @GET("friends")
    suspend fun getFriends(): Response<List<FriendResponse>>

    @GET("friends/requests")
    suspend fun getFriendRequests(): Response<List<FriendResponse>>

    @POST("friends/handle-request")
    suspend fun handleFriendRequest(@Body action: FriendRequestAction): Response<Unit>

    @GET("friends/search")
    suspend fun searchPeople(@Query("query") query: String): Response<List<ProfileResponse>>

    @POST("friends/invite")
    suspend fun inviteFriend(@Query("nickname") nickname: String): Response<Unit>

    @GET("friends/pending-count")
    suspend fun getPendingFriendCount(): Response<Long>

    // --- Levels ---
    @GET("levels/progress/all")
    suspend fun getAllLevelProgress(): Response<List<LevelProgressResponse>>

    @PUT("levels/progress")
    suspend fun updateLevelProgress(@Body request: UpdateLevelProgressRequest): Response<LevelProgressResponse>

    @GET("profiles/me/xp-history")
    suspend fun getXpHistory(@Query("limit") limit: Int = 20): Response<List<XpHistoryResponse>>

    @GET("profiles/leaderboard")
    suspend fun getLeaderboard(@Query("limit") limit: Int = 20): Response<List<LeaderboardResponse>>

    // --- Coding War (Matchmaking) ---
    @POST("war/find-opponent")
    suspend fun findOpponent(@Query("language") language: String): Response<WarMatchResponse>

    @POST("war/report-result")
    suspend fun reportWarResult(@Body request: WarResultRequest): Response<WarResultResponse>

    @POST("war/friend-challenge")
    suspend fun createFriendChallenge(@Body request: CreateFriendChallengeRequest): Response<FriendChallengeResponse>

    @GET("war/friend-challenges/incoming")
    suspend fun getIncomingFriendChallenges(): Response<List<FriendChallengeResponse>>

    @POST("war/friend-challenge/{challengeId}/accept")
    suspend fun acceptFriendChallenge(@Path("challengeId") challengeId: String): Response<WarMatchResponse>

    @GET("war/friend-challenge/{challengeId}/match")
    suspend fun getFriendChallengeMatch(@Path("challengeId") challengeId: String): Response<WarMatchResponse>

    @POST("war/friend-challenge/{challengeId}/decline")
    suspend fun declineFriendChallenge(@Path("challengeId") challengeId: String): Response<Unit>

    // --- Cody AI companion ---
    @POST("companion/chat")
    suspend fun codyChat(@Body request: CodyChatRequest): Response<CodyChatResponse>
}

data class CodyChatRequest(
    val message: String,
    val screenContext: String,
    val history: List<CodyChatTurn> = emptyList(),
)

data class CodyChatTurn(
    val role: String,
    val content: String,
)

data class CodyChatResponse(
    val reply: String,
)

data class WarMatchResponse(
    val matchId: String,
    val opponentName: String,
    val opponentElo: Int,
    val missionId: Int,
    val opponentSolveTimeMs: Long = 30_000L,
    val playerElo: Int = 1000,
    val botOpponent: Boolean = true,
)

data class WarResultRequest(
    val matchId: String,
    val solveTimeMs: Long,
    val won: Boolean,
    val language: String,
)

data class WarResultResponse(
    val won: Boolean,
    val eloDelta: Int,
    val newElo: Int,
    val totalWins: Int,
    val playerTime: String,
    val opponentTime: String,
)

data class CreateFriendChallengeRequest(
    val targetUserId: String,
    val language: String,
)

data class FriendChallengeResponse(
    val challengeId: String,
    val challengerId: String,
    val challengerName: String?,
    val targetId: String,
    val language: String,
    val status: String,
    val challengerElo: Int = 1000,
    val challengerXp: Int = 0,
)
