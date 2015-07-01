package com.smart.lock.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.smart.lock.R;
import com.smart.lock.ui.adapter.NewsAdapter;
import com.smart.lock.ui.base.BaseActivity;
import com.smart.lock.ui.entity.NewsEntity;
import com.smart.lock.util.DisplayUtil;
import com.smart.lock.widget.refreshswipemenulistview.XListView;
import com.smart.lock.widget.swipemenulistview.SwipeMenu;
import com.smart.lock.widget.swipemenulistview.SwipeMenuCreator;
import com.smart.lock.widget.swipemenulistview.SwipeMenuItem;
import com.smart.lock.widget.swipemenulistview.SwipeMenuListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class NewsActivity extends BaseActivity implements XListView.IXListViewListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.xlv_listView)
    XListView mXlvListView;

    private List<NewsEntity> newsList;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.inject(this);

        initActionBar();
        initData();
        initView();
    }

    private void initActionBar() {
        mToolbar.setTitle("新闻");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        newsList = NewsEntity.listAll(NewsEntity.class);
    }

    private void initView() {
        initXlistView();
        newsAdapter = new NewsAdapter(this, newsList);
        mXlvListView.setAdapter(newsAdapter);
        setSwipeMenuCreator();
        initSwipeMenuItemClickListener();
    }

    private void initXlistView() {
        mXlvListView.setXListViewListener(this);
        mXlvListView.setPullRefreshEnable(true);
        mXlvListView.setPullLoadEnable(true);
        mXlvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void setSwipeMenuCreator() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                openItem.setWidth(DisplayUtil.dip2px(NewsActivity.this, 90));
                openItem.setTitle("删除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        mXlvListView.setMenuCreator(creator);
    }

    private void initSwipeMenuItemClickListener() {
        mXlvListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteNewsListItem(position);
                        break;
                }
                return true;
            }
        });
    }

    private void deleteNewsListItem(int position) {
        NewsEntity entity = newsList.get(position);
        entity.delete();
        newsList.remove(entity);
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mXlvListView.stopRefresh();
                mXlvListView.setRefreshTime("刚刚");
                newsAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mXlvListView.stopLoadMore();
                newsAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }
}
