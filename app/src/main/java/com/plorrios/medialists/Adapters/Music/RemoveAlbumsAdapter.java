package com.plorrios.medialists.Adapters.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.plorrios.medialists.Objects.Music.Album;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RemoveAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static ArrayList<String> RemoveAlbumsIds;
    ArrayList<Album> albums;
    Context context;
    boolean isPLAYING;
    String use;
    MediaPlayer mp;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class RemoveAlbumsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public CheckBox checkBox;

        public RemoveAlbumsViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
            textView2 = (TextView) view.findViewById(R.id.textViewAuthorAlbumListItem);
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
    public RemoveAlbumsAdapter(ArrayList<Album> myAlbums, Context mcontext) {
        Log.d("Adapter","Adapter Started");

        RemoveAlbumsIds = new ArrayList<String>();
        albums = myAlbums;
        context = mcontext;
        isPLAYING = false;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_album_list, parent, false);
            final RemoveAlbumsAdapter.RemoveAlbumsViewHolder vh = new RemoveAlbumsAdapter.RemoveAlbumsViewHolder(firstView);
            return vh;

        } else {
            return new RemoveAlbumsAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {

        if (getItemViewType(position) == CONTENT) {
            final int RealPosition = getRealPosition(position);
            Log.d("position",String.valueOf(position));
            final RemoveAlbumsAdapter.RemoveAlbumsViewHolder holder = (RemoveAlbumsAdapter.RemoveAlbumsViewHolder) mholder;
            final Album album = albums.get(RealPosition);

            holder.textView.setText(context.getString(R.string.ItemTitle, album.getName()));
            Picasso.get().load(album.getImages()[0].getUrl()).into(holder.imageView, new Callback() {
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

            String artistsString = "";
            Artist[] artists = album.getArtists();
            for (int i = 0; i < artists.length; i++) {
                Artist q = artists[i];
                if (i == artists.length - 1) {
                    artistsString = artistsString + q.getName();
                } else {
                    artistsString = artistsString + q.getName() + ", ";
                }
            }
            holder.textView2.setText(context.getString(R.string.Artist, artistsString));

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        RemoveAlbumsIds.add(albums.get(RealPosition).getId());
                    } else {
                        RemoveAlbumsIds.remove(albums.get(RealPosition).getId());
                    }

                }
            });

        } else {
            RemoveAlbumsAdapter.AdViewHolder holder = (RemoveAlbumsAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null) {
                holder.adView.loadAd(adRequest);
            }
        }

    }

    public ArrayList<String> listsToRemove() {
        return RemoveAlbumsIds;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && albums.size() >= LIST_AD_DELTA - 1) {
            additionalContent = albums.size() / (LIST_AD_DELTA - 1);
        }
        return albums.size() + additionalContent;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }


    public void clear(){
        albums.clear();
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
