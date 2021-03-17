package com.plorrios.medialists.Tasks.Music;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.plorrios.medialists.Extras.Base64;
import com.plorrios.medialists.Objects.Music.MusicList;
import com.plorrios.medialists.Objects.Music.Spotify_acces_token;
import com.plorrios.medialists.Music.MusicSearchActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class GetSongsSpotifySearch extends AsyncTask<Void, Void, MusicList> {


    WeakReference<MusicSearchActivity> PetitionsWeakReference;
    String busquedaLocal;
    int pageLocal = 1;
    Context context;
    String token = null;

    public GetSongsSpotifySearch(MusicSearchActivity activity, String busqueda, int page, Context mcontext) {
        busquedaLocal = busqueda;
        this.PetitionsWeakReference = new WeakReference<MusicSearchActivity>(activity);
        pageLocal = page;
        context = mcontext;
    }

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    @Override
    protected MusicList doInBackground(Void... voids) {
        //return getGame();
        MusicList Music = getMusicList(false);
        return Music;
    }

    private MusicList getMusicList(boolean asktoken) {


        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = obtainedPreferences.getString("SpotifyToken", null);

        //Log.d("SAVED TOKEN IS NULL", String.valueOf(token == null));

        if (token == null || asktoken) {
            return getResult();
        } else {
            return getToken();
        }
    }

    private MusicList getToken() {
        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        MusicList music = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("accounts.spotify.com");
        builder.appendPath("api");
        builder.appendPath("token");

        String auth = "a85217df9f3245918e7928c4f97f8627:7411c46b7c7c4a48a0193c58e0bbff02";
        String authencoded = Base64.encodeBytes(auth.getBytes());

        try {

            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + authencoded);
            connection.getOutputStream().write(("grant_type=client_credentials").getBytes());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile = new Gson();
                //Log.d("",gsonfile.toString());
                Spotify_acces_token tokenObject = gsonfile.fromJson(reader, Spotify_acces_token.class);
                token = tokenObject.getAccess_token();
                reader.close();
                //Log.d("TOKEN", token);
                SharedPreferences.Editor editor = obtainedPreferences.edit();
                editor.putString("SpotifyToken", token);
                editor.apply();
            } else{
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("Answer",reader.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        /*if (music != null) {
            Log.d("search", Integer.toString(music.GetCount()));
        }*/

        return getResult();
    }


    private MusicList getResult() {

        MusicList music = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.spotify.com");
        builder.appendPath("v1");

        builder.appendEncodedPath("search?q=" + busquedaLocal + "&type=track");
        try {

            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile = new Gson();
                Log.d("GSON",gsonfile.toString());
                music = gsonfile.fromJson(reader, MusicList.class);
                reader.close();
                //Log.d("First Result Name", music.GetMusic().getItems()[0].getName());
                //Log.d("First Result ID", music.GetMusic().getItems()[0].getId());
                //Log.d("SEARCH COUNT", music.GetMusic().getItems()[1].getName());
            } else{
                return getToken();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        if (music != null) {
            Log.d("search", Integer.toString(music.GetCount()));
        }
        return music;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(MusicList music) {
        PetitionsWeakReference.get().AddMusic(music);
        super.onPostExecute(music);
    }
}
