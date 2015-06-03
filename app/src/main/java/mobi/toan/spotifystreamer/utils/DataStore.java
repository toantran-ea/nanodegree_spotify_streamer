package mobi.toan.spotifystreamer.utils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by toan on 6/3/15.
 */
public class DataStore {
    private static List<Artist> sArtists;

    public static void setsArtists(List<Artist> storedArtists) {
        resetArtist();
        sArtists = new ArrayList<>();
        sArtists.addAll(storedArtists);
    }

    public static List<Artist> getsArtists() {
        return sArtists;
    }

    public static void resetArtist() {
        if (sArtists != null) {
            sArtists.clear();
            sArtists = null;
        }
    }
}
