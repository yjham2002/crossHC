package kr.co.picklecode.crossmedia.hiddencatch.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import kr.co.picklecode.crossmedia.hiddencatch.R;
import kr.co.picklecode.crossmedia.hiddencatch.model.StageBox;
import kr.co.picklecode.crossmedia.hiddencatch.util.StageUtil;

public class StageGridAdapter extends BaseAdapter {
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
        ImageView level;

        if (arg1 == null) {
            hostview = new HostView();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_grid_stage, null, true);
            hostview.stageId = arg1.findViewById(R.id.stageId);
            hostview.level = arg1.findViewById(R.id.level);

            arg1.setTag(hostview);
        } else {
            hostview = (HostView) arg1.getTag();
        }

        if(winInfo.containsKey(StageUtil.genKeyForWinInfo(stageBox.getId()))){
            final int count = winInfo.get(StageUtil.genKeyForWinInfo(stageBox.getId()));
            hostview.level.setVisibility(View.VISIBLE);
        }else{
            hostview.level.setVisibility(View.INVISIBLE);
        }

        hostview.stageId.setText((arg0 + 1) + "");

//        Log.e("sizeInfo", hostview.m_ivPhoto.getHeight() + "/" + hostview.m_ivPhoto.getMeasuredHeight() + "/" + hostview.m_ivPhoto.getWidth() + "/" + hostview.m_ivPhoto.getMeasuredWidth());

        return arg1;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class HostView {
        ImageView level;
        TextView stageId;
    }

}
