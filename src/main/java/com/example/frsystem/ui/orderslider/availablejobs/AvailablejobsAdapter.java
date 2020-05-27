package com.example.frsystem.ui.orderslider.availablejobs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frsystem.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class AvailablejobsAdapter extends FirestoreRecyclerAdapter<Availablejobsclass, AvailablejobsAdapter.ViewHolder>  {
    private static final String TAG = "OrderAdapter";
    Context context;
    private static Activity ctx;
    //private static OnItemClickListener listener;




    private final String IMAGE_URL = "REPLACE_WITH_STORAGE_URL/Firebase.png";
    private ImageView mImageView;

    public AvailablejobsAdapter(@NonNull FirestoreRecyclerOptions<Availablejobsclass> options, Activity ctx) {
        super(options);
        this.ctx = ctx;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Availablejobsclass model) {
        holder.getTextViewname().setText(model.getClient_name());
        holder.getTextViewlocation().setText(model.getLocationName());
        holder.getTextViewnumber().setText(String.valueOf(model.getClientnumber()));
        holder.getTextdesc().setText(model.getDesc());

        //Glide.with(ctx).load(model.getImageref()).into(holder.clientimageref);
        Picasso.get().load(model.getImageref()).resize(250, 400).error(R.drawable.ic_account_user).into(holder.clientimageref);
        //Log.d(TAG, "url " + model.getImageref() + " .");
        //Picasso.get().load(model.getImageref()).into(holder.clientimageref);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.availablejobs_row_item, parent, false);

        return new ViewHolder(v);
    }

    private ClickListener mClicklistner;
    public interface ClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnClickListener(ClickListener listener){
        this.mClicklistner = listener;
    }


    //sub class for view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clientinfonametxt;
        private TextView clientinfonumbertxt;
        private TextView clientinfolocationtxt;
        private TextView clientinfodesc;
        private ImageView clientimageref;



        public ViewHolder(View v) {
            super(v);

            clientinfonametxt = (TextView) v.findViewById(R.id.clientinfofullname);
            clientinfolocationtxt = (TextView) v.findViewById(R.id.clientinfolocationtxt);
            clientinfonumbertxt = (TextView) v.findViewById(R.id.clientinfonumbertxt);
            clientinfodesc = (TextView) v.findViewById(R.id.clientinfodesctxt);
            clientimageref = (ImageView) v.findViewById(R.id.profile_image_availablejobs);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && mClicklistner!= null){
                        mClicklistner.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && mClicklistner!= null){
                        mClicklistner.onItemLongClick(getSnapshots().getSnapshot(position), position);
                    }

                 return true;
                }
            });
        }

        public void setIcon(String url) {
            //Glide.with(myFragment).load(url).centerCrop().placeholder(R.drawable.loading_spinner).into(myImageView);
        }


        public TextView getTextViewname() {
            return clientinfonametxt;
        }
        public TextView getTextViewlocation() {
            return clientinfolocationtxt;
        }
        public TextView getTextViewnumber() {
            return clientinfonumbertxt;
        }
        public TextView getTextdesc() {
            return clientinfodesc;
        }
        public ImageView getClientImageref(){
            return clientimageref;
        }

    }
    // END_INCLUDE
}
