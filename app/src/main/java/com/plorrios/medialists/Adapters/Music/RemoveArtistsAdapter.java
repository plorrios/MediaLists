package com.plorrios.medialists.Adapters.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.MusicList;
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RemoveArtistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static ArrayList<String> RemoveArtistIds;
    ArrayList<Artist> artists;
    Context context;
    boolean isPLAYING;
    String use;
    MediaPlayer mp;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class RemoveArtistViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public CheckBox checkBox;

        public RemoveArtistViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewAlbumListItem);
            checkBox = (CheckBox) view.findViewById(R.id.SelectAlbumcheckBox);
            Log.d("CHECKBOX NULL",String.valueOf(checkBox==null));
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdView adView;

        public AdViewHolder(View view) {
            super(view);
            adView = (AdView) view.findViewById(R.id.adView_adItem);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RemoveArtistsAdapter(ArrayList<Artist> myArtistList, Context mcontext) {
        Log.d("Adapter","Adapter Started");

        RemoveArtistIds = new ArrayList<String>();
        artists = myArtistList;
        context = mcontext;
        isPLAYING = false;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_artist_list, parent, false);
            final RemoveArtistsAdapter.RemoveArtistViewHolder vh = new RemoveArtistsAdapter.RemoveArtistViewHolder(firstView);
            return vh;

        } else {
            return new RemoveMusicAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {

        if (getItemViewType(position) == CONTENT) {
            final int RealPosition = getRealPosition(position);
            Log.d("position",String.valueOf(position));
            final RemoveArtistsAdapter.RemoveArtistViewHolder holder = (RemoveArtistsAdapter.RemoveArtistViewHolder) mholder;
            final Artist artist = artists.get(RealPosition);

            holder.textView.setText(context.getString(R.string.ItemTitle, artist.getName()));
            Picasso.get().load(artist.getImages()[0].getUrl()).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//Or ScaleType.FIT_CENTER
                }

                @Override
                public void onError(Exception e) {
                    //nothing for now
                    e.printStackTrace();
                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        RemoveArtistIds.add(artists.get(RealPosition).getId());
                    } else {
                        RemoveArtistIds.remove(artists.get(RealPosition).getId());
                    }

                }
            });

        } else {
            RemoveArtistsAdapter.AdViewHolder holder = (RemoveArtistsAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null) {
                holder.adView.loadAd(adRequest);
            }
        }

    }

    public ArrayList<String> listsToRemove() {
        return RemoveArtistIds;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && artists.size() >= LIST_AD_DELTA - 1) {
            additionalContent = artists.size() / (LIST_AD_DELTA - 1);
        }
        return artists.size() + additionalContent;
    }

    public void addArtist(Artist artist) {
        artists.add(artist);
    }


    public void clear(){
        artists.clear();
    }

    @Override
    public int getItemViewType(int position) {
        position++;
        if (position % LIST_AD_DELTA == 0) {
            return AD;
        }
        return CONTENT;
    }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }
}
