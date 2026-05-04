package com.layerdocs.cli.server

import com.layerdocs.core.log.Log
import com.layerdocs.server.LocalFileWebServer
import com.layerdocs.server.ServerEndpoints
import com.layerdocs.server.message.ServerMessageSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Starter of the web server.
 */
object WebServerStarter {
    /**
     * Starts the web server which serves the specified file and allows for live reloading.
     * @param options options to start the server with
     * @param session session to use to communicate with the web server
     * @param onSessionReady optional callback to invoke when the session is ready
     */
    fun start(
        options: WebServerOptions,
        session: ServerMessageSession,
        onSessionReady: suspend () -> Unit = { },
    ) = runBlocking {
        // Asynchronously start the web server.
        launch(Dispatchers.IO) {
            LocalFileWebServer(options.targetFile).start(options.port, wait = false)
            session.init(onSessionReady)
        }

        Log.info("Started web server on port ${options.port}")

        // Optionally the target file in the browser.
        options.browserLauncher?.let {
            try {
                val endpoint = if (options.preferLivePreviewUrl) ServerEndpoints.LIVE_PREVIEW else ServerEndpoints.ROOT
                it.launchLocal(options.port, endpoint)
            } catch (e: Exception) {
                Log.error("Failed to launch URL via ${it::class.simpleName}: ${e.message}")
                Log.debug(e)
            }
        }
    }
}
