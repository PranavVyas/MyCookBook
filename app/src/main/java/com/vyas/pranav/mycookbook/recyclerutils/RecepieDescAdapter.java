package com.vyas.pranav.mycookbook.recyclerutils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.SingleStepActivity;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

public class RecepieDescAdapter extends RecyclerView.Adapter<RecepieDescAdapter.RecepieDescHolder>{
    Context context;
    MainRecepieModel recepie;

    public static final String KEY_STEP_SINGLE = "SingleStep";
    public static final String KEY_ALL_STEPS = "AllSteps";
    public static final String KEY_CURRENT_STEP = "CurrentStep";

    public RecepieDescAdapter(Context ct) {
        this.context = ct;
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
        final int currPos = position;
        holder.no.setText("Step No "+currPos+" : ");
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String StepJson = gson.toJson(recepie.getSteps());
                Intent intent = new Intent(context,SingleStepActivity.class);
                intent.putExtra(KEY_ALL_STEPS,StepJson);
                //Toast.makeText(context, "Clickec on Position "+currPos, Toast.LENGTH_SHORT).show();
                intent.putExtra(KEY_CURRENT_STEP,position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recepie == null) return 0;
        else return recepie.getSteps().size();
    }

    class RecepieDescHolder extends RecyclerView.ViewHolder{
        TextView desc,no,more;
        public RecepieDescHolder(View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.text_stepdesc_recepie_desc_recycler);
            no = itemView.findViewById(R.id.text_stepno_recepie_desc_recycler);
            more = itemView.findViewById(R.id.text_more_recepie_desc_recycler);
        }
    }

    public void setRecepie(MainRecepieModel recepie){
        this.recepie = recepie;
        notifyDataSetChanged();
    }
}
