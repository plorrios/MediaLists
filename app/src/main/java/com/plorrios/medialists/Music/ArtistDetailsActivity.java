package com.plorrios.medialists.Music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.plorrios.medialists.ListsModifications.RemoveAddListsActivity;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Tasks.Music.GetSpotifyAlbum;
import com.plorrios.medialists.Tasks.Music.GetSpotifyArtist;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ArtistDetailsActivity extends AppCompatActivity {

    String id;
    Toolbar toolbar;
    TextView ArtistTextView;
    ImageView AlbumImageView;
    Artist artist;

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
        setContentView(R.layout.activity_artist_details);

        id = getIntent().getStringExtra("id");
        getInfo(id);
        toolbar = findViewById(R.id.AlbumDetailsToolbar);
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(R.drawable.toolbar_drawable_dark));
        }
        AlbumImageView = findViewById(R.id.ArtistDetailsImageView);

    }

    public void getInfo(String id){
        GetSpotifyArtist task = new GetSpotifyArtist(this, id, 0,this);
        task.execute();
    }

    public void AddInfo(Artist martist){
        artist = martist;
        Log.d("CHECK",artist.getName());
        Picasso.get().load(artist.getImages()[0].getUrl()).into(AlbumImageView, new Callback() {
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
        mTitle.setText(artist.getName());
    }

    public void AddToList(View view){
        Intent intent = new Intent(ArtistDetailsActivity.this, RemoveAddListsActivity.class);
        intent.putExtra("tipo","MusicLists");
        intent.putExtra("type","artist");
        intent.putExtra("id",id);
        intent.putExtra("use","add");
        startActivity(intent);
    }
}
