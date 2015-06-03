package mobi.toan.spotifystreamer;

import android.app.Application;

import mobi.toan.spotifystreamer.utils.PrefUtil;

/**
 * Created by toan on 6/3/15.
 */
public class SpotifyStreamerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtil.initialize(this);
    }
}
