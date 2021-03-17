package com.plorrios.medialists.Adapters.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MusicListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static MusicListSearchAdapter.IClickListener onClk;
    private ArrayList<Song> songs;
    Context context;
    boolean isPLAYING;
    MediaPlayer mp;
    int AD_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class MusicListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public Button button;
        public AdView adView;

        public MusicListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
            textView2 = (TextView) view.findViewById(R.id.textViewAuthorAlbumListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewAlbumListItem);
            button = (Button) view.findViewById(R.id.itemMusicSelectButton);
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
    public MusicListSearchAdapter(MusicList myMusicList, MusicListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");
        songs = new ArrayList<>();
        songs.addAll(Arrays.asList(myMusicList.GetMusic().getItems()));
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
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list, parent, false);
            final MusicListSearchAdapter.MusicListViewHolder vh = new MusicListSearchAdapter.MusicListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Song getSong(int position){
        return songs.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            final MusicListViewHolder holder = (MusicListViewHolder) mholder;
            final Song song = songs.get(getRealPosition(position));

            holder.textView.setText(context.getString(R.string.ItemTitle,song.getName()));
            Picasso.get().load(song.getAlbum().getImages()[0].getUrl()).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);//Or ScaleType.FIT_CENTER
                }
                @Override
                public void onError(Exception e) {
                    //nothing for now
                    e.printStackTrace();
                }
            });
            holder.imageView.setAdjustViewBounds(true);

            String artistsString = "";
            Artist[] artists = song.getArtists();
            for (int i = 0; i < artists.length; i++){
                Artist q = artists[i];
                if (i == artists.length - 1) {
                    artistsString = artistsString + q.getName();
                }else{
                    artistsString = artistsString + q.getName() + ", ";
                }
            }
            holder.textView2.setText(context.getString(R.string.Artist,artistsString));

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioClick(song.getPreview());
                }
            });
        } else {
            AdViewHolder holder = (AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null){
                holder.adView.loadAd(adRequest);
            }
        }
    }

    public void onRadioClick(String link) {
        if (link == null) {
            Toast.makeText(context, context.getText(R.string.NoPreview), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isPLAYING) {
            isPLAYING = true;
            try {
                mp = new MediaPlayer();
                Log.d("Preview Link",link);
                mp.setDataSource(link);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isPLAYING = false;
            stopPlaying();
            onRadioClick(link);
        }
    }

    public void stopPlaying() {
        if(mp!=null) {
            mp.release();
            mp = null;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && songs.size() >= LIST_AD_DELTA) {
            additionalContent = songs.size() / (LIST_AD_DELTA - 1);
        }
        return songs.size() + additionalContent;
    }

    public void addMusic(MusicList myMusicList) {

        songs.addAll(Arrays.asList(myMusicList.GetMusic().getItems()));

    }

    public void clear(){
        songs.clear();
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
