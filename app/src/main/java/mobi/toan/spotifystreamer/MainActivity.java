package mobi.toan.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import mobi.toan.spotifystreamer.utils.Constants;
import mobi.toan.spotifystreamer.utils.DataStore;
import mobi.toan.spotifystreamer.views.RecyclerItemClickListener;
import mobi.toan.spotifystreamer.views.SearchRecyclerViewAdapter;
import mobi.toan.spotifystreamer.views.SimpleDividerItemDecoration;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends SpotifyActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mArtistRecyclerView;
    private EditText mSearchTextView;
    private TextView mStatusTextView;
    private SearchRecyclerViewAdapter mAdapter;
    private Toast mToast;
    private String mPreviousQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            mPreviousQuery = savedInstanceState.getString(Constants.QUERY);
            mSearchTextView.setText(mPreviousQuery);
            mSearchTextView.setSelection(mPreviousQuery.length());
            mAdapter.updateDatasource(DataStore.getsArtists());
            DataStore.resetArtist();
            toggleStatus(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", mPreviousQuery);
        DataStore.setsArtists(mAdapter.getData());
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mArtistRecyclerView = (RecyclerView) findViewById(R.id.artist_list_view);
        mArtistRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mArtistRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SearchRecyclerViewAdapter(this, null);
        mArtistRecyclerView.setAdapter(mAdapter);

        mSearchTextView = (EditText) findViewById(R.id.search_text_field);
        mSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    String artistName = v.getText().toString().trim();
                    if (artistName.equals(mPreviousQuery)) {
                        handled = false;
                    } else {
                        mPreviousQuery = artistName;
                        searchForArtist(artistName);
                        handled = true;
                    }
                }
                return handled;
            }
        });
        mStatusTextView = (TextView) findViewById(R.id.label_status);

        mArtistRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Artist artist = mAdapter.getData().get(position);
                openTopTrackActivity(artist);
            }
        }));
    }

    private void searchForArtist(String artistName) {
        toggleStatus(true);
        mAdapter.reset();
        mSpotifyService.searchArtists(artistName, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                toggleStatus(false);
                if (artistsPager.artists.items.size() > 0) {
                    updateRecyclerView(artistsPager.artists.items);
                } else {
                    showMessage(getString(R.string.message_no_artist_found));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                toggleStatus(false);
                showMessage(getString(R.string.message_failed_search_artist));
            }
        });
    }

    private void updateRecyclerView(final List<Artist> artists) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateDatasource(artists);
            }
        });
    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchTextView.getWindowToken(), 0);
    }

    private void toggleStatus(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    mStatusTextView.setVisibility(View.VISIBLE);
                    mArtistRecyclerView.setVisibility(View.GONE);
                } else {
                    mStatusTextView.setVisibility(View.GONE);
                    mArtistRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void openTopTrackActivity(Artist artist) {
        Intent intent = new Intent(this, TopTrackActivity.class);
        intent.putExtra(Constants.SELECTED_ARTIST_ID, artist.id);
        intent.putExtra(Constants.SELECTED_ARTIST, artist.name);
        startActivity(intent);
    }
}
