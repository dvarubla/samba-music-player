package com.dvarubla.sambamusicplayer.player;

public class SongMData {
    private String _artist;
    private String _trackName;
    private String _album;
    private long _duration;
    SongMData(String artist, String trackName, String album, long duration){
        _artist = artist;
        _trackName = trackName;
        _album = album;
        _duration = duration;
    }

    public String getAlbum(){
        return _album;
    }

    public String getArtist() {
        return _artist;
    }

    public String getTrackName() {
        return _trackName;
    }

    public long getDuration() {
        return _duration;
    }
}
