package com.plorrios.medialists.Games;

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

import com.plorrios.medialists.Adapters.Games.DevelopersListSearchAdapter;
import com.plorrios.medialists.Adapters.Games.GamesListSearchAdapter;
import com.plorrios.medialists.Adapters.Games.PublishersListSearchAdapter;
import com.plorrios.medialists.Objects.Games.Developer;
import com.plorrios.medialists.Objects.Games.DevelopersList;
import com.plorrios.medialists.Objects.Games.Game;
import com.plorrios.medialists.Objects.Games.GamesList;
import com.plorrios.medialists.Objects.Games.Publisher;
import com.plorrios.medialists.Objects.Games.PublishersList;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Tasks.Games.GetDeveloperSearch;
import com.plorrios.medialists.Tasks.Games.GetGameSearch;
import com.plorrios.medialists.Tasks.Games.GetPublisherSearch;


public class GameSearchActivity extends AppCompatActivity {

    Context context;
    ProgressBar progressBar;
    SearchView search;
    boolean gameSearch=true;
    GamesListSearchAdapter gamesAdapter;
    /*DevelopersListSearchAdapter developersAdapter;
    PublishersListSearchAdapter publishersAdapter;
    boolean developerSearch=false;
    boolean publisherSearch=false;*/
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
        setContentView(R.layout.activity_game_search);

        //((RadioGroup) findViewById(R.id.Radio_Group_Games_search)).setOnCheckedChangeListener(ToggleListener);

        SearchView searchView = findViewById(R.id.GamesSearchView);
        recyclerView = findViewById(R.id.searchRecyclerView);
        progressBar = findViewById(R.id.searchProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        search = findViewById(R.id.GamesSearchView);

        newSearch = false;
        context = this;

        searchView.requestFocus();

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

        Game[] games = new Game[0];
        GamesList gamesList = new GamesList(games);
        /*Developer[] developers = new Developer[0];
        DevelopersList developerList = new DevelopersList(developers);
        Publisher[] publishers = new Publisher[0];
        PublishersList publisherList = new PublishersList(publishers);*/

        gamesAdapter = new GamesListSearchAdapter( gamesList, new GamesListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (gamesAdapter.getGame(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openGame(position);
                    //Toast.makeText(context, musicAdapter.getSong(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);

        /*developersAdapter = new DevelopersListSearchAdapter(developerList, new DevelopersListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (developersAdapter.getDeveloper(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openDeveloper(position);
                    //Toast.makeText(context, developersAdapter.getDeveloper(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);

        publishersAdapter = new PublishersListSearchAdapter(publisherList, new PublishersListSearchAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                if (publishersAdapter.getPublisher(position) == null) {
                    Toast.makeText(context, getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                }else {
                    openPublisher(position);
                    //Toast.makeText(context, publishersAdapter.getPublisher(position).getName(), Toast.LENGTH_SHORT).show();
                    //Log.d("",musicAdapter.getSong(position).getId());
                }
            }
        },this);*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(gamesAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void startSearch(String query){

        if (hasConnectivity()) {

            if (gameSearch) {
                GetGameSearch task = new GetGameSearch(this, query, 1);
                task.execute();
            }/*else if (developerSearch){
                GetDeveloperSearch task = new GetDeveloperSearch(this, query, 1);
                task.execute();
            }else if (publisherSearch){
                GetPublisherSearch task = new GetPublisherSearch(this, query, 1);
                task.execute();
            }*/
        }else {
            Toast.makeText(this, getText(R.string.NoConnectivity), Toast.LENGTH_LONG).show();
        }

    }

    public void finishedSearch(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void AddGames(GamesList games){
        if (newSearch) {
            gamesAdapter.clear();
        }
        if (games.getGames().length == 0)
        {
            Toast.makeText(this, "Game not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Game added", Toast.LENGTH_LONG).show();
            gamesAdapter.addGames(games);
            gamesAdapter.notifyDataSetChanged();
            Log.d("Games Adapter",String.valueOf(gamesAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    /*public void AddDevelopers(DevelopersList devs){
        if (newSearch) {
            developersAdapter.clear();
        }
        if (devs.getDevs().length == 0)
        {
            Toast.makeText(this, "Developer not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Developer added", Toast.LENGTH_LONG).show();
            developersAdapter.addDevelopers(devs);
            developersAdapter.notifyDataSetChanged();
            Log.d("Developers Adapter",String.valueOf(developersAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void AddPublishers(PublishersList publishers){
        if (newSearch) {
            gamesAdapter.clear();
        }
        if (publishers.getPublishers().length == 0)
        {
            Toast.makeText(this, "Publisher not found", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(this, "Publisher added", Toast.LENGTH_LONG).show();
            publishersAdapter.addPublishers(publishers);
            publishersAdapter.notifyDataSetChanged();
            Log.d("Publishers Adapter",String.valueOf(publishersAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);

        }
    }*/


    public boolean hasConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    /*public void onToggle(android.view.View view){
        ((RadioGroup)view.getParent()).check(view.getId());
        int pos = view.getId();
        clearSearch();
        switch (pos) {
            case R.id.btn_Game_search:
                Log.d("button","Music");
                gameSearch = true;
                developerSearch = false;
                publisherSearch = false;
                recyclerView.setAdapter(gamesAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
            case R.id.btn_Developer_search:
                Log.d("button","Album");
                gameSearch = false;
                developerSearch = true;
                publisherSearch = false;
                recyclerView.setAdapter(developersAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
            case R.id.btn_Artist_search:
                Log.d("button","Artist");
                gameSearch = false;
                developerSearch = false;
                publisherSearch = true;
                recyclerView.setAdapter(publishersAdapter);
                progressBar.setVisibility(View.VISIBLE);
                startSearch(search.getQuery().toString());
                break;
        }

    }*/

    public void clearSearch(){
        gamesAdapter.clear();
        /*developersAdapter.clear();
        publishersAdapter.clear();*/
    }


    public void openGame(int position) {
        Intent intent = new Intent(GameSearchActivity.this, GameDetailsActivity.class);
        intent.putExtra("id", gamesAdapter.getGame(position).getId());
        startActivity(intent);
    }

    /*public void openDeveloper(int position) {
        Intent intent = new Intent(GameSearchActivity.this, DeveloperDetailsActivity.class);
        intent.putExtra("id", gamesAdapter.getGame(position).getId());
        startActivity(intent);
    }

    public void openPublisher(int position) {
        Intent intent = new Intent(GameSearchActivity.this, PublisherDetailsActivity.class);
        intent.putExtra("id", gamesAdapter.getGame(position).getId());
        startActivity(intent);
    }*/



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
