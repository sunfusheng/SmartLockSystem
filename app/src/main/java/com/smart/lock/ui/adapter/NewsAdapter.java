package com.smart.lock.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.smart.lock.R;
import com.smart.lock.ui.adapter.base.BaseListAdapter;
import com.smart.lock.ui.entity.NewsEntity;
import com.smart.lock.util.IsNullOrEmpty;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/2/5.
 */
public class NewsAdapter extends BaseListAdapter<NewsEntity> {

    private BitmapUtils bitmapUtils;

    public NewsAdapter(Context context, List<NewsEntity> list) {
        super(context, list);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NewsEntity entity = getItem(position);
        if (!IsNullOrEmpty.isEmpty(entity.getIcon())) {
            bitmapUtils.display(holder.mIvNewsIcon, entity.getIcon());
        }
        holder.mTvNewsTitle.setText(entity.getArticle()+"");
        holder.mTvNewsContent.setText(entity.getSource()+"");

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_news_icon)
        ImageView mIvNewsIcon;
        @InjectView(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @InjectView(R.id.tv_news_content)
        TextView mTvNewsContent;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
