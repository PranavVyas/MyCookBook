package com.vyas.pranav.mycookbook.recyclerutils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.SingleStepActivity;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecepieDescAdapter extends RecyclerView.Adapter<RecepieDescAdapter.RecepieDescHolder>{
    private Context context;
    private MainRecepieModel recepie;
    //TODO
    private OnTapListener mCallback;
    private boolean twoPane;

    public static final String KEY_STEP_SINGLE = "SingleStep";
    public static final String KEY_ALL_STEPS = "AllSteps";
    public static final String KEY_CURRENT_STEP = "CurrentStep";

    public RecepieDescAdapter(Context ct,OnTapListener mCallback) {
        this.context = ct;
        //TODO
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public RecepieDescHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recepie_desc_step_holder,parent,false);
        return new RecepieDescHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecepieDescHolder holder, final int position) {
        holder.desc.setText(recepie.getSteps().get(position).getShortDescription());
        holder.no.setText("Step "+ (position+1) +" : ");
        String thumbUrl = recepie.getSteps().get(position).getThumbnailURL();
        Uri thumbUri = Uri.parse(thumbUrl);
        Picasso.get()
                .load(thumbUri)
                .placeholder(R.drawable.default_food)
                .error(R.drawable.default_food)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (recepie == null) return 0;
        else return recepie.getSteps().size();
    }

    public void setTwoPaneLayout(boolean twoPane) {
        this.twoPane = twoPane;
    }

    class RecepieDescHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.text_stepdesc_recepie_desc_recycler) TextView desc;
        @BindView(R.id.text_stepno_recepie_desc_recycler) TextView no;
        @BindView(R.id.text_more_recepie_desc_recycler) Button more;
        @BindView(R.id.image_thumbnail_desc_frag) ImageView thumbnail;
        public RecepieDescHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            more.setOnClickListener(this);
        }

        /*
            @param StepJson = List<StepModel>
         */
        @Override
        public void onClick(View v) {
            //mCallback = (OnTapListener) this;
            Gson gson = new Gson();
            String StepJson = gson.toJson(recepie.getSteps());
            if(twoPane){
                //TODO
                mCallback.OnItemClick(getAdapterPosition(),StepJson);
            }else{
                Intent intent = new Intent(context,SingleStepActivity.class);
                intent.putExtra(KEY_ALL_STEPS,StepJson);
                //Toast.makeText(context, "Clickec on Position "+currPos, Toast.LENGTH_SHORT).show();
                intent.putExtra(KEY_CURRENT_STEP,getAdapterPosition());
                context.startActivity(intent);
            }
        }
    }

    public void setRecepie(MainRecepieModel recepie){
        this.recepie = recepie;
        notifyDataSetChanged();
    }

    public interface OnTapListener{
        void OnItemClick(int stepNo,String StepJsonFull);
    }
}
