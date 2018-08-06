package com.vyas.pranav.mycookbook.recyclerutils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.RecepieDescriptionActivity;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        Uri photoUri = Uri.parse(mainRecepieModelList.get(position).getImage());
        Glide.with(context)
                .load(photoUri)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.waitforfood)
                        .error(R.drawable.error_yellow)
                .useAnimationPool(true))
                .into(holder.imageRecepieMaster);
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
        @BindView(R.id.image_recepie_recycler_main) ImageView imageRecepieMaster;
        @BindView(R.id.text_recepie_recycler_main) TextView tvRecepieName;
        @BindView(R.id.text_recepie_servings_recycler_main) TextView tvServings;
        MainListItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
