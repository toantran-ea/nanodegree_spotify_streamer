package mobi.toan.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import mobi.toan.spotifystreamer.utils.Constants;
import mobi.toan.spotifystreamer.utils.DataStore;
import mobi.toan.spotifystreamer.views.SimpleDividerItemDecoration;
import mobi.toan.spotifystreamer.views.TopTrackAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by toan on 6/5/15.
 */
public class TopTrackActivity extends SpotifyActivity {
    private static final String TAG = TopTrackActivity.class.getSimpleName();
    private TextView mStatusTextView;
    private RecyclerView mTopTrackRecyclerView;
    private TopTrackAdapter mAdapter;
    private String mArtistName;
    private String mArtistId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractData();
        initUI();

        if(savedInstanceState != null) {
            mAdapter.updateDatasource(DataStore.getTracks());
            toggleStatusView(false);
        } else {
            query();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        DataStore.setTopTracks(mAdapter.getData());
    }

    private void extractData() {
        Intent intent = getIntent();
        mArtistId = intent.getStringExtra(Constants.SELECTED_ARTIST_ID);
        mArtistName = intent.getStringExtra(Constants.SELECTED_ARTIST);
    }

    private void query() {
        if (!TextUtils.isEmpty(mArtistId)) {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put(Constants.QUERY_KEY_COUNTRY, Constants.QUERY_VALUE_COUNTRY);
            mSpotifyService.getArtistTopTrack(mArtistId, queryMap, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {
                    updateTopTracks(tracks.tracks);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null) {
                        Log.e(TAG, "query failed " + error.getMessage());
                        updateTopTracks(null);
                    }
                }
            });
        } else {
            mStatusTextView.setText(R.string.message_nothing_to_show);
        }
    }

    private void initUI() {
        setContentView(R.layout.activity_top_track);
        mStatusTextView = (TextView) findViewById(R.id.label_status);
        mStatusTextView.setText(String.format(getString(R.string.message_loading_top_tracks), mArtistName));
        mTopTrackRecyclerView = (RecyclerView) findViewById(R.id.top_track_list_view);
        mTopTrackRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTopTrackRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TopTrackAdapter(this, null);
        mTopTrackRecyclerView.setAdapter(mAdapter);
        toggleStatusView(true);
    }

    private void updateTopTracks(final List<Track> tracks) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tracks == null || tracks.size() == 0) {
                    // show message
                    mStatusTextView.setText(String.format(getString(R.string.message_no_track_for_artist), mArtistName));
                    return;
                } else {
                    toggleStatusView(false);
                }
                mAdapter.updateDatasource(tracks);
            }
        });
    }

    private void toggleStatusView(final boolean statusLabelVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(statusLabelVisible) {
                    mStatusTextView.setVisibility(View.VISIBLE);
                    mTopTrackRecyclerView.setVisibility(View.GONE);
                } else {
                    mStatusTextView.setVisibility(View.GONE);
                    mTopTrackRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
