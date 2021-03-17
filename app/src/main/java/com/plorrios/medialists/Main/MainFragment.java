package com.plorrios.medialists.Main;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.plorrios.medialists.R;

import static androidx.core.content.ContextCompat.getDrawable;

public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        AdView mAdView = (AdView) view.findViewById(R.id.adViewMainActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.MainToolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(R.string.AppTitle);

        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.toolbar_drawable_dark));
        }

        return view;
    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // or  (ImageView) view.findViewById(R.id.foo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
