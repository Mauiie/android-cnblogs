package com.rae.cnblogs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.LoadingMoreFooter;
import com.rae.cnblogs.R;

/**
 * 加载更多
 * Created by ChenRui on 2016/12/3 17:56.
 */
public class RaeLoadMoreView extends LoadingMoreFooter {

    private TextView mTextView;
    private String mNoMoreText;
    private View mProgressBar;

    public RaeLoadMoreView(Context context) {
        this(context, null);
    }

    public RaeLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 32, 0, 32);
    }

    @Override
    public void initView() {
        super.initView();
        mProgressBar = getChildAt(0);
        mTextView = (TextView) getChildAt(1);
        mNoMoreText = getResources().getString(R.string.no_more_tips);
    }

    public void setNoMoreText(String text) {
        mNoMoreText = text;
    }

    @Override
    public void setState(int state) {
        if (state == STATE_NOMORE) {
            mTextView.setText(mNoMoreText);
            mProgressBar.setVisibility(View.GONE);
            this.setVisibility(View.VISIBLE);
            return;
        }
        super.setState(state);
    }
}
