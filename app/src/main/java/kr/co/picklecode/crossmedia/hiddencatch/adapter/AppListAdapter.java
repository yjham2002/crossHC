package kr.co.picklecode.crossmedia.hiddencatch.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import bases.imageTransform.RoundedTransform;
import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.model.RecommendBox;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.RecyclerViewHolder>{

    private List<RecommendBox> mItems = new Vector<>();
    private Context mContext;

    public AppListAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setDataAndRefresh(List<RecommendBox> list){
        this.mItems = list;
        this.notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumb;
        public TextView title;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.thumb = itemView.findViewById(R.id.thumb);
            this.title = itemView.findViewById(R.id.appName);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_recommend, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final RecommendBox recommendBox = mItems.get(position);

        holder.title.setText(recommendBox.getAppName());
        if(recommendBox.getImgPath() != null && !recommendBox.getImgPath().trim().equals("")){
            Picasso
                    .get()
                    .load(recommendBox.getImgPath())
                    .centerCrop()
                    .resize(150, 150)
                    .placeholder(R.drawable.icon_hour_glass)
                    .transform(new RoundedTransform(10, 0)).into(holder.thumb);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + recommendBox.getPackageName());
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
