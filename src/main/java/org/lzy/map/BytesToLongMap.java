package org.lzy.map;

import org.lzy.map.utils.ByteUtils;

import java.util.HashMap;
import java.util.Map;

public class BytesToLongMap extends HashMap<Integer, KVSortByteArray> {
    private static final String CHARS = "1234567890abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ-";

    private int peakKey;
    private int peakSize=0;
    private int minKey;
    private int minSize=Integer.MAX_VALUE;

    public void put(String key, String value) {
        put(key, Long.parseLong(value));
    }

    public void put(String key, long value) {
        int keyIndex = getKeyIndex(key);
        if (!this.containsKey(keyIndex)) this.put(keyIndex, new KVSortByteArray(16, key.length(), 8));
        KVSortByteArray sortByteArray = this.get(keyIndex);
        sortByteArray.add(key.getBytes(), ByteUtils.LongToBytes(value));
    }

    /**
     * 根据String查询long
     * @param key
     * @return 返回-1时候是没有结果的情况
     */
    public long get(String key) {
        int keyIndex = getKeyIndex(key);
        if (this.containsKey(keyIndex)) {
            return this.get(keyIndex).getLong(key);
        }
        return -1L;
    }

    /**
     * 根据String查询String，包含了将long转为字符串的过程
     * @param key
     * @return
     */
    public String getStr(String key) {
        int keyIndex = getKeyIndex(key);
        if (this.containsKey(keyIndex)) {
            long searchValue = this.get(keyIndex).getLong(key);
            return searchValue == -1 ? null : String.valueOf(searchValue);
        }
        return null;
    }

    /**
     * 对每个数组进行排序，并统计长度信息
     */
    public void sort(){
        for (Map.Entry<Integer, KVSortByteArray> entry : this.entrySet()) {
            KVSortByteArray value = entry.getValue();
            value.sort(); //排序
            //统计指标
            if(value.size()>peakSize){
                peakKey=entry.getKey();
                peakSize=value.size();
            }
            if(value.size()<minSize){
                minKey=entry.getKey();
                minSize=value.size();
            }
        }
        System.out.println("peak size :"+peakKey+"->"+peakSize);
        System.out.println("min size :"+minKey+"->"+minSize);
    }
    private int getKeyIndex(String key) {
        return key.length() * 100 + CHARS.indexOf(key.charAt(0));
    }
}
