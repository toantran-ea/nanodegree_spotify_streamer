package mobi.toan.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import mobi.toan.spotifystreamer.utils.Constants;
import mobi.toan.spotifystreamer.utils.PrefUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final int REQUEST_CODE = 1001;
    private RecyclerView mArtistRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        loginSpotify();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Log.e(TAG, response.getAccessToken());
                PrefUtil.setPref(Constants.SPOTIFY_TOKEN, response.getAccessToken());
            }
        }
    }

    private void loginSpotify() {
        if (TextUtils.isEmpty(PrefUtil.getPref(Constants.SPOTIFY_TOKEN))) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(Credentials.SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        } else {
            Log.e(TAG, "App authenticated with token " + PrefUtil.getPref(Constants.SPOTIFY_TOKEN));
        }
    }

    private void initUI() {
        mArtistRecyclerView = (RecyclerView) findViewById(R.id.artist_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mArtistRecyclerView.setLayoutManager(layoutManager);
    }
}
