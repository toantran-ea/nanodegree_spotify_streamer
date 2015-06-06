package mobi.toan.spotifystreamer.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import mobi.toan.spotifystreamer.R;

/**
 * Created by toan on 6/5/15.
 */
public class TopTrackAdapter extends RecyclerView.Adapter<TopTrackAdapter.ViewHolder> {
    private static final String TAG = TopTrackAdapter.class.getSimpleName();
    private List<Track> mTrackList;
    private Context mContext;

    public TopTrackAdapter(Context context, List<Track> trackList) {
        mContext = context;
        mTrackList = new ArrayList<>();
        if(trackList != null) {
            mTrackList.addAll(trackList);
        }
    }

    public void updateDatasource(List<Track> tracks) {
        Log.e(TAG, "updateDatasource");
        mTrackList.clear();
        if(tracks != null) {
            mTrackList.addAll(tracks);
        }
        notifyDataSetChanged();
    }

    public void reset() {
        mTrackList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_track_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track track = mTrackList.get(position);
        holder.mAlbumNameTextView.setText(track.album.name);
        holder.mTrackNameTextView.setText(track.name);
        List<Image> images = track.album.images;
        String albumThumbImage = "";
        int size = 0;
        if(images != null) {
            for(Image img : images) {
                if (img.width >  size) {
                    size = img.width;
                    albumThumbImage = img.url;
                }
            }
        }

        Picasso.with(mContext).load(albumThumbImage).into(holder.mAvatarImageView);
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTrackNameTextView;
        public TextView mAlbumNameTextView;
        public ImageView mAvatarImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrackNameTextView = (TextView) itemView.findViewById(R.id.track_name);
            mAlbumNameTextView = (TextView) itemView.findViewById(R.id.album_name);
            mAvatarImageView = (ImageView) itemView.findViewById(R.id.artist_image);
        }
    }
}
