package com.example.musicplayer;

import android.os.Parcel;

// Container for information about each audio.
class AudioFile implements android.os.Parcelable {

    private final String _path;
    private final String _title;
    private final String _artist;
    private final String _album;
    private final String _duration;

    public AudioFile(String path, String title, String artist, String album, String duration) {
        this._path = path;
        this._title = title;
        this._artist = artist;
        this._album = album;
        this._duration = duration;
    }

    // TODO, Add a Constructor that takes a Parcel as parameter.
    //  The CREATOR calls that constructor to rebuild our object;
    /** from Parcel, reads back fields IN THE ORDER they were written */
    protected AudioFile(Parcel in) {
        _path = in.readString();
        _title = in.readString();
        _artist = in.readString();
        _album = in.readString();
        _duration = in.readString();
    }

    // TODO, Add a static field called CREATOR to our class, which is
    //  an object implementing the Parcelable.Creator interface;
    /** Static field used to regenerate object, individually or as arrays */
    public static final Creator<AudioFile> CREATOR = new Creator<AudioFile>() {
        @Override
        public AudioFile createFromParcel(Parcel in) {
            return new AudioFile(in);
        }

        @Override
        public AudioFile[] newArray(int size) {
            return new AudioFile[size];
        }
    };

    public String getPath() { return _path; }

    public String getTitle() { return _title; }

    public String getArtist() { return _artist; }

    public String getAlbum() { return _album; }

    public String getDuration() { return _duration; }

    /** Used to give additional hints on how to process the received parcel.*/
    // TODO, - Ignore for now - which in this case does nothing.
    @Override
    public int describeContents() {
        return 0;
    }

    // TODO, Implement Parcelable abstract method writeToParcel, which
    //  takes the current state of the object and writes it to a Parcel;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_path);
        dest.writeString(_title);
        dest.writeString(_artist);
        dest.writeString(_album);
        dest.writeString(_duration);
    }

}
