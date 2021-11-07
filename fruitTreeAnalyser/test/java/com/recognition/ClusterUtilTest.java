package com.recognition;
import com.recognition.util.ClusterUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClusterUtilTest {
    ClusterUtil clusterUtil;
    ArrayList<Integer> arrayList;

    @BeforeEach
    void createNewHashMap() {
        clusterUtil = new ClusterUtil();
        arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        arrayList.add(6);
        clusterUtil.map.put(1, arrayList);
    }

    @Test
    void rootNotStored() {
        assertFalse(clusterUtil.rootNotStored(1), "rootNotStored() not reporting whether or not a root is stored in a hashMap as a key correctly!");
    }

    @Test
    void calcMedian() {
        assertEquals(3, clusterUtil.calcMedian(0, 6), "calcMedian() is not calculating the size medium of a hashMap correctly!");
    }

    @Test
    void calcIQR() {
        assertEquals(4, clusterUtil.calcIQR(clusterUtil.map.get(1), 6), "calcIQR() is not calculating the inter quartile range correctly!");
    }

    @Test
    void totalFruits() {
        assertEquals(1, clusterUtil.totalFruits(), "totalFruits() is not calculating the total number of fruits stored in a hashMap correctly!");
    }

}