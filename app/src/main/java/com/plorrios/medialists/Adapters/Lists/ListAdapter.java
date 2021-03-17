package com.plorrios.medialists.Adapters.Lists;

import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<String> ListTitles;
    private StorageReference storageRef;
    private String email;
    private String tipoListas;
    private static IClickListener onClk;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;



    public static class ListsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ListsViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewListItem);
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
    public ListAdapter(ArrayList<String> myListTitles, String memail, StorageReference mstorageRef, String mTipoListas, IClickListener monClk) {
        Log.d("Adapter","Adapter Started");
        ListTitles = myListTitles;
        storageRef = mstorageRef;
        email = memail;
        tipoListas = mTipoListas;
        onClk = monClk;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CREATED", "View Holder created");

        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            final ListsViewHolder vh = new ListsViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;
        } else {
            return new ListAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }

    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (getItemViewType(position) == CONTENT) {
            final int RealPosition = getRealPosition(position);
            final ListAdapter.ListsViewHolder holder = (ListAdapter.ListsViewHolder) mholder;
            holder.textView.setText(ListTitles.get(RealPosition));
            final ImageView im = holder.imageView;
            String search = email + tipoListas + ListTitles.get(RealPosition);
            storageRef.child(search).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(im, new Callback() {
                        @Override
                        public void onSuccess() {
                            im.setScaleType(ImageView.ScaleType.CENTER_CROP);//Or ScaleType.FIT_CENTER
                        }

                        @Override
                        public void onError(Exception e) {
                            //nothing for now
                            e.printStackTrace();
                        }
                    });
                    Log.d("SUCCESS", "Image loaded succesfully");
                    // Got the download URL for 'users/me/profile.png'
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("ERROR", "error on loading image");
                    // Handle any errors
                }
            });
        }else {
            ListAdapter.AdViewHolder holder = (ListAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null) {
                holder.adView.loadAd(adRequest);
            }
        }



    }

    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && ListTitles.size() >= LIST_AD_DELTA - 1) {
            additionalContent = ListTitles.size() / (LIST_AD_DELTA - 1);
        }
        return ListTitles.size() + additionalContent;
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

    public interface IClickListener { void onClickListener(int position); }

}
