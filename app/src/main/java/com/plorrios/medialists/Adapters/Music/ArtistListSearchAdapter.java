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
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.ArtistList;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class ArtistListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static ArtistListSearchAdapter.IClickListener onClk;
    private ArrayList<Artist> artists;
    Context context;
    boolean isPLAYING;
    MediaPlayer mp;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class ArtistListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public AdView adView;

        public ArtistListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
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
    public ArtistListSearchAdapter(ArtistList myArtistList, ArtistListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");;
        artists = new ArrayList<Artist>();
        artists.addAll(Arrays.asList(myArtistList.GetArtist().getItems()));
        onClk = monClk;
        context = mcontext;
        isPLAYING = false;
    }

    public ArtistListSearchAdapter(ArrayList<Artist> myArtistList, ArtistListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");;
        artists = myArtistList;
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
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_list, parent, false);
            final ArtistListSearchAdapter.ArtistListViewHolder vh = new ArtistListSearchAdapter.ArtistListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new ArtistListSearchAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Artist getArtist(int position){
        return artists.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            int RealPosition = getRealPosition(position);
            final ArtistListSearchAdapter.ArtistListViewHolder holder = (ArtistListSearchAdapter.ArtistListViewHolder) mholder;
            final Artist artist = artists.get(RealPosition);

            holder.textView.setText(context.getString(R.string.ItemTitle,artist.getName()));
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

        } else {
            ArtistListSearchAdapter.AdViewHolder holder = (ArtistListSearchAdapter.AdViewHolder) mholder;
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
        if (LIST_AD_DELTA > 0 && artists.size() >= LIST_AD_DELTA - 1) {
            additionalContent = artists.size() / (LIST_AD_DELTA-1);
        }
        return artists.size() + additionalContent;
    }

    public void addArtist(ArtistList myArtistList) {

        artists.addAll(Arrays.asList(myArtistList.GetArtist().getItems()));

    }

    public void addArtist(Artist Artist) {
        artists.add(Artist);
    }


    public void clear(){
        artists.clear();
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
