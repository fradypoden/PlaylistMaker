package com.example.playlistmaker.media_library.data

import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.player.data.db.TrackDbConvertor
import com.example.playlistmaker.player.data.db.entity.TrackEntity
import com.example.playlistmaker.media_library.domain.FavTrRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTrRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavTrRepository {
    override suspend fun insertTrack(track: Track) {
        val trackWithDate = track.copy(dateAdded = System.currentTimeMillis())
        val trackEntity = trackDbConvertor.map(trackWithDate)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override suspend fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(track: List<TrackEntity>): List<Track> {
        return track.map { track -> trackDbConvertor.map(track) }
    }
}
