package com.hywy.luanhzt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.common.base.BaseToolbarActivity;
import com.cs.common.handler.SpringViewHandler;
import com.cs.common.listener.OnPostListenter;
import com.cs.common.utils.DialogTools;
import com.cs.common.utils.StringUtils;
import com.hywy.luanhzt.R;
import com.hywy.luanhzt.action.RxAction;
import com.hywy.luanhzt.app.App;
import com.hywy.luanhzt.entity.Account;
import com.hywy.luanhzt.entity.RiverDetails;
import com.hywy.luanhzt.entity.RiverFile;
import com.hywy.luanhzt.entity.UserTree;
import com.hywy.luanhzt.key.Key;
import com.hywy.luanhzt.task.GetRiverDetailsTask;
import com.hywy.luanhzt.task.GetUserTreeDataTask;
import com.hywy.luanhzt.treeview.bean.BranchNode;
import com.hywy.luanhzt.treeview.bean.BranchViewBinder;
import com.hywy.luanhzt.treeview.bean.LeafNode;
import com.hywy.luanhzt.treeview.bean.LeafViewBinder;
import com.hywy.luanhzt.treeview.bean.RootNode;
import com.hywy.luanhzt.treeview.bean.RootViewBinder;
import com.superman.treeview.TreeViewAdapter;
import com.superman.treeview.bean.LayoutItem;
import com.superman.treeview.bean.TreeNode;
import com.superman.treeview.bean.TreeViewBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class UserTreeActivity extends BaseToolbarActivity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.title)
    TextView title;
    private List<UserTree> userTrees;
    private List<TreeNode> list = new ArrayList<>();
    private TreeViewAdapter adapter;
    private int roleId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tree);
        init();
        initAdapter();
        initData();
    }

    public static void startAction(Activity activity) {
        activity.startActivity(new Intent(activity, UserTreeActivity.class));
    }

    private void init() {
        list = new ArrayList<>();
        setTitleBulider(new Bulider().title("选择人员").backicon(R.drawable.ic_arrow_back_white_24dp));
        title.setText("河长");
    }

    @OnClick(R.id.layout_river)
    public void choose() {
        showRadioDialog();
    }

    /**
     */
    private void showRadioDialog() {
        //弹窗显示
        final List<String> list = new ArrayList<>();
        list.add("河长");
        list.add("河长办");
        list.add("成员单位");
        DialogTools.showListViewDialog(this, "提示", list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> params = new HashMap<>();
                title.setText(list.get(i));
                if (i == 0) {
                    roleId = 2;
                    initData();
                } else if (i == 1) {
                    roleId = 3;
                    initData();
                } else if (i == 2) {
                    roleId = 4;
                    initData();
                }
            }
        });
    }


    private Map<String, String> dataMap = new HashMap<String, String>();
    private List<String> strings = new ArrayList<>();

    private void initAdapter() {
        adapter = new TreeViewAdapter(list, Arrays.asList(new RootViewBinder(), new BranchViewBinder(), new LeafViewBinder())) {

            @Override
            public void toggleClick(TreeViewBinder.ViewHolder viewHolder, View view, boolean isOpen, TreeNode treeNode) {
                if (isOpen) {
                    addNewNode(treeNode);
                } else {
                    adapter.lastToggleClickToggle();
                }
            }

            @Override
            public void toggled(TreeViewBinder.ViewHolder viewHolder, View view, boolean isOpen, TreeNode treeNode) {
                viewHolder.findViewById(R.id.ivNode).setRotation(isOpen ? 90 : 0);
            }

            @Override
            public void checked(TreeViewBinder.ViewHolder viewHolder, View view, boolean checked, TreeNode treeNode) {
                if (checked) {
                    saveMap(treeNode);
                }
            }

            @Override
            public void itemClick(TreeViewBinder.ViewHolder viewHolder, View view, TreeNode treeNode) {
                String name = null;
                LayoutItem item = treeNode.getValue();
                if (item instanceof RootNode) {
                    name = ((RootNode) item).getName();
                } else if (item instanceof BranchNode) {
                    name = ((BranchNode) item).getName();
                } else if (item instanceof LeafNode) {
                    name = ((LeafNode) item).getName();
                    mRxManager.post(RxAction.EVENT_CHOOSE_USER, item);
                    finish();
                }
//                Toast.makeText(UserTreeActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void saveMap(TreeNode treeNode) {
        if (treeNode.getChildNodes() != null) {
            List<TreeNode> list = treeNode.getChildNodes();
            for (TreeNode item : list) {
                saveData(item);
                if (!item.getChildNodes().isEmpty()) {
                    saveMap(item);
                }
            }
        }

    }

    private void saveData(TreeNode treeNode) {
        LayoutItem item = treeNode.getValue();
        if (item instanceof RootNode) {
            RootNode rootNode = (RootNode) item;
            strings.add(rootNode.getName());
            dataMap.put(rootNode.getName(), rootNode.getName());
        } else if (item instanceof BranchNode) {
            BranchNode branchNode = (BranchNode) item;
            strings.add(branchNode.getName());
            dataMap.put(branchNode.getName(), branchNode.getName());
        } else if (item instanceof LeafNode) {
            LeafNode leafNode = (LeafNode) item;
            strings.add(leafNode.getName());
            dataMap.put(leafNode.getName(), leafNode.getName());
        }
    }


    private void initData() {
        Account account = App.getInstance().getAccount();
        SpringViewHandler handler = new SpringViewHandler(this);
        Map<String, Object> params = new HashMap<>();
        params.put("ROLE_ID", roleId);//2河长，3河长办，4成员单位
        params.put("AREA", account.getADCD());
        handler.request(params, new GetUserTreeDataTask(this));
        handler.setListener(new OnPostListenter() {
            @Override
            public void OnPostSuccess(Map<String, Object> result) {
                userTrees = (List<UserTree>) result.get(Key.RESULT);
                list.clear();
                if (StringUtils.isNotNullList(userTrees)) {
                    list.addAll(initRoot(userTrees));
                    adapter.notifyData(list);
                }
            }

            @Override
            public void OnPostFail(Map<String, Object> result) {

            }
        });
    }

    /**
     * 初始化根
     *
     * @param userTrees
     * @return
     */
    private List<TreeNode> initRoot(List<UserTree> userTrees) {
        List<TreeNode> rootList = new ArrayList<>();
        for (int i = 0; i < userTrees.size(); i++) {
            List<UserTree> child = userTrees.get(i).getChildren();
            if (StringUtils.isNotNullList(child)) {//如果有分支，继续初始化分支
                TreeNode<RootNode> node = new TreeNode<>(new RootNode(userTrees.get(i).getName(), userTrees.get(i).getId()));
                node.setChildNodes(initBranchs(child));
                rootList.add(node);
            } else {
                TreeNode<LeafNode> node = new TreeNode<>(new LeafNode(userTrees.get(i).getName(), userTrees.get(i).getId()));
                rootList.add(node);
//                List<TreeNode> list = new ArrayList<>();
//                list.add(initLeaf(userTrees.get(i).getName()));
//                node.setChildNodes(list);
            }

        }
        return rootList;
    }

    /**
     * 初始化枝
     *
     * @return
     */
    private List<TreeNode> initBranchs(List<UserTree> userTrees) {
        List<TreeNode> branchList = new ArrayList<>();
        for (int i = 0; i < userTrees.size(); i++) {
            UserTree ut = userTrees.get(i);
            List<UserTree> child = userTrees.get(i).getChildren();
            if (StringUtils.isNotNullList(child)) {//如果有分支，继续初始化分支
                TreeNode<BranchNode> node = new TreeNode<>(new BranchNode(ut.getName(), ut.getId()));
                node.setChildNodes(initBranchs2(child));
                branchList.add(node);
            } else {
//                List<TreeNode> list = new ArrayList<>();
//                list.add(initLeaf(userTrees.get(i).getName()));
//                node.setChildNodes(list);
                TreeNode<LeafNode> node = new TreeNode<>(new LeafNode(userTrees.get(i).getName(), userTrees.get(i).getId()));
                branchList.add(node);
            }
//            branchList.add(node);
        }
        return branchList;
    }

    /**
     * 初始化枝
     *
     * @return
     */
    private List<TreeNode> initBranchs2(List<UserTree> userTrees) {
        List<TreeNode> branchList = new ArrayList<>();
        for (int i = 0; i < userTrees.size(); i++) {
            UserTree ut = userTrees.get(i);
            List<UserTree> child = userTrees.get(i).getChildren();
            if (StringUtils.isNotNullList(child)) {//如果有分支，继续初始化分支
                TreeNode<BranchNode> node = new TreeNode<>(new BranchNode(ut.getName(), ut.getId()));
                node.setChildNodes(initLeaves(child));
                branchList.add(node);
            } else {
//                List<TreeNode> list = new ArrayList<>();
//                list.add(initLeaf(userTrees.get(i).getName()));
//                node.setChildNodes(list);
                TreeNode<LeafNode> node = new TreeNode<>(new LeafNode(userTrees.get(i).getName(), userTrees.get(i).getId()));
                branchList.add(node);
            }
//            branchList.add(node);
        }
        return branchList;
    }

    /**
     * 初始化叶
     *
     * @return
     */
    private List<TreeNode> initLeaves(List<UserTree> userTrees) {
        List<TreeNode> leafList = new ArrayList<>();
        for (int i = 0; i < userTrees.size(); i++) {
            TreeNode<LeafNode> node = new TreeNode<>(new LeafNode(userTrees.get(i).getName(), userTrees.get(i).getId()));
//            List<UserTree> child = userTrees.get(i).getChildren();
//            if (StringUtils.isNotNullList(child)) {//如果有分支，继续初始化分支
//                node.setChildNodes(initLeaves(child));
//            } else {
//                node.addChild(initLeaf(userTrees.get(i).getName()));
//            }
            leafList.add(node);
        }
        return leafList;
    }


    private void addNewNode(final TreeNode treeNode) {
        autoProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String name = null;
                LayoutItem item = treeNode.getValue();
                if (item instanceof RootNode) {
                    name = ((RootNode) item).getName();
                } else if (item instanceof BranchNode) {
                    name = ((BranchNode) item).getName();
                } else if (item instanceof LeafNode) {
                    name = ((LeafNode) item).getName();
                }
                List<TreeNode> list = treeNode.getChildNodes();
                boolean hasLeaf = false;
                for (TreeNode child : list) {
                    if (child.getValue() instanceof LeafNode) {
                        hasLeaf = true;
                        break;
                    }
                }
                autoProgress(false);
                adapter.lastToggleClickToggle();
            }
        }, 300);
    }

    private ProgressDialog progressDialog;

    private void autoProgress(boolean show) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载数据。。。");
        }
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void save() {
    }
}
