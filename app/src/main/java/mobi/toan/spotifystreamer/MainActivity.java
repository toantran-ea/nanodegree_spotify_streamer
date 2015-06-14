package mobi.toan.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;

import java.util.List;

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
    private RecyclerView mArtistRecyclerView;
    private EditText mSearchTextView;
    private TextView mStatusTextView;
    private SearchRecyclerViewAdapter mAdapter;
    private Toast mToast;
    private String mPreviousQuery = "";

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
            mAdapter.updateDatasource(DataStore.getArtists());
            DataStore.resetArtist();
            toggleStatus(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.QUERY, mPreviousQuery);
        DataStore.setArtists(mAdapter.getData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_country:
                openCountry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openCountry() {
        final CountryPicker countryPicker = CountryPicker.newInstance(getString(R.string.select_country));
        countryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String countryName, String countryCode) {
                saveCountry(countryCode, countryName);
                countryPicker.dismiss();
            }
        });
        countryPicker.show(getSupportFragmentManager(), getString(R.string.select_country));
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

    /**
     * Toggles visibility of status label and recyclerview.
     * @param statusLabelVisible
     */
    private void toggleStatus(final boolean statusLabelVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusLabelVisible) {
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

    private void saveCountry(String countryCode, String countryName) {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PREF_COUNTRY_CODE, countryCode);
        editor.putString(Constants.PREF_COUNTRY_NAME, countryName);
        editor.commit();
    }
}
