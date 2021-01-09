package org.lzy.map;

import org.lzy.map.utils.ByteUtils;
import org.lzy.map.sort.BytesComparator;
import org.lzy.map.sort.KVByteSortDataFormat;
import org.lzy.map.sort.TimSort;

import java.util.*;

/**
 * @Description: 该数据结构使用一个数组存放固定长度的key和value，实现内存紧凑kv数据存储
 * 提供了根据key进行排序的方法，并根据排序使用二分查找返回value的position
 * @Author: liuziyang
 * @CreateDate: 2021-01-09
 */
public class KVSortByteArray {
    /**
     * 用一个byte数组依次存放key和value
     * position {ENTRY_WIDTH * i} 在数组中存放key
     * position {ENTRY_WIDTH * i + K_WIDTH} 在数组中存放value
     */
    private byte[] data;


    private int capacity; //数组长度
    private int count = 0; //数据长度
    private final int K_WIDTH; //key的字节长度
    private final int V_WIDTH; //value的字节长度
    private final int ENTRY_WIDTH; //key+value字节长度

    private final int MAXIMUM_CAPACITY; //数组最大长度
    private static final float DEFAULT_GROW_FACTOR = 1.2f; //数组扩容系数
    private static final BytesComparator COMPARATOR = new BytesComparator(); //byte排序


    public KVSortByteArray(int capacity, int keyLen, int valueLen) {
        this.K_WIDTH = keyLen;
        this.V_WIDTH = valueLen;
        this.ENTRY_WIDTH = keyLen + valueLen;
        this.capacity = capacity;
        data = new byte[this.capacity * this.ENTRY_WIDTH];
        MAXIMUM_CAPACITY = Integer.MAX_VALUE / this.ENTRY_WIDTH;
    }




    /**
     * 插入数据
     * @param keyBytes
     * @param valueBytes
     */
     public void add(byte[] keyBytes, byte[] valueBytes) {
        assert keyBytes.length==K_WIDTH : "key length must ="+K_WIDTH+", but new key length ="+keyBytes.length;
        assert valueBytes.length==K_WIDTH : "value length must ="+K_WIDTH+", but new value length ="+valueBytes.length;

        if (count == capacity) growArray(); //长度不足以存放当前数据，则进行数组扩充
        System.arraycopy(keyBytes, 0, data, getKeyStartPosition(count), K_WIDTH);
        System.arraycopy(valueBytes, 0, data, getValueStartPosition(count), V_WIDTH);

        count++;
    }


    /**
     * 扩充数组
     */
    private void growArray() {
        //新长度不能超过2GB
        int newCapacity = Math.min((int) (capacity * DEFAULT_GROW_FACTOR), MAXIMUM_CAPACITY);
        byte[] newData = new byte[newCapacity * ENTRY_WIDTH];
        System.arraycopy(data, 0, newData, 0, capacity * ENTRY_WIDTH); //旧数据复制
        //新变量赋值
        data = newData;
        capacity = newCapacity;
    }

    public int size() {
        return this.count;
    }

    /**
     * 根据字符串查询long值，非标准查询方法
     * 直接读取data[]生成long值，不需要从源数组中复制子byte[]
     * @param key
     * @return
     */
    public long getLong(final String key) {
        int valuePos = get(key.getBytes());
        return valuePos == -1 ? -1L : ByteUtils.BytesToLong(data, valuePos);
    }

    /**
     * 根据字符串查询bye[]数据,非标准查询方法
     * 需要复制一个byte数组再返回
     * @param key
     * @return byte[] 注意：该byte[]不能直接转字符串，是value的编码，应先转为value类型
     */
    public byte[] get(final String key){
        int valuePos=get(key.getBytes());
        return valuePos== -1 ? null :Arrays.copyOfRange(data, valuePos, valuePos + V_WIDTH);
    }

    /**
     * 二分法查询指定key对应的value
     *
     * @param key
     * @return key不存在则返回-1，否则返回其value的起始索引
     */
    public int get(final byte[] key) {
        int i = 0;
        int j = count - 1;
        int mid;
        while (i <= j) {
            mid = (i + j) >> 1;
            int midPos = mid * ENTRY_WIDTH;
            final int ret = COMPARATOR.compare(data, midPos, midPos + K_WIDTH, key);
            if (ret == 0) {
                return midPos + K_WIDTH;
            } else if (ret < 0) {
                i = mid + 1;
            } else {
                j = mid - 1;
            }

        }
        return -1;
    }

    /**
     * 数组排序
     * todo 每次排序都需要创建TimSort对象，待优化
     */
    public void sort() {
        /**创建排序使用的buufer*/
        int len = count;
        if (len > 10) len = count / 2; //使用1/2长度即可进行排序
        byte[] buffer = new byte[len * ENTRY_WIDTH];

        /**创建一个根据byte[]段排序的DataFormat*/
        KVByteSortDataFormat sdf = new KVByteSortDataFormat(buffer, K_WIDTH, V_WIDTH);
        TimSort<byte[], byte[]> timSort = new TimSort<>(sdf);
        timSort.sort(data, 0, count, COMPARATOR);
    }

    /**
     * 获取指定索引对应的真实key在数组中的起始索引
     * @param index
     * @return
     */
    private int getKeyStartPosition(int index){
        return index * ENTRY_WIDTH;
    }
    /**
     * 获取指定索引对应的真实value在数组中的起始索引
     * @param index
     * @return
     */
    private int getValueStartPosition(int index){
        return index * ENTRY_WIDTH + K_WIDTH;
    }

    /**
     * 获取所有Key集合，用于测试
     * @return
     */
    public Set<String> getUuids() {
        KVByteSortDataFormat sdf = new KVByteSortDataFormat(new byte[0], K_WIDTH, V_WIDTH);
        Set<String> list = new HashSet<>();
        for (int i = 0; i < count; i++) {
            list.add(new String(sdf.getKey(data, i)));
        }
        return list;
    }
}