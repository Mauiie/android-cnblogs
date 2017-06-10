package com.rae.cnblogs.sdk.parser;

import java.io.IOException;

/**
 * 解析器
 * Created by ChenRui on 2017/5/22 0022 0:07.
 */
public interface IHtmlParser<T> {
    T parse(String html) throws IOException;
}
