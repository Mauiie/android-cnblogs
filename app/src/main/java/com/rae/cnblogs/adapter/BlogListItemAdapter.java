package com.rae.cnblogs.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rae.cnblogs.AppRoute;
import com.rae.cnblogs.R;
import com.rae.cnblogs.RaeImageLoader;
import com.rae.cnblogs.model.BlogItemViewHolder;
import com.rae.cnblogs.model.ItemLoadingViewHolder;
import com.rae.cnblogs.sdk.bean.Blog;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.core.Rae;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客列表ITEM
 * Created by ChenRui on 2016/12/2 0002 19:43.
 */
public class BlogListItemAdapter extends BaseItemAdapter<Blog, RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_LOADING = 2;

    /**
     * 没有用户信息的类型
     */
    public static final int VIEW_TYPE_KB = 1;
    private final BlogType mBlogType;


    private DisplayImageOptions mAvatarOptions;

    public BlogListItemAdapter(BlogType type) {
        mAvatarOptions = RaeImageLoader.headerOption();
        mBlogType = type;
        int size = 5;
        List<Blog> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Blog m = new Blog();
            m.setTag("loading");
            data.add(m);
        }

        invalidate(data);

    }

    @Override
    public int getItemViewType(int position) {
        Blog blog = getDataItem(position);
        if (TextUtils.equals("loading", blog.getTag())) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            return new ItemLoadingViewHolder(inflateView(parent, R.layout.item_list_loading));
        }

        return new BlogItemViewHolder(inflateView(parent, R.layout.item_blog_list));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, Blog m) {

        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            return;
        }

        BlogItemViewHolder holder = (BlogItemViewHolder) vh;

        switch (mBlogType) {
            case KB: // 知识库
                holder.authorLayout.setVisibility(View.GONE);
                holder.commentView.setVisibility(View.GONE);
                break;
        }

        ImageLoader.getInstance().displayImage(m.getAvatar(), holder.avatarView, mAvatarOptions);

        holder.authorView.setText(m.getAuthor());
        holder.titleView.setText(m.getTitle());
        holder.summaryView.setText(m.getSummary());
        holder.dateView.setText(m.getPostDate());
        holder.readerView.setText(m.getViews());
        holder.likeView.setText(m.getLikes());
        holder.commentView.setText(m.getComment());

        holder.itemView.setTag(m);
        holder.itemView.setOnClickListener(this);
//        showThumbImages(m, holder);

    }

    /**
     * 预览图处理
     *
     * @param m
     * @param holder
     */
    private void showThumbImages(Blog m, BlogItemViewHolder holder) {
        List<String> thumbs = m.getThumbs();
        if (Rae.isEmpty(thumbs)) {
            //  没有预览图
            holder.largeThumbView.setVisibility(View.GONE);
            holder.thumbLayout.setVisibility(View.GONE);
        } else if (thumbs.size() < 3 && thumbs.size() > 0) {
            // 一张预览图
            holder.largeThumbView.setVisibility(View.VISIBLE);
            holder.thumbLayout.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(thumbs.get(0), holder.largeThumbView);
        } else if (thumbs.size() >= 3) {
            holder.largeThumbView.setVisibility(View.GONE);
            holder.thumbLayout.setVisibility(View.VISIBLE);
            // 取三张预览图
            ImageLoader.getInstance().displayImage(thumbs.get(0), holder.thumbOneView);
            ImageLoader.getInstance().displayImage(thumbs.get(1), holder.thumbTwoView);
            ImageLoader.getInstance().displayImage(thumbs.get(2), holder.thumbThreeView);
        } else {
            holder.largeThumbView.setVisibility(View.GONE);
            holder.thumbLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        Blog blog = (Blog) view.getTag();
        AppRoute.jumpToBlogContent(view.getContext(), blog, mBlogType);
    }

}
