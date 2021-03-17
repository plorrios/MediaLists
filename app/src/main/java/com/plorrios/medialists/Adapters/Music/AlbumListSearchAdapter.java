package com.plorrios.medialists.Adapters.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.plorrios.medialists.Objects.Music.Album;
import com.plorrios.medialists.Objects.Music.AlbumList;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class AlbumListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static AlbumListSearchAdapter.IClickListener onClk;
    private ArrayList<Album> albums;
    Context context;
    boolean isPLAYING;
    MediaPlayer mp;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class AlbumListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;

        public AlbumListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
            textView2 = (TextView) view.findViewById(R.id.textViewAuthorAlbumListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewAlbumListItem);
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
    public AlbumListSearchAdapter(AlbumList myAlbumList, AlbumListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");;
        albums = new ArrayList<Album>();
        albums.addAll(Arrays.asList(myAlbumList.GetAlbum().getItems()));
        onClk = monClk;
        context = mcontext;
        isPLAYING = false;
    }

    public AlbumListSearchAdapter(ArrayList<Album> myAlbumList, AlbumListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");;
        albums = myAlbumList;
        onClk = monClk;
        context = mcontext;
        isPLAYING = false;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_list, parent, false);
            final AlbumListSearchAdapter.AlbumListViewHolder vh = new AlbumListSearchAdapter.AlbumListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new AlbumListSearchAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Album getAlbum(int position){
        return albums.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            int RealPosition = getRealPosition(position);
            final AlbumListSearchAdapter.AlbumListViewHolder holder = (AlbumListSearchAdapter.AlbumListViewHolder) mholder;
            final Album Album = albums.get(RealPosition);

            holder.textView.setText(context.getString(R.string.ItemTitle,Album.getName()));
            Picasso.get().load(Album.getImages()[0].getUrl()).into(holder.imageView, new Callback() {
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
            Artist[] artists = Album.getArtists();
            for (int i = 0; i < artists.length; i++){
                Artist q = artists[i];
                if (i == artists.length - 1) {
                    artistsString = artistsString + q.getName();
                }else{
                    artistsString = artistsString + q.getName() + ", ";
                }
            }
            holder.textView2.setText(context.getString(R.string.Artist,artistsString));

        } else {
            AlbumListSearchAdapter.AdViewHolder holder = (AlbumListSearchAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null){
                holder.adView.loadAd(adRequest);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && albums.size() >= LIST_AD_DELTA - 1) {
            additionalContent = (albums.size() / (LIST_AD_DELTA-1));
        }
        return albums.size() + additionalContent;
    }

    public void addAlbum(AlbumList myAlbumList) {

        albums.addAll(Arrays.asList(myAlbumList.GetAlbum().getItems()));

    }

    public void addAlbum(Album Album) {
        albums.add(Album);
    }


    public void clear(){
        albums.clear();
    }

    public interface IClickListener { void onClickListener(int position); }

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

