package com.plorrios.medialists.Music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.plorrios.medialists.Adapters.Music.AlbumListSearchAdapter;
import com.plorrios.medialists.Adapters.Music.ArtistListSearchAdapter;
import com.plorrios.medialists.Adapters.Music.MusicListSearchAdapter;
import com.plorrios.medialists.Objects.Music.Album;
import com.plorrios.medialists.Objects.Music.AlbumList;
import com.plorrios.medialists.Objects.Music.Albums;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.ArtistList;
import com.plorrios.medialists.Objects.Music.Artists;
import com.plorrios.medialists.Objects.Music.MusicList;
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.Objects.Music.Songs;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Tasks.Music.GetAlbumsSpotifySearch;
import com.plorrios.medialists.Tasks.Music.GetArtistsSpotifySearch;
import com.plorrios.medialists.Tasks.Music.GetSongsSpotifySearch;

public class MusicSearchActivity extends AppCompatActivity {

    Context context;
    ProgressBar progressBar;
    MusicListSearchAdapter musicAdapter;
    AlbumListSearchAdapter albumAdapter;
    ArtistListSearchAdapter artistAdapter;
    SearchView search;
    boolean musicSearch=true;
    boolean albumsSearch=false;
    boolean artistsSearch=false;
    MusicList musicList;
    boolean newSearch;
    int page = 0;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light)).equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
        }else {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_search);

        ((RadioGroup) findViewById(R.id.Radio_Group_Music_search)).setOnCheckedChangeListener(ToggleListener);

        SearchView searchView = findViewById(R.id.MusicSearchView);
        recyclerView = findViewById(R.id.searchRecyclerView);
        progressBar = findViewById(R.id.searchProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        search = findViewById(R.id.MusicSearchView);

        newSearch = false;
        context = this;

        searchView.requestFocus();

        Intent intent = getIntent();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {

                startSearch(query);
                progressBar.setVisibility(View.VISIBLE);
                newSearch=true;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // do your search on change or save the last string in search
                return false;
            }
        });

        Song[] songs = new Song[0];
        Songs songsitem = new Songs(songs);
        musicList = new MusicList(songsitem);
        Album[] albums = new Album[0];
        Albums albumss = new Albums(albums);
        AlbumList albumList = new AlbumList(albumss);
        Artist[] artists = new Artist[0];
        Artists artistss = new Artists(artists);
        ArtistList artistList = new ArtistList(artistss);

        musicAdapter = new MusicListSearchAdapter( musicList, new MusicListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (musicAdapter.getSong(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openSong(position);
                    //Toast.makeText(context, musicAdapter.getSong(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);

        albumAdapter = new AlbumListSearchAdapter(albumList, new AlbumListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (albumAdapter.getAlbum(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openAlbum(position);
                    Toast.makeText(context, albumAdapter.getAlbum(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);

        artistAdapter = new ArtistListSearchAdapter(artistList, new ArtistListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (artistAdapter.getArtist(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openArtist(position);
                    Toast.makeText(context, artistAdapter.getArtist(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void startSearch(String query){

        if (hasConnectivity()) {

            if (musicSearch) {
                GetSongsSpotifySearch task = new GetSongsSpotifySearch(this, query, 0, this);
                task.execute();
            }else if (albumsSearch){
                GetAlbumsSpotifySearch task = new GetAlbumsSpotifySearch(this, query, 0, this);
                task.execute();
            }else if (artistsSearch){
                GetArtistsSpotifySearch task = new GetArtistsSpotifySearch(this, query, 0, this);
                task.execute();
            }
        }else {
            Toast.makeText(this, getText(R.string.NoConnectivity), Toast.LENGTH_LONG).show();
        }

    }

    public void finishedSearch(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void AddMusic(MusicList music){
        if (newSearch) {
            musicAdapter.clear();
        }
        if (music.GetMusic().getCount() == 0)
        {
            Toast.makeText(this, "Song not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Song added", Toast.LENGTH_LONG).show();
            musicAdapter.addMusic(music);
            musicAdapter.notifyDataSetChanged();
            Log.d("Music Adapter",String.valueOf(musicAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void AddAlbums(AlbumList albums){
        if (newSearch) {
            albumAdapter.clear();
        }
        if (albums.GetCount() == 0)
        {
            Toast.makeText(this, "Song not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Song added", Toast.LENGTH_LONG).show();
            albumAdapter.addAlbum(albums);
            albumAdapter.notifyDataSetChanged();
            Log.d("albumAdapter",String.valueOf(albumAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void AddArtists(ArtistList artists){
        if (newSearch) {
            artistAdapter.clear();
        }
        if (artists.GetCount() == 0)
        {
            Toast.makeText(this, "Song not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Song added", Toast.LENGTH_LONG).show();
            artistAdapter.addArtist(artists);
            artistAdapter.notifyDataSetChanged();
            Log.d("albumAdapter",String.valueOf(artistAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public boolean hasConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }


    @Override
    public void onBackPressed() {
        // Write your code here
        musicAdapter.stopPlaying();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        musicAdapter.stopPlaying();
        super.onStop();
    }

    public void onToggle(android.view.View view){
        ((RadioGroup)view.getParent()).check(view.getId());
        int pos = view.getId();
        clearSearch();
        switch (pos) {
            case R.id.btn_Music_search:
                Log.d("button","Music");
                musicSearch = true;
                artistsSearch = false;
                albumsSearch = false;
                recyclerView.setAdapter(musicAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
            case R.id.btn_Album_search:
                Log.d("button","Album");
                musicSearch = false;
                artistsSearch = false;
                albumsSearch = true;
                recyclerView.setAdapter(albumAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
            case R.id.btn_Artist_search:
                Log.d("button","Artist");
                musicSearch = false;
                artistsSearch = true;
                albumsSearch = false;
                recyclerView.setAdapter(artistAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
        }

    }

    public void clearSearch(){
        musicAdapter.clear();
        albumAdapter.clear();
        artistAdapter.clear();
    }

    public void openSong(int position) {
        Intent intent = new Intent(MusicSearchActivity.this, SongDetailsActivity.class);
        intent.putExtra("id", musicAdapter.getSong(position).getId());
        musicAdapter.stopPlaying();
        startActivity(intent);
    }

    public void openArtist(int position) {
        Intent intent = new Intent(MusicSearchActivity.this, ArtistDetailsActivity.class);
        intent.putExtra("id", artistAdapter.getArtist(position).getId());
        musicAdapter.stopPlaying();
        startActivity(intent);
    }

    public void openAlbum(int position){
        Intent intent = new Intent(MusicSearchActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", albumAdapter.getAlbum(position).getId());
        musicAdapter.stopPlaying();
        startActivity(intent);
    }


    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

}
