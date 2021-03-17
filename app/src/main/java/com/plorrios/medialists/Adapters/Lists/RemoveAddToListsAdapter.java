package com.plorrios.medialists.Adapters.Lists;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class RemoveAddToListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> ListTitles;
    private StorageReference storageRef;
    private String email;
    static ArrayList<TextView> RemoveTitles;
    static ArrayList<ImageView> RemoveImages;
    private String tipolistas;
    String use;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class EditListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public CheckBox checkBox;

        public EditListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewEditListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewEditListItem);
            checkBox = (CheckBox) view.findViewById(R.id.checkBoxEditListItem);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if ( isChecked )
                    {
                        RemoveTitles.add(textView);
                        RemoveImages.add(imageView);
                    }
                    else{
                        RemoveTitles.remove(textView);
                        RemoveImages.remove(imageView);
                    }

                }
            });
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
    public RemoveAddToListsAdapter(ArrayList<String> myListTitles, String memail, StorageReference mstorageRef, String mTipoListas, String muse) {
        Log.d("Adapter","Adapter Started");
        ListTitles = myListTitles;
        storageRef = mstorageRef;
        email = memail;
        RemoveTitles = new ArrayList<TextView>();
        RemoveImages = new ArrayList<ImageView>();
        tipolistas = mTipoListas;
        use = muse;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CREATED", "View Holder created");

        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_list, parent, false);

            final RemoveAddToListsAdapter.EditListViewHolder vh = new RemoveAddToListsAdapter.EditListViewHolder(firstView);
            return vh;

        } else {
            return new RemoveAddToListsAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }

    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (getItemViewType(position) == CONTENT) {
            final int RealPosition = getRealPosition(position);
            final RemoveAddToListsAdapter.EditListViewHolder holder = (RemoveAddToListsAdapter.EditListViewHolder) mholder;
            holder.textView.setText(ListTitles.get(RealPosition));
            final ImageView im = holder.imageView;
            String search = email + tipolistas + ListTitles.get(RealPosition);
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
            RemoveAddToListsAdapter.AdViewHolder holder = (RemoveAddToListsAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null) {
                holder.adView.loadAd(adRequest);
            }
        }

    }

    public ArrayList<String> listsToRemove() {

        ArrayList<String> res = new ArrayList<>();


        for (TextView TitleToRemove : RemoveTitles) {
            res.add(TitleToRemove.getText().toString());
            if (use.equals("delete")) {
                String search = email + tipolistas + TitleToRemove.getText().toString();
                StorageReference auxStorageRef = storageRef.child(search);
                auxStorageRef.delete();
            }
        }
        return res;
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

}
