package mobi.toan.spotifystreamer.utils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by toan on 6/3/15.
 */
public class DataStore {
    private static List<Artist> sArtists;
    private static List<Track> sTopTracks;

    public static void setArtists(List<Artist> storedArtists) {
        resetArtist();
        sArtists = new ArrayList<>();
        sArtists.addAll(storedArtists);
    }

    public static List<Artist> getArtists() {
        return sArtists;
    }

    public static void resetArtist() {
        if (sArtists != null) {
            sArtists.clear();
            sArtists = null;
        }
    }


    public static void setTopTracks(List<Track> tracks) {
        resetTracks();
        sTopTracks = new ArrayList<>();
        sTopTracks.addAll(tracks);
    }

    public static List<Track> getTracks() {
        return sTopTracks;
    }

    public static void resetTracks() {
        if (sTopTracks != null) {
            sTopTracks.clear();
            sTopTracks = null;
        }
    }

}
