package com.sobey.tvcust.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.tvcust.R;
import com.sobey.tvcust.common.AppData;
import com.sobey.tvcust.common.CommonNet;
import com.sobey.tvcust.common.LoadingViewUtil;
import com.sobey.tvcust.entity.MsgOrder;
import com.sobey.tvcust.entity.MsgOrderPojo;
import com.sobey.tvcust.entity.Order;
import com.sobey.tvcust.entity.OrderPojo;
import com.sobey.tvcust.ui.adapter.RecycleAdapterMsgOrder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MsgOrderActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<MsgOrder> results = new ArrayList<>();
    private RecycleAdapterMsgOrder adapter;

    private ViewGroup showingroup;
    private View showin;
    private int page;
    private final int PAGE_COUNT = 5;

    private Callback.Cancelable cancelable;
    private Callback.Cancelable cancelablemore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgorder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initData();
        initCtrl();
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_msg);
        springView = (SpringView) findViewById(R.id.spring_msg);
    }

    private void initData() {
        freshData(true);
    }

    private void freshData(final boolean isFirst){
        if (cancelable != null) {
            cancelable.cancel();
        }
        if (cancelablemore != null) {
            cancelablemore.cancel();
        }
        final RequestParams params = new RequestParams(AppData.Url.msglist);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("type", 1 + "");
        params.addBodyParameter("pageNO", 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelable = CommonNet.samplepost(params, MsgOrderPojo.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, text);
                else {
                    MsgOrderPojo msgOrderPojo = (MsgOrderPojo) pojo;
                    List<MsgOrder> msgs = msgOrderPojo.getDataList();
                    //有数据才添加，否则显示lack信息
                    if (msgs != null && msgs.size() != 0) {
                        List<MsgOrder> results = adapter.getResults();
                        results.clear();
                        results.addAll(msgs);
                        freshCtrl();
                        page = 1;
                        if (isFirst) {
                            LoadingViewUtil.showout(showingroup, showin);
                        } else {
                            springView.onFinishFreshAndLoad();
                        }
                    } else {
                        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_lack, showin, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initData();
                            }
                        });
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                if (isFirst) {
                    Toast.makeText(MsgOrderActivity.this, text, Toast.LENGTH_SHORT).show();
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                } else {
                    springView.onFinishFreshAndLoad();
                }
            }

            @Override
            public void netStart(int code) {
                if (isFirst) {
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
                }
            }
        });
    }

    private boolean isloadmore = false;
    private void loadMoreData() {
        if (isloadmore) return;
        final RequestParams params = new RequestParams(AppData.Url.msglist);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("type", 1 + "");
        params.addBodyParameter("pageNO", page + 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelablemore = CommonNet.samplepost(params, MsgOrderPojo.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, text);
                else {
                    MsgOrderPojo msgOrderPojo = (MsgOrderPojo) pojo;
                    List<MsgOrder> msgs = msgOrderPojo.getDataList();
                    if (msgs.size() != 0) {
                        List<MsgOrder> results = adapter.getResults();
                        results.addAll(msgs);
                        freshCtrl();
                        page++;
                    } else {
                        Snackbar.make(showingroup, "没有更多的数据了", Snackbar.LENGTH_SHORT).show();
//                        Toast.makeText(getActivity(),"没有更多的数据了",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(MsgOrderActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int code) {
                isloadmore = true;
            }

            @Override
            public void netEnd(int code) {
                isloadmore = false;
                springView.onFinishFreshAndLoad();
            }
        });
    }

    private void initCtrl() {
        adapter = new RecycleAdapterMsgOrder(this,R.layout.item_recycle_msgorder,results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        springView.setHeader(new AliHeader(this,false));
        springView.setFooter(new AliFooter(this,false));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                freshData(false);
            }

            @Override
            public void onLoadmore() {
                loadMoreData();
            }
        });
    }

    private void freshCtrl(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
