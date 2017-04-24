package com.wj.search.index;

/**
 * Created by Administrator on 4/21/2017.
 */
public interface InIndex <T extends Object>{

    boolean bulkIndexing(Iterable<T> documents, String indexName);

    boolean removeIndex(String index);
}
