package com.example.playlistmaker.search.data

import com.example.playlistmaker.Resource
import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                val favTrack =
                    withContext(Dispatchers.IO) { appDatabase.trackDao().getTracksById() }
                emit(Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        it.dateAdded,
                        it.trackId in favTrack
                    )
                }))

            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}

