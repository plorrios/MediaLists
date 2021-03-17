package com.plorrios.medialists.Tasks.Games;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.plorrios.medialists.Games.GameSearchActivity;
import com.plorrios.medialists.Objects.Games.APIDevelopersList;
import com.plorrios.medialists.Objects.Games.DevelopersList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetDeveloperSearch extends AsyncTask<Void, Void, APIDevelopersList> {

    WeakReference<GameSearchActivity> PetitionsWeakReference;
    String busquedaLocal;
    int pageLocal = 1;
    boolean end = false;

    public GetDeveloperSearch(GameSearchActivity activity, String busqueda, int page){
        busquedaLocal = busqueda;
        this.PetitionsWeakReference = new WeakReference<GameSearchActivity>(activity);
        pageLocal = page;
    }

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    @Override
    protected APIDevelopersList doInBackground(Void... voids) {
        //return getGame();
        return getDevelopersList(busquedaLocal);
    }

    private APIDevelopersList getDevelopersList(String search){
        APIDevelopersList devs = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendEncodedPath("developers?page=" + pageLocal + "&page_size=40" + "&search=" + search);
        Log.d("search",builder.build().toString());
        try{
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            //Log.d("RespondesCode",connection.getResponseMessage());
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile =  new Gson();
                //Log.d("",gsonfile.toString());
                devs = gsonfile.fromJson(reader,APIDevelopersList.class);
                reader.close();
                //Log.d("received","received");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error","error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error","error2");
        }
        if(devs!=null) {
            Log.d("search", Integer.toString(devs.getCount()));

        }else{
            end = true;
        }
        return devs;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(APIDevelopersList devs) {
        DevelopersList result = new DevelopersList(devs.getDevs());
        //PetitionsWeakReference.get().AddDevelopers(result);
        super.onPostExecute(devs);
    }
}
