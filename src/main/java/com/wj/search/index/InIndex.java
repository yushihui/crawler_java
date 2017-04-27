package com.wj.search.index;

import com.wj.crawler.common.Tuple;

import java.util.Date;

/**
 * Created by Administrator on 4/21/2017.
 */
public interface InIndex <T extends Object>{

    Tuple<Integer, Date> bulkIndexing(Iterable<T> documents, String indexName);

    boolean removeIndex(String index);
}
