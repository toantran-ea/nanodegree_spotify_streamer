package mobi.toan.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by toan on 6/5/15.
 */
public class SpotifyActivity extends AppCompatActivity {

    protected  SpotifyApi mSpotifyApi;
    protected  SpotifyService mSpotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApi();
    }

    protected void initApi() {
        mSpotifyApi = new SpotifyApi();
        mSpotifyService = mSpotifyApi.getService();
    }
}
