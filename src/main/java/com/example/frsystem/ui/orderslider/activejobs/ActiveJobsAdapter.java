package com.example.frsystem.ui.orderslider.activejobs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frsystem.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ActiveJobsAdapter extends FirestoreRecyclerAdapter<Activejobsclass, ActiveJobsAdapter.ViewHolder> {
    private static final String TAG = "OrderAdapter";
    Dialog popupDialog;
    private static Activity ctx;

    private Activejobsclass[] listdata;

    // RecyclerView recyclerView;
    public ActiveJobsAdapter(FirestoreRecyclerOptions<Activejobsclass> options, Activity ctx) {
        super(options);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Activejobsclass model) {
        holder.getTextViewname().setText(model.getClientusername());
        //holder.getTextViewlocation().setText(model.getLocationName());
        holder.getTextViewnumber().setText(String.valueOf(model.getClientnumber()));
        holder.getTextdesc().setText(model.getClient_description());
    }

    public void showpopup(View v){
        popupDialog.setContentView(R.layout.popupwindowuserinfo);
        popupDialog.show();
    }

    //sub class for view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clientinfonametxt;
        private TextView clientinfonumbertxt;
        private TextView clientinfolocationtxt;
        private TextView clientinfodatetxt;
        private TextView clientinfodesc;
        Dialog popupDialog;
        Context context;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    final Dialog popupDialog = new Dialog(ctx);
                    popupDialog.setContentView(R.layout.popupwindowuserinfo);
                    popupDialog.show();
                }
            });

            clientinfonametxt = (TextView) v.findViewById(R.id.clientinfofullname);
            clientinfolocationtxt = (TextView) v.findViewById(R.id.clientinfolocationtxt);
            clientinfonumbertxt = (TextView) v.findViewById(R.id.clientinfonumbertxt);
            clientinfodatetxt = (TextView) v.findViewById(R.id.clientinfodateofdelivery);
            clientinfodesc = (TextView) v.findViewById(R.id.clientinfodesctxt);
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
        public TextView getTextViewdate() {
            return clientinfodatetxt;
        }
        public TextView getTextdesc() {
            return clientinfodesc;
        }
    }
    // END_INCLUDE
}
