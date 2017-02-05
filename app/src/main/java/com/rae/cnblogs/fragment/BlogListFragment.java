package com.rae.cnblogs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rae.cnblogs.R;
import com.rae.cnblogs.adapter.BlogListItemAdapter;
import com.rae.cnblogs.presenter.CnblogsPresenterFactory;
import com.rae.cnblogs.presenter.IBlogListPresenter;
import com.rae.cnblogs.sdk.bean.Blog;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.cnblogs.sdk.bean.Category;
import com.rae.cnblogs.widget.AppLayout;
import com.rae.cnblogs.widget.RaeRecyclerView;

import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 博客列表
 * Created by ChenRui on 2016/12/2 00:33.
 */
public class BlogListFragment extends BaseFragment implements IBlogListPresenter.IBlogListView {


    public static BlogListFragment newInstance(Category category, BlogType type) {
        Bundle args = new Bundle();
        args.putParcelable("category", category);
        args.putString("blogType", type.getTypeName());
        BlogListFragment fragment = new BlogListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.content)
    AppLayout mAppLayout;

    @BindView(R.id.rec_blog_list)
    RaeRecyclerView mRecyclerView;

    protected Category mCategory;
    protected BlogType mBlogType;

    protected IBlogListPresenter mBlogListPresenter;
    protected BlogListItemAdapter mItemAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fm_blog_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getParcelable("category");
        mBlogType = BlogType.typeOf(getArguments().getString("type"));
        mItemAdapter = new BlogListItemAdapter(mBlogType);
        mBlogListPresenter = CnblogsPresenterFactory.getBlogListPresenter(getContext(), mBlogType, this);
    }

    protected String getTitle() {
        return mCategory.getName();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setAdapter(mItemAdapter);
        mAppLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mBlogListPresenter.start();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mRecyclerView.isOnTop();
            }
        });
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                mBlogListPresenter.loadMore();
            }
        });

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBlogListPresenter.start();
    }

    @Override
    public void onLoadBlogList(int page, List<Blog> data) {
        if (page <= 1)
            mAppLayout.refreshComplete();
        else
            mRecyclerView.loadMoreComplete();

        mItemAdapter.invalidate(data);
        mItemAdapter.notifyDataSetChanged();

        // 异步下载博文内容
//        Intent intent = new Intent(BlogService.ACTION_OFFLINE_BLOG_CONTENT);
//        intent.setClass(getContext(), BlogService.class);
//        getContext().startService(intent);
    }

    @Override
    public void onLoadFailed(int page, String msg) {
        if (page <= 1)
            mAppLayout.refreshComplete();
        else
            mRecyclerView.loadMoreComplete();
    }

    @Override
    public Category getCategory() {
        return mCategory;
    }

    /**
     * 滚动到顶部
     */
    public void scrollToTop() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() <= 1) {
            mAppLayout.autoRefresh();
            return;
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

}
