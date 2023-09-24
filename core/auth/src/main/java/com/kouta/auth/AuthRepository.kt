package com.kouta.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.datastore.core.DataStore
import com.auth0.android.jwt.JWT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataStore: DataStore<AuthState>,
    private val authService: AuthorizationService
) {
    companion object {
        private const val AUTHORIZATION_EP_URI = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val TOKEN_EP_URI = "https://oauth2.googleapis.com/token"
        private const val CLIENT_ID = "1056937266327-54uei3vprt13b6q3mu4a078i5r3v3oss.apps.googleusercontent.com"
        private const val REDIRECT_PATH = "/google"
        private val SCOPES = listOf(
            "https://www.googleapis.com/auth/youtube",
            "openid",
            "profile"
        )
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var authState: AuthState = AuthState.jsonDeserialize("{}")

    val isLogin = authDataStore.data.map {
        it.isAuthorized
    }
    val user = authDataStore.data.map { authState ->
        authState.idToken?.let {
            val jwt = JWT(it)

            User(
                name = jwt.claims["name"]?.asString() ?: "未設定",
                profileUrl = jwt.claims["picture"]?.asString() ?: "未設定"
            )
        }
    }

    fun login(launcher: ActivityResultLauncher<Intent>) {
        // TODO: pendingIntentの必要性
        // requestの生成
        val request = AuthorizationRequest
            .Builder(
                AuthorizationServiceConfiguration(
                    Uri.parse(AUTHORIZATION_EP_URI),
                    Uri.parse(TOKEN_EP_URI),
                ),
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse("${context.packageName}:$REDIRECT_PATH")
            )
            .setScopes(SCOPES)
            .build()

        // intent処理
        val authIntent = authService.getAuthorizationRequestIntent(request)

        // 結果の取得
        launcher.launch(authIntent)
    }

    fun logout() {
        authState.update(null)
        save(authState)
    }

    suspend fun requestAccessTokenFromIntent(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        authState.update(response, exception)

        if (response != null && exception == null) {
            authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, tokenException ->
                authState.update(tokenResponse, tokenException)
                save(authState)
            }
        }
    }

    suspend fun requestAccessToken(): String? {
        if (authState.needsTokenRefresh) {
            val result = authService.performTokenRequest(authState.createTokenRefreshRequest(), authState.clientAuthentication)
            authState.update(result.first, result.second)
            save(authState)
        }
        return authState.accessToken
    }

    private suspend fun AuthorizationService.performTokenRequest(
        request: TokenRequest,
        clientAuth: ClientAuthentication,
    ) = suspendCancellableCoroutine { continuation ->
        val callback = AuthorizationService.TokenResponseCallback { response, ex ->
            continuation.resumeWith(Result.success(response to ex))
        }

        performTokenRequest(request, clientAuth, callback)
    }


    private fun save(authState: AuthState) = coroutineScope.launch {
        authDataStore.updateData {
            AuthState.jsonDeserialize(authState.jsonSerializeString())
        }
    }
}