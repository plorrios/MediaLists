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
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class RemoveMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static ArrayList<String> RemoveSongsIds;
    ArrayList<Song> songs;
    Context context;
    boolean isPLAYING;
    String use;
    MediaPlayer mp;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class RemoveMusicViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public Button button;
        public ImageView imageView;
        public CheckBox checkBox;

        public RemoveMusicViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewAlbumListItem);
            textView2 = (TextView) view.findViewById(R.id.textViewAuthorAlbumListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewAlbumListItem);
            checkBox = (CheckBox) view.findViewById(R.id.SelectAlbumcheckBox);
            Log.d("CHECKBOX NULL",String.valueOf(checkBox==null));
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
    public RemoveMusicAdapter(ArrayList<Song> myMusicList, Context mcontext) {
        Log.d("Adapter","Adapter Started");

        RemoveSongsIds = new ArrayList<String>();
        songs = myMusicList;
        context = mcontext;
        isPLAYING = false;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_music_list, parent, false);
            final RemoveMusicAdapter.RemoveMusicViewHolder vh = new RemoveMusicAdapter.RemoveMusicViewHolder(firstView);
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
              final RemoveMusicAdapter.RemoveMusicViewHolder holder = (RemoveMusicAdapter.RemoveMusicViewHolder) mholder;
              final Song song = songs.get(RealPosition);

              holder.textView.setText(context.getString(R.string.ItemTitle, song.getName()));
              Picasso.get().load(song.getAlbum().getImages()[0].getUrl()).into(holder.imageView, new Callback() {
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
              Artist[] artists = song.getArtists();
              for (int i = 0; i < artists.length; i++) {
                  Artist q = artists[i];
                  if (i == artists.length - 1) {
                      artistsString = artistsString + q.getName();
                  } else {
                      artistsString = artistsString + q.getName() + ", ";
                  }
              }
              holder.textView2.setText(context.getString(R.string.Artist, artistsString));

              holder.button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      onRadioClick(song.getPreview());
                  }
              });

              holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                      if (isChecked) {
                          RemoveSongsIds.add(songs.get(RealPosition).getId());
                      } else {
                          RemoveSongsIds.remove(songs.get(RealPosition).getId());
                      }

                  }
              });

          } else {
              RemoveMusicAdapter.AdViewHolder holder = (RemoveMusicAdapter.AdViewHolder) mholder;
              AdRequest adRequest = new AdRequest.Builder().build();
              if (adRequest != null && holder.adView != null) {
                  holder.adView.loadAd(adRequest);
              }
          }

    }

    public ArrayList<String> listsToRemove() {
        return RemoveSongsIds;
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
        if (LIST_AD_DELTA > 0 && songs.size() >= LIST_AD_DELTA - 1) {
            additionalContent = songs.size() / (LIST_AD_DELTA - 1);
        }
        return songs.size() + additionalContent;
    }

    public void addSong(Song song) {
        songs.add(song);
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
