package org.lzy.map;


import org.junit.Test;
import org.lzy.map.utils.ByteUtils;

class NestSortMapTest {
    public static void main(String[] args) {
        NestSortMapTest t=new NestSortMapTest();
        t.test1();
    }
    @Test
    public void test1(){
        String a="abcd";
        String b="abce";
        String c="abcf";
        KVSortByteArray byteArray = new KVSortByteArray(12, a.length(), 8);
        byteArray.add(a.getBytes(), ByteUtils.LongToBytes(100));
        byteArray.add(b.getBytes(), ByteUtils.LongToBytes(200));
        byteArray.add(c.getBytes(), ByteUtils.LongToBytes(300));
        byteArray.sort();
        System.out.println(byteArray.getLong(a));
        System.out.println(byteArray.getLong(b));
        System.out.println(byteArray.getLong(c));
    }

}