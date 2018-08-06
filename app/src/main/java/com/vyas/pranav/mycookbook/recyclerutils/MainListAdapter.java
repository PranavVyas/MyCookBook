package com.vyas.pranav.mycookbook.recyclerutils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.RecepieDescriptionActivity;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;
import java.util.zip.Inflater;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainListItemHolder>{

    Context context;
    List<MainRecepieModel> mainRecepieModelList;
    public static final String KEY_SINGLE_RECEPIE_JSON = "SingleRecepieJson";

    public MainListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_receipie_holder,parent,false);
        return new MainListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListItemHolder holder, final int position) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecepieDescriptionActivity.class);
                Gson gson = new Gson();
                String RecepieSingleJson = gson.toJson(mainRecepieModelList.get(position));
                intent.putExtra(KEY_SINGLE_RECEPIE_JSON,RecepieSingleJson);
                context.startActivity(intent);
            }
        };
        holder.tvRecepieName.setText(mainRecepieModelList.get(position).getName());
        holder.tvServings.setText("Servings : "+mainRecepieModelList.get(position).getServings());
        holder.imageRecepieMaster.setOnClickListener(listener);
        holder.tvRecepieName.setOnClickListener(listener);
        holder.tvServings.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if (mainRecepieModelList == null) return 0;
        else return mainRecepieModelList.size();
    }

    public void setRecepiesList(List<MainRecepieModel> recepiesList){
        this.mainRecepieModelList = recepiesList;
        notifyDataSetChanged();
    }

    class MainListItemHolder extends RecyclerView.ViewHolder{
        ImageView imageRecepieMaster;
        TextView tvRecepieName,tvServings;
        MainListItemHolder(View itemView) {
            super(itemView);
            imageRecepieMaster = itemView.findViewById(R.id.image_recepie_recycler_main);
            tvRecepieName = itemView.findViewById(R.id.text_recepie_recycler_main);
            tvServings = itemView.findViewById(R.id.text_recepie_servings_recycler_main);
        }
    }
}
