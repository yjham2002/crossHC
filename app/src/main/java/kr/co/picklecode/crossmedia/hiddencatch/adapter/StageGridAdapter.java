package kr.co.picklecode.crossmedia.hiddencatch.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import bases.Configs;
import bases.imageTransform.RoundedTransform;
import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.StageActivity;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageSynchronizer;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class StageGridAdapter extends BaseAdapter {

    public int selectedNum = -1;
    private HashMap<String, Integer> winInfo;
    private Context context;
    private List<StageBox> list = new Vector<>();

    public StageGridAdapter(Context context) {
        super();
        this.winInfo = StageUtil.getWinningInfo();
        Log.e("StageGridAdapter", this.winInfo.toString());
        this.context = context;
    }

    public void setDataAndRefresh(List<StageBox> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        final StageBox stageBox = this.list.get(arg0);

        HostView hostview;
        TextView stageId;
        ImageView level, clearImg;

        if (arg1 == null) {
            hostview = new HostView();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_grid_stage, null, true);
            hostview.stageId = arg1.findViewById(R.id.stageId);
            hostview.level = arg1.findViewById(R.id.level);
            hostview.clearImg = arg1.findViewById(R.id.clearImg);
            hostview.bgView = arg1.findViewById(R.id.bgView);

            arg1.setTag(hostview);
        } else {
            hostview = (HostView) arg1.getTag();
        }

        if(arg0 == selectedNum){
            hostview.bgView.setBackground(context.getResources().getDrawable(R.drawable.rounded_rect_thin_gray_no_border_light));
        }else{
            hostview.bgView.setBackground(context.getResources().getDrawable(R.drawable.rounded_rect_thin_gray_no_border));
        }

        if(winInfo.containsKey(StageUtil.genKeyForWinInfo(stageBox.getId()))){
            final int count = winInfo.get(StageUtil.genKeyForWinInfo(stageBox.getId()));

            if(count > 0){
                hostview.clearImg.setVisibility(View.VISIBLE);
                hostview.stageId.setVisibility(View.INVISIBLE);
                if(stageBox.getOriginalPath() != null && !stageBox.getOriginalPath().trim().equals("")){
                    final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Configs.DOWNLOAD_DIR + File.separator + stageBox.makePath());
                    Picasso
                            .get()
                            .load(dir)
                            .centerCrop()
                            .resize(150, 90)
                            .placeholder(R.drawable.icon_hour_glass)
                            .transform(new RoundedTransform(10, 0))
                            .into(hostview.clearImg);
                }
            }else{
                hostview.stageId.setVisibility(View.VISIBLE);
            }

            if(count == 0){
                hostview.clearImg.setVisibility(View.INVISIBLE);
                hostview.level.setImageDrawable(context.getResources().getDrawable(R.drawable.img_star_0_s));
            }else if(count == 1){
                hostview.level.setImageDrawable(context.getResources().getDrawable(R.drawable.img_star_1_s));
            }else if(count == 2){
                hostview.level.setImageDrawable(context.getResources().getDrawable(R.drawable.img_star_2_s));
            }else if(count >= 3){
                hostview.level.setImageDrawable(context.getResources().getDrawable(R.drawable.img_star_3_s));
            }
        }else{
            hostview.stageId.setVisibility(View.VISIBLE);
            hostview.clearImg.setVisibility(View.INVISIBLE);
            hostview.level.setImageDrawable(context.getResources().getDrawable(R.drawable.img_star_0_s));
        }

        hostview.stageId.setText((arg0 + 1) + "");

//        Log.e("sizeInfo", hostview.m_ivPhoto.getHeight() + "/" + hostview.m_ivPhoto.getMeasuredHeight() + "/" + hostview.m_ivPhoto.getWidth() + "/" + hostview.m_ivPhoto.getMeasuredWidth());

        return arg1;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class HostView {
        View bgView;
        ImageView level, clearImg;
        TextView stageId;
    }

}
