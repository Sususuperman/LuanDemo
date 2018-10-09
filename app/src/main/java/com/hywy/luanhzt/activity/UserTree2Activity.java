package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.entity.AdcdTree;
import com.hywy.luanhzt.entity.action.AdcdMultiAction;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetAdcdTreeDataTask;
import com.superman.treeview.adapter.RecyclerViewAdapter;
import com.superman.treeview.bean.TreeNode;
import com.superman.treeview.treelist.Node;
import com.superman.treeview.treelist.OnTreeNodeCheckedChangeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class UserTree2Activity extends BaseToolbarActivity {
    @Bind(R.id.cb)
    CheckBox checkBox;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    private List<AdcdTree> adcdTrees;
    private List<TreeNode> list = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    private List<Node> dataList = new ArrayList<>();
    private List<Node> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tree);
        init();
        initData();
    }

    private static final int request_code_adcdtree = 12011;

    public static void startAction(Activity activity, String json) {
        Intent intent = new Intent(activity, UserTree2Activity.class);
        intent.putExtra("nodes", json);
        activity.startActivityForResult(intent, request_code_adcdtree);
    }

    private void init() {
        list = new ArrayList<>();
        findViewById(R.id.layout_river).setVisibility(View.GONE);
        setTitleBulider(new Bulider().title("选择发布对象").backicon(R.drawable.ic_arrow_back_white_24dp));
        String json = getIntent().getStringExtra("nodes");
        selectList = new Gson().fromJson(json, new TypeToken<List<Node>>() {
        }.getType());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    List<Node> list = adapter.getAllNodes();
                    for (Node node : list) {
                        node.setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    List<Node> list = adapter.getAllNodes();
                    for (Node node : list) {
                        node.setChecked(false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private Map<String, String> dataMap = new HashMap<String, String>();
    private List<String> strings = new ArrayList<>();

    private void initAdapter() {
        //第一个参数  ListView
        //第二个参数  上下文
        //第三个参数  数据集
        //第四个参数  默认展开层级数 0为不展开
        //第五个参数  展开的图标
        //第六个参数  闭合的图标
        adapter = new RecyclerViewAdapter(recyclerView, this, dataList,
                0, R.drawable.bt_arrow_up_gray, R.drawable.bt_arrow_down_gray);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //选中状态监听
        adapter.setCheckedChangeListener(new OnTreeNodeCheckedChangeListener() {
            @Override
            public void onCheckChange(Node node, int position, boolean isChecked) {
                if (!isChecked) {
                    checkBox.setChecked(false);
                }
//                for (Node n : selectedNode) {
//                    Log.e("xyh", "onCheckChange: " + n.getName());
//                    StringBuilder sbId = new StringBuilder();
//                    StringBuilder sbName = new StringBuilder();
//                    sbId.append(n.getId() + ",");
//                    sbName.append(n.getName() + ",");
//                }
            }
        });
    }


    private void initData() {
        Account account = App.getInstance().getAccount();
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
//        params.put("ROLE_ID", "2");
//        params.put("AREA", account.getADCD());
        params.put("ADCD", account.getADCD());
        handler.request(params, new GetAdcdTreeDataTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                adcdTrees = (List<AdcdTree>) result.get(Key.RESULT);
                findViewById(R.id.layout_all).setVisibility(View.VISIBLE);
                initNodes(adcdTrees);
                initAdapter();
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     * 递归初始化树状数据
     *
     * @param adcdTrees
     */
    private void initNodes(List<AdcdTree> adcdTrees) {
        if (StringUtils.isNotNullList(adcdTrees)) {
            for (AdcdTree adcdTree : adcdTrees) {
                dataList.add(new Node<>(adcdTree.getId(), adcdTree.getPId(), adcdTree.getName()));
                if (StringUtils.isNotNullList(adcdTree.getChildren())) {
                    initNodes(adcdTree.getChildren());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            //获取所有选中节点
            List<Node> selectedNode = adapter.getSelectedNode();
            mRxManager.post(RxAction.YUJING_CHOOSE_ADCD, new AdcdMultiAction(selectedNode));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
