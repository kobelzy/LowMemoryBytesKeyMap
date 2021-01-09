package org.lzy.map.sort;

import java.util.Comparator;

public class BytesComparator implements Comparator<byte[]> {


//    @Override
//    public int compare(byte[] o1, byte[] o2) {
//        int len1 = o1.length;
//        int len2 = o2.length;
//        int lim = Math.min(len1, len2);
//
//        int k = 0;
//        while (k < lim) {
//            byte c1 = o1[k];
//            byte c2 = o2[k];
//            if (c1 != c2) {
//                return c1 - c2;
//            }
//            k++;
//        }
//        return len1 - len2;
//    }

    @Override
    public int compare(byte[] o1, byte[] o2) {
        return compare(o1, 0, o1.length, o2);
    }

    /**
     * 从byte[]的指定区间与目标进行比较
     *
     * @param data
     * @param startPos
     * @param endPos
     * @param other
     * @return
     */
    public int compare(byte[] data, int startPos, int endPos, byte[] other) {
        int len1 = endPos - startPos;
        int len2 = other.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            byte c1 = data[startPos + k];
            byte c2 = other[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
}
