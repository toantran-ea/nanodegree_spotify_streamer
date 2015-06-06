package mobi.toan.spotifystreamer.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import mobi.toan.spotifystreamer.R;

/**
 * Created by toan on 6/3/15.
 */
public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder> {
    private CopyOnWriteArrayList<Artist> mArtists;
    private Context mContext;

    public SearchRecyclerViewAdapter(Context context, List<Artist> artists) {
        mArtists = new CopyOnWriteArrayList<>();
        mContext = context;
        if(artists != null) {
            updateDatasource(artists);
        }
    }

    public void updateDatasource(List<Artist> artists) {
        mArtists.clear();
        if(artists != null) {
            mArtists.addAll(artists);
        }
        notifyDataSetChanged();
    }

    public void reset() {
        mArtists.clear();
        notifyDataSetChanged();
    }

    public List<Artist> getData() {
        return mArtists;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_search_result_item, parent, false);
        return new SearchViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return mArtists == null ? 0 : mArtists.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        Artist artist = mArtists.get(position);
        holder.mArtistTextView.setText(artist.name);
        if(artist.images.size() > 0) {
            Picasso.with(mContext).load(artist.images.get(0).url).into(holder.mThumbImageView);
        }
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        public ImageView mThumbImageView;
        public TextView mArtistTextView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            mThumbImageView = (ImageView) itemView.findViewById(R.id.artist_image);
            mArtistTextView = (TextView) itemView.findViewById(R.id.artist_name);
        }
    }
}
