package com.kirillyemets.catapp.data.usecase

import android.app.DownloadManager
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.getSystemService
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.domain.usecase.MediaInteractor
import kotlinx.coroutines.channels.Channel

class MediaInteractorImpl(
    appContext: Context,
    private val animalRepository: AnimalRepository
) : MediaInteractor {
    private val downloadManager: DownloadManager? = appContext.getSystemService()

    val channel = Channel<Uri>(1)
    init {
        appContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L)
                val uri = downloadManager?.getUriForDownloadedFile(downloadId) ?: return

                channel.trySend(uri)
            }
        }, IntentFilter(ACTION_DOWNLOAD_COMPLETE))
    }

    override suspend fun downloadImageForAnimal(activity: Context, animalId: String): Result<Unit> {
        downloadManager ?: return Result.failure(Throwable("Couldn't get DownloadManager instance"))

        val url = animalRepository.getCachedThumbnail(animalId)?.imageUrl
            ?: animalRepository.getAnimalInfo(animalId).getOrNull()?.imageInfo?.imageUrl
            ?: return Result.failure(Throwable("Couldn't get a url to load the image"))

        val request = DownloadManager.Request(Uri.parse(url)).setTitle("Downloading a cat...")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, "$animalId.jpg"
            )

        downloadManager.enqueue(request)

        val uri = channel.receive()
        val viewIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
        }

        activity.startActivity(viewIntent)

        return Result.success(Unit)
    }



    override suspend fun shareImageForAnimal(activity: Context, animalId: String): Result<Unit> {
        downloadManager ?: return Result.failure(Throwable("Couldn't get DownloadManager instance"))

        val url = animalRepository.getCachedThumbnail(animalId)?.imageUrl
            ?: animalRepository.getAnimalInfo(animalId).getOrNull()?.imageInfo?.imageUrl
            ?: return Result.failure(Throwable("Couldn't get a url to load the image"))

        val request = DownloadManager.Request(Uri.parse(url)).setTitle("Downloading a cat...")

        downloadManager.enqueue(request)

        val uri = channel.receive()

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
        }

        activity.startActivity(Intent.createChooser(shareIntent, null))
        return Result.success(Unit)
    }
}

