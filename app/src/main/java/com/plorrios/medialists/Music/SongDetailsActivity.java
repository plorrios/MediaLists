package com.plorrios.medialists.Music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.plorrios.medialists.ListsModifications.RemoveAddListsActivity;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Tasks.Music.GetSpotifyTrack;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongDetailsActivity extends AppCompatActivity {

    String id;
    Toolbar toolbar;
    TextView ArtistTextView;
    ImageView AlbumImageView;
    TextView AlbumTextView;
    Song song;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
        }else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);

        id = getIntent().getStringExtra("id");
        getInfo(id);
        toolbar = findViewById(R.id.AlbumDetailsToolbar);
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(R.drawable.toolbar_drawable_dark));
        }
        ArtistTextView = findViewById(R.id.ArtistSongDetailsTextView);
        AlbumImageView = findViewById(R.id.AlbumSongDetailsImageView);
        AlbumTextView = findViewById(R.id.AlbumSongDetailsTextView);
    }

    public void getInfo(String id){
        GetSpotifyTrack task = new GetSpotifyTrack(this, id, 0,this);
        task.execute();
    }

    public void AddInfo(Song Song){
        song = Song;
        Log.d("CHECK",song.getName());
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
        ArtistTextView.setText(getString(R.string.ArtistsWithLink, artistsString));
        AlbumTextView.setText(getString(R.string.AlbumWithLink, song.getAlbum().getName()));
        Picasso.get().load(song.getAlbum().getImages()[0].getUrl()).into(AlbumImageView, new Callback() {
            @Override
            public void onSuccess() {
                AlbumImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//Or ScaleType.FIT_CENTER
            }
            @Override
            public void onError(Exception e) {
                //nothing for now
                e.printStackTrace();
            }
        });
        toolbar.inflateMenu(R.menu.menu_details);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(song.getName());
    }

    public void AddToList(View view){
        Intent intent = new Intent(SongDetailsActivity.this, RemoveAddListsActivity.class);
        intent.putExtra("tipo","MusicLists");
        intent.putExtra("type","song");
        intent.putExtra("id",id);
        intent.putExtra("use","add");
        startActivity(intent);
    }
}
