package com.rae.cnblogs.sdk.parser;

import android.text.TextUtils;

import com.rae.cnblogs.sdk.Utils;
import com.rae.cnblogs.sdk.bean.Blog;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.core.sdk.ApiUiArrayListener;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 知识库列表解析器
 * Created by ChenRui on 2017/1/18 0018 18:27.
 */
public class KBListParser extends HtmlParser<Blog> {

    public KBListParser(ApiUiArrayListener<Blog> arrayListener) {
        super(arrayListener);
    }

    @Override
    protected void onParseHtmlDocument(Document document) {

        // 解析HTML
        List<Blog> result = new ArrayList<>();
        Elements elements = document.select(".kb_item");
        for (Element element : elements) {
            Blog m = new Blog();
            m.setBlogId(Utils.getNumber(element.attr("id")));
            m.setTitle(element.select(".kb_entry .kb-title").text());
            m.setTag(element.select(".kb_entry .deepred").text());
            m.setSummary(element.select(".kb_summary").text());
            m.setPostDate(Utils.getDate(element.select(".kb_footer .green").text()));
            m.setUrl("http:" + element.select(".kb_entry .kb-title").attr("href"));
            m.setViews(Utils.getNumber(element.select(".kb_footer .view").text()));
            m.setLikes(getLikeCount(element.select(".kb_footer").text()));
            m.setBlogType(BlogType.KB.getTypeName());
            result.add(m);
        }
        mArrayListener.onApiSuccess(result);
    }

    /**
     * 获取点赞数量
     *
     * @param text 浏览(8312) 推荐(94) 程序员 学习 发布于2017-01-09 14:10
     * @return
     */
    private String getLikeCount(String text) {
        if (TextUtils.isEmpty(text)) return "0";
        // 找到推荐
        Matcher matcher = Pattern.compile("推荐\\(\\d+\\)").matcher(text);
        if (matcher.find()) {
            return Utils.getNumber(matcher.group());
        }
        return "0";
    }
}