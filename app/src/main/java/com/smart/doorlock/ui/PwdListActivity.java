package com.smart.doorlock.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.smart.doorlock.R;
import com.smart.doorlock.SmartLockApplication;
import com.smart.doorlock.adapter.PwdAdapter;
import com.smart.doorlock.ble.BleProto;
import com.smart.doorlock.entity.PwdEntity;
import com.smart.doorlock.ui.base.BaseActivity;
import com.smart.doorlock.util.DisplayUtil;
import com.smart.doorlock.widget.swipemenulistview.SwipeMenu;
import com.smart.doorlock.widget.swipemenulistview.SwipeMenuCreator;
import com.smart.doorlock.widget.swipemenulistview.SwipeMenuItem;
import com.smart.doorlock.widget.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class PwdListActivity extends BaseActivity {

    private BluetoothLeService mBluetoothLeService = null;
    private final static String TAG = BleProto.DEBUG_TAG;
    private ProgressDialog process_dialog = null;
    private boolean HANDLER_FLAG = true;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.smlv_listView)
    SwipeMenuListView smlvListView;

    private List<PwdEntity> pwdList;
    private PwdAdapter pwdAdapter;
    private int pwd_index = 0;
    private int pwd_total = 0;
    private final int READ_NUM_MAX = 10;
    private int cur_index = 0;
    private String edit_pwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_list);
        ButterKnife.inject(this);
        mBluetoothLeService = ((SmartLockApplication) getApplication()).getBleService();

        initActionBar();
        initData();
        initView();

        registerReceiver(mPwdDataReceiver, makeDataUpdateFilter());
        requestPwdList(pwd_index);

        process_dialog = new ProgressDialog(PwdListActivity.this);
        process_dialog.setTitle(getResources().getString(
                R.string.read_pwd_set_title));
        process_dialog.setMessage(
                "\n"
                        + getResources().getString(R.string.reading_pwd_set)
                        + "\n"
                        + "\n"
                        + getResources().getString(R.string.alert_message_wait));
        process_dialog.show();
        HANDLER_FLAG = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HANDLER_FLAG) {
                    HANDLER_FLAG = false;
                    process_dialog.dismiss();
                    try {
                        Toast.makeText(PwdListActivity.this,
                                R.string.reading_set_overtime,
                                Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        }, 3000);
    }

    private void requestPwdList(int indexm) {

        mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_READ_PASSWORD, null));
    }

    private void requestModifyPwd(int index, String pwd) {
        byte[] proto_data = new byte[5];

        if ((pwd == null) || (pwd.length() == 0)) {
            proto_data[1] = (byte) 0xff;
            proto_data[2] = (byte) 0xff;
            proto_data[3] = (byte) 0xff;
            proto_data[4] = (byte) 0xff;
        } else {
            byte[] bcd_data = BleProto.stringToPwdBcdData(pwd);
            System.arraycopy(bcd_data,0,proto_data,1,4);
        }
        proto_data[0] = (byte) index;
        mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_MODIFY_PASSWORD, proto_data));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPwdDataReceiver);
        HANDLER_FLAG = false;
    }

    private void initActionBar() {
        mToolbar.setTitle("密码配置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        pwdList = new ArrayList<PwdEntity>();
    }

    private void initView() {
        initSwipeMenuListView();
        pwdAdapter = new PwdAdapter(this, pwdList);
        smlvListView.setAdapter(pwdAdapter);
        setSwipeMenuCreator();
        initSwipeMenuItemClickListener();
    }

    private void initSwipeMenuListView() {
        smlvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void setSwipeMenuCreator() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem modifyItem = new SwipeMenuItem(getApplicationContext());
                modifyItem.setBackground(new ColorDrawable(getResources().getColor(R.color.font_black_6)));
                modifyItem.setWidth(DisplayUtil.dip2px(PwdListActivity.this, 90));
                modifyItem.setTitle("修改");
                modifyItem.setTitleSize(16);
                modifyItem.setTitleColor(getResources().getColor(R.color.font_black_2));

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(PwdListActivity.this, 90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);

                menu.addMenuItem(modifyItem);
                menu.addMenuItem(deleteItem);
            }
        };
        smlvListView.setMenuCreator(creator);
    }

    private void initSwipeMenuItemClickListener() {
        smlvListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //Toast.makeText(PwdListActivity.this, "修改", Toast.LENGTH_SHORT).show();
                        final int modify_position = position;
                        PwdEntity entity = pwdList.get(position);
                        final EditText et = new EditText(PwdListActivity.this);

                        et.setFilters(new InputFilter[]{
                                new InputFilter.LengthFilter(6)
                        });
                        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

                        if (entity.getPassword().equals("(空)"))
                            et.setText("");
                        else
                            et.setText(entity.getPassword());

                        //
                        new AlertDialog.Builder(PwdListActivity.this).setTitle("请输入密码")
                                .setIcon(R.drawable.icon_password).setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //数据获取
                                        deletePwdListItem(false,modify_position,et.getText().toString());
                                    }
                                }).setNegativeButton("取消", null).show();
                        break;
                    case 1:
                        deletePwdListItem(true,position,null);
                        break;
                }
                return true;
            }
        });
    }

    private void deletePwdListItem(boolean is_delete,int position,String new_pwd) {
        PwdEntity entity = pwdList.get(position);
        if (is_delete){
            if (entity.getPassword().equals("(空)")) {
                Toast.makeText(PwdListActivity.this, "密码已经为空！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        cur_index = position;
        if (is_delete)
            edit_pwd = "(空)";
        else
            edit_pwd = new_pwd;
        requestModifyPwd(position, edit_pwd);

        process_dialog = new ProgressDialog(PwdListActivity.this);
        process_dialog.setTitle(getResources().getString(
                R.string.modify_pwd_set_title));
        process_dialog.setMessage(
                "\n"
                        + getResources().getString(R.string.modifying_pwd_set)
                        + "\n"
                        + "\n"
                        + getResources().getString(R.string.alert_message_wait));
        process_dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HANDLER_FLAG) {
                    HANDLER_FLAG = false;
                    process_dialog.dismiss();
                    try {
                        Toast.makeText(PwdListActivity.this,
                                R.string.process_fail,
                                Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 3000);
    }

    private void updatePwdItem(String pwd) {

        PwdEntity entity = pwdList.get(cur_index);
        entity.setPassword(pwd);
        pwdList.set(cur_index, entity);

        pwdAdapter.notifyDataSetChanged();

        if (process_dialog != null && process_dialog.isShowing()) {
            process_dialog.dismiss();
        }
        HANDLER_FLAG = false;

        Toast.makeText(PwdListActivity.this,
                R.string.process_succ,
                Toast.LENGTH_SHORT).show();
    }
	
	
	private void formatPwdList(byte[] pwd_data)
	{	
		int length = (pwd_data[BleProto.PROTO_RSP_LENGTH_POS]<<8) + pwd_data[BleProto.PROTO_RSP_LENGTH_POS+1] - 1;
        int pwd_cnt = 0;
        byte[] bcd_data = new byte[BleProto.PROTO_PWD_LEN];

        for (int i = 0;i < length;i += BleProto.PROTO_PWD_LEN)
        {
            System.arraycopy( pwd_data, BleProto.PROTO_RSP_VALID_DATA_POS+i,bcd_data, 0, BleProto.PROTO_PWD_LEN);
            String pwd_item = BleProto.bcdByteToString(bcd_data,BleProto.PROTO_PWD_LEN);
            if (pwd_item == null)
                pwd_item = "(空)";
            pwd_cnt += 1;
            pwdList.add(new PwdEntity("", pwd_item,  getString(R.string.str_index) + pwd_cnt + getString(R.string.str_group) + getString(R.string.str_pwd)));
            Log.e(TAG, "formatPwdList  pwd_item = " + pwd_item + " pwd_cnt = " + pwd_cnt);
        }
        pwdAdapter.notifyDataSetChanged();

        if (process_dialog != null && process_dialog.isShowing()) {
            process_dialog.dismiss();
        }
        HANDLER_FLAG = false;
	}
	
	private static IntentFilter makeDataUpdateFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		
		intentFilter.addAction(BleProto.PWD_PROTO_RSP_ACTION);
		return intentFilter;
	}

	
	private final BroadcastReceiver mPwdDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

            Log.e(TAG, "mPwdDataReceiver  action = " + action);
			if (BleProto.PWD_PROTO_RSP_ACTION.equals(action)) {
				
				byte[] rcv_data = intent.getByteArrayExtra(BleProto.PROTO_RSP_DATA);
				if (rcv_data != null) {
                    if ((rcv_data[BleProto.PROTO_CMD_POS]&0xff) == BleProto.PROTO_READ_PASSWORD){
					    formatPwdList(rcv_data);
                        Log.e(TAG, "mPwdDataReceiver  length = " + rcv_data.length + " cmd = " + rcv_data[BleProto.PROTO_CMD_POS]);
                    }
                    else if ((rcv_data[BleProto.PROTO_CMD_POS]&0xff) == BleProto.PROTO_MODIFY_PASSWORD){
                        updatePwdItem(edit_pwd);
                    }
                }
			}
		}
	};
}
