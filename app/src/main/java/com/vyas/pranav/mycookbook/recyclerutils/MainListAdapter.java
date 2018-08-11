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

import com.blankj.ALog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vyas.pranav.mycookbook.BuildConfig;
import com.vyas.pranav.mycookbook.widgetutils.IngrediantWidget;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.ui.RecepieDescriptionActivity;
import com.vyas.pranav.mycookbook.extrautils.SharedPrefs;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainListItemHolder>{

    private Context context;
    private List<MainRecepieModel> mainRecepieModelList;
    public static final String KEY_SINGLE_RECEPIE_JSON = "SingleRecepieJson";

    public MainListAdapter(Context context) {
        this.context = context;
        initALog();
    }

    @NonNull
    @Override
    public MainListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_receipie_holder,parent,false);
        return new MainListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListItemHolder holder, final int position) {
        holder.tvRecepieName.setText(mainRecepieModelList.get(position).getName());
        holder.tvServings.setText("Servings : "+mainRecepieModelList.get(position).getServings());
        Uri photoUri = Uri.parse(mainRecepieModelList.get(position).getImage());
        Picasso.get()
                .load(photoUri)
                .error(R.drawable.default_food)
                .placeholder(R.drawable.default_food)
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

    void updatewidget(String RecepieSingleJson){
        SharedPrefs.addRecepieToSharedPrefs(context,RecepieSingleJson);
        ALog.d("Updating widget from MainaCTIVITY");
        IngrediantWidget.UpdateWidget(context);
    }

    class MainListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.image_recepie_recycler_main) ImageView imageRecepieMaster;
        @BindView(R.id.text_recepie_recycler_main) TextView tvRecepieName;
        @BindView(R.id.text_recepie_servings_recycler_main) TextView tvServings;
        MainListItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RecepieDescriptionActivity.class);
            Gson gson = new Gson();
            String RecepieSingleJson = gson.toJson(mainRecepieModelList.get(getAdapterPosition()));
            intent.putExtra(KEY_SINGLE_RECEPIE_JSON,RecepieSingleJson);
            updatewidget(RecepieSingleJson);
            context.startActivity(intent);
        }
    }

    // init it in ur application
    public void initALog() {
        ALog.Config config = ALog.init(context)
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"alog"，即写入文件为"alog-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(ALog.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(ALog.V)// log文件过滤器，和logcat过滤器同理，默认Verbose
                .setStackDeep(1);// log栈深度，默认为1
    }
}
