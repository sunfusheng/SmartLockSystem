package com.smart.doorlock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.doorlock.entity.PwdEntity;
import com.smart.doorlock.util.TimeUtil;
import com.smart.lock.R;
import com.smart.doorlock.adapter.base.BaseListAdapter;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class PwdAdapter extends BaseListAdapter<PwdEntity> {

    public PwdAdapter(Context context, List<PwdEntity> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pwd_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PwdEntity entity = getItem(position);
        holder.tvPwdTitle.setText(entity.getTitle());
        holder.tvPwdAuthor.setText(entity.getAuthor());
        holder.tvPwdTime.setText(TimeUtil.convertDateToString(Calendar.getInstance().getTimeInMillis()));

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_pwd_title)
        TextView tvPwdTitle;
        @InjectView(R.id.tv_pwd_author)
        TextView tvPwdAuthor;
        @InjectView(R.id.tv_pwd_time)
        TextView tvPwdTime;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
