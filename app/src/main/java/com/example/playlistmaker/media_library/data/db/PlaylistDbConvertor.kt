package com.example.playlistmaker.media_library.data.db

import com.example.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media_library.domain.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.path,
            playlist.tracksId,
            playlist.trackCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.path,
            playlist.tracksId,
            playlist.trackCount
        )
    }
}
