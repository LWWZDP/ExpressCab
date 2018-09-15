package com.example.expresscab;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Entity.GetExpItem;
import com.example.Entity.Order;
import com.example.Entity.info.AddrInfo;
import com.example.Entity.info.LoginInfo;
import com.example.mytools.GetExpAdapter;
import com.example.mytools.GlobalData;
import com.example.mytools.HttpUtil;
import com.example.mytools.JsonParseUtil;
import com.example.mytools.StrUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetExpActivity extends AppCompatActivity {

    private String TAG = "取件";

    private List<Order> orderList = new ArrayList<>();

    private RecyclerView all_exp_rv;

    private TextView my_exp_title;

    private List<GetExpItem> myExpList = new ArrayList<>();

    private Handler olHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        orderList = (List<Order>) msg.obj;
        if (orderList != null) {
            my_exp_title.setText("您共有" + orderList.size() + "件快递");
            Log.d(TAG, "handleMessage: count:" + orderList.size());
            for (Order order : orderList) {
                Log.d(TAG, "handleMessage: id: " + order.getId());
                Log.d(TAG, "handleMessage: in_time" + order.getIn_time());
                Log.d(TAG, "handleMessage: addr" + order.getAddr());
                AddrInfo addrInfo = order.getAddr_info();
                Log.d(TAG, "handleMessage: type:" + addrInfo.getType());
                Log.d(TAG, "handleMessage: code:" + addrInfo.getCode());
                Log.d(TAG, "handleMessage: addr:" + addrInfo.getAddr());
                Log.d(TAG, "handleMessage: desc:" + addrInfo.getDesc());

                GetExpItem getExpItem = new GetExpItem();
                String [] desc = StrUtil.DescSplit(addrInfo.getDesc());
                getExpItem.setDesc_type(desc[0]);
                getExpItem.setDesc_code(desc[1]);
                getExpItem.setExp_code(order.getId());
                getExpItem.setAddr(addrInfo.getAddr());
                getExpItem.setIn_time(order.getIn_time());
                getExpItem.setOrder_id(order.getId());
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
                myExpList.add(getExpItem);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(GetExpActivity.this);
        all_exp_rv.setLayoutManager(layoutManager);
        GetExpAdapter adapter = new GetExpAdapter(myExpList);
        all_exp_rv.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_exp);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        invokeGetAllAPI(GlobalData.getUid(), "1", GlobalData.getSid());
    }

    void invokeGetAllAPI(String uid, String status, String sid){
        String api_url = "http://101.200.89.170:9002/sexp/order/all/list";
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", uid)
                .add("status", status)
                .add("sid", sid)
                .build();
        HttpUtil.sendOkHttpRequest(api_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "异常信息：\n" + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "onResponse: " + responseData);
                orderList = JsonParseUtil.parseForAllList(responseData);
                Message message = new Message();
                message.obj = orderList;
                olHandler.sendMessage(message);
            }
        }, requestBody);
    }

    void initView(){
        all_exp_rv = findViewById(R.id.all_exp_rv);
        my_exp_title = findViewById(R.id.my_exp_title);
    }
}