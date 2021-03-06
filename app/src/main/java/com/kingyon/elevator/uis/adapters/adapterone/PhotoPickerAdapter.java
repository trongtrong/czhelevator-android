package com.kingyon.elevator.uis.adapters.adapterone;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kingyon.elevator.R;
import com.kingyon.elevator.photopicker.MediaData;
import com.kingyon.elevator.utils.DensityUtil;
import com.kingyon.elevator.utils.utilstwo.VideoUtils;
import com.leo.afbaselibrary.utils.GlideUtils;
import com.zhaoss.weixinrecorded.util.TimeUtils;
import com.zhihu.matisse.internal.entity.SelectionSpec;

import java.io.File;
import java.util.List;

/**
 * Created By SongPeng  on 2019/12/13
 * Email : 1531603384@qq.com
 */
public class PhotoPickerAdapter extends BaseAdapter {

    private List<MediaData> mediaDataList;
    private LayoutInflater mInflater;
    Context context;
    private int width;
    private int showType;

    public PhotoPickerAdapter(Context context, int showType, List<MediaData> mediaDataList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mediaDataList = mediaDataList;
        this.showType = showType;
        width = (ScreenUtils.getScreenWidth() - DensityUtil.dip2px(15)) / 3;
    }

    public void reflashData(List<MediaData> mediaDataList) {
        this.mediaDataList = mediaDataList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mediaDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.photo_picker_grid_item_layout, null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
            convertView.setLayoutParams(layoutParams);
            holder.iv_preview = convertView.findViewById(R.id.iv_preview);
            holder.video_time = convertView.findViewById(R.id.video_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaData mediaData = mediaDataList.get(position);
        if (showType == 1) {
            holder.video_time.setVisibility(View.VISIBLE);
            holder.video_time.setText(TimeUtils.secondToTime( (int) mediaData.getDuration()/1000));
            Glide.with(context).load(Uri.fromFile(new File(mediaData.getOriginalPath())))
                    .into(holder.iv_preview);
        } else {
            holder.video_time.setVisibility(View.GONE);
            holder.video_time.setText("");
            GlideUtils.loadLocalFrame(context, mediaData.getOriginalPath(), holder.iv_preview);
        }
        if (mediaData.getOriginalPath() != null) {
        } else {
            holder.iv_preview.setImageResource(R.drawable.bg_picture_space_article);
        }
        return convertView;
    }

    public final class ViewHolder {
        ImageView iv_preview;
        TextView video_time;

    }

}
