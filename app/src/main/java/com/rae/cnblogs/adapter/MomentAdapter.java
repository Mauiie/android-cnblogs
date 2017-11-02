package com.rae.cnblogs.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rae.cnblogs.R;
import com.rae.cnblogs.RaeImageLoader;
import com.rae.cnblogs.model.MomentHolder;
import com.rae.cnblogs.sdk.UserProvider;
import com.rae.cnblogs.sdk.bean.MomentBean;
import com.rae.cnblogs.sdk.bean.UserInfoBean;
import com.rae.swift.Rx;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 闪存
 * Created by ChenRui on 2017/10/27 0027 10:49.
 */
public class MomentAdapter extends BaseItemAdapter<MomentBean, MomentHolder> implements View.OnClickListener {

    String blogApp;

    public MomentAdapter() {
        initUserInfo();
    }

    private void initUserInfo() {
        UserInfoBean userInfoBean = UserProvider.getInstance().getLoginUserInfo();
        if (userInfoBean != null) {
            blogApp = userInfoBean.getBlogApp();
        }
    }

    @Override
    public void invalidate(List<MomentBean> data) {
        super.invalidate(data);
        initUserInfo();
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnBloggerClickListener {
        void onBloggerClick(String blogApp);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String ingId, int position);
    }

    private OnBloggerClickListener mOnBloggerClickListener;
    private OnDeleteClickListener mOnDeleteClickListener;

    public void setOnBloggerClickListener(OnBloggerClickListener onBloggerClickListener) {
        mOnBloggerClickListener = onBloggerClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        mOnDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public MomentHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new MomentHolder(inflateView(parent, R.layout.item_moment_list));
    }

    @Override
    public void onBindViewHolder(MomentHolder holder, int position, MomentBean m) {
        int imageCount = Rx.getCount(m.getImageList());
        holder.mRecyclerView.setVisibility(imageCount > 1 ? View.GONE : View.VISIBLE);

        if (imageCount > 1) {
            int spanCount = imageCount == 4 || imageCount == 2 ? 2 : 3;
            holder.mRecyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), spanCount));
            holder.mRecyclerView.setAdapter(new MomentImageAdapter(m.getImageList()));
        } else {
            holder.mRecyclerView.getLayoutManager().removeAllViews();
            holder.mRecyclerView.removeAllViews();
        }


        View.OnClickListener onClickListener = TextUtils.isEmpty(m.getBlogApp()) ? null : new ItemBloggerClickListener(m.getBlogApp(), mOnBloggerClickListener);
        holder.authorView.setOnClickListener(onClickListener);
        holder.avatarView.setOnClickListener(onClickListener);
        holder.dateView.setOnClickListener(onClickListener);

        holder.thumbView.setVisibility(imageCount == 1 ? View.VISIBLE : View.GONE);
        if (imageCount == 1) {
            RaeImageLoader.displayHeaderImage(m.getImageList().get(0), holder.thumbView);
        }

        RaeImageLoader.displayHeaderImage(m.getAvatar(), holder.avatarView);
        holder.deleteView.setVisibility(TextUtils.equals(blogApp, m.getBlogApp()) ? View.VISIBLE : View.GONE);
        holder.deleteView.setOnClickListener(new ItemDeleteClickListener(m.getId(), position, mOnDeleteClickListener));
        holder.authorView.setText(m.getAuthorName());
        holder.dateView.setText(m.getPostTime());
        holder.summaryView.setText(m.getContent());
        holder.commentView.setText(m.getCommentCount());
    }

    private static class ItemDeleteClickListener implements View.OnClickListener {

        private final WeakReference<OnDeleteClickListener> mOnDeleteClickListener;
        private final String mId;
        private final int mPosition;

        public ItemDeleteClickListener(String id, int position, OnDeleteClickListener onDeleteClickListener) {
            mId = id;
            mPosition = position;
            mOnDeleteClickListener = new WeakReference<>(onDeleteClickListener);
        }

        @Override
        public void onClick(View v) {
            if (mOnDeleteClickListener.get() != null) {
                mOnDeleteClickListener.get().onDeleteClick(mId, mPosition);
            }
        }
    }


    private static class ItemBloggerClickListener implements View.OnClickListener {
        private String blogApp;
        private WeakReference<OnBloggerClickListener> mOnBloggerClickListenerWeakReference;

        public ItemBloggerClickListener(String blogApp, OnBloggerClickListener onBloggerClickListener) {
            this.blogApp = blogApp;
            mOnBloggerClickListenerWeakReference = new WeakReference<OnBloggerClickListener>(onBloggerClickListener);
        }

        @Override
        public void onClick(View v) {
            if (mOnBloggerClickListenerWeakReference.get() != null) {
                mOnBloggerClickListenerWeakReference.get().onBloggerClick(blogApp);
            }
        }
    }
}