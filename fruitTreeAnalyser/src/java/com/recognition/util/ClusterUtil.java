package com.recognition.util;
import com.recognition.process.Binaryzation;
import com.recognition.process.Cluster;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.util.*;
import java.util.List;

public class ClusterUtil {

    public HashMap<Integer, ArrayList<Integer>> map;

    public ClusterUtil() {
        this.map = new HashMap<>();
    }

    /**
     * 开始聚类和识别
     * @param treeUtil
     */
    public void startCluster(TreeUtil treeUtil) {
        startCluster(treeUtil, 8);
    }

    /**
     * 开始聚类和识别
     * @param treeUtil
     * @param minimumPixels 每个聚类的最少像素点
     */
    private void startCluster(TreeUtil treeUtil, int minimumPixels) {
        // 对像素点一个一个遍历
        for (int x = 0; x < treeUtil.pixelArray.length; x++) {
            if (!treeUtil.pixelIsWhite(x)) {
                // 非水果像素点，略过
                continue;
            }

            int root = treeUtil.find(treeUtil.pixelArray, x);
            if (rootNotStored(root)) {
                ArrayList<Integer> tmpList = new ArrayList<>();
                for (int i = x; i < treeUtil.pixelArray.length; i++) {
                    if (treeUtil.pixelIsWhite(i) && currentRootEqualsTempRoot(i, root, treeUtil)) {
                        tmpList.add(i);
                    }
                }
                map.put(root, tmpList);
            }

        }

        // 聚类的像素点少于minimumPixels个，就判定不是水果点，从识别中剔除
        List<Integer> removeIndexList = new ArrayList<>();
        for (Integer root : map.keySet()) {
            if (map.get(root).size() < minimumPixels) {
                removeIndexList.add(root);
            }
        }
        map.keySet().removeAll(removeIndexList);

    }


    /**
     * 是否是聚类起始点
     * @param root
     * @return
     */
    public boolean rootNotStored(int root) {
        return !map.containsKey(root);
    }

    /**
     * 是否同一聚类
     * @param i
     * @param root
     * @param a
     * @return
     */
    private boolean currentRootEqualsTempRoot(int i, int root, TreeUtil a) {
        return a.find(a.pixelArray, i) == root;
    }


    /**
     * 删除异常点（四分位的异常点）
     * @param min
     * @param doIQR
     */
    public void removeOutliers(int min, boolean doIQR) {
        int IQR = calcIQR(createSortedSizeArray(), createSortedSizeArray().size());
        Set<Map.Entry<Integer, ArrayList<Integer>>> setOfEntries = map.entrySet();
        Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
            int size = entry.getValue().size();
            if (size < IQR && doIQR) {
                iterator.remove();
            } else if (size < min) {
                iterator.remove();
            }
        }
    }

    public int getClusterSizeRank(int root) {
        return rankSetsBySize().get(root);
    }

    /**
     * 所有聚类的大小从小到大排序
     * @return
     */
    private ArrayList<Integer> createSortedSizeArray() {
        ArrayList<Integer> arrayOfSizes = new ArrayList<>();
        for (int i : map.keySet()) {
            arrayOfSizes.add(map.get(i).size());
        }
        Collections.sort(arrayOfSizes);
        return arrayOfSizes;
    }

    /**
     * 计算四分位
     * @param a
     * @param length
     * @return
     */
    public int calcIQR(ArrayList<Integer> a, int length) {
        int middleIndex = calcMedian(0, length);
        int Q1 = a.get(calcMedian(0, middleIndex));
        int Q3 = a.get(calcMedian(middleIndex + 1, length));
        return (Q3 - Q1);
    }

    /**
     * 计算区间均值
     * @param start
     * @param end
     * @return
     */
    public int calcMedian(int start, int end) {
        int n = end - start + 1;
        n = (n + 1) / 2 - 1;
        return n + start;
    }

    public HashMap<Integer, Integer> sortByValue() {
        HashMap<Integer, Integer> hm = createSizeHashMap();
        List<HashMap.Entry<Integer, Integer>> list = new LinkedList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        HashMap<Integer, Integer> sortedHashMap = new LinkedHashMap<>();

        for (HashMap.Entry<Integer, Integer> a : list) {
            sortedHashMap.put(a.getKey(), a.getValue());
        }
        return sortedHashMap;
    }


    public HashMap<Integer, Integer> createSizeHashMap() {
        HashMap<Integer, Integer> newHM = new HashMap<>();
        for (int i : map.keySet()) {
            newHM.put(i, map.get(i).size());
        }
        return newHM;
    }


    public HashMap<Integer, Integer> rankSetsBySize() {
        HashMap<Integer, Integer> sortedMap = sortByValue();
        int newSize = sortedMap.keySet().size();
        for (int i : sortedMap.keySet()) {
            sortedMap.replace(i, newSize);
            newSize--;
        }
        return sortedMap;
    }

    public int totalFruits() {
        return map.keySet().size();
    }

    public void randomlyColorCluster(int root, Cluster cluster, PixelWriter pw, Color color) {
        ArrayList<Integer> tempArray = map.get(root);
        for (int i : tempArray) {
            pw.setColor(cluster.calcXFromIndex(i), cluster.calcYFromIndex(i), color);
        }
    }

    /**
     * 根据二值化的图片最后识别出来的水果
     * @param binaryzation
     * @param cluster
     */
    public void colorAllClusters(Binaryzation binaryzation, Cluster cluster) {
        PixelReader pixelReader = binaryzation.coloredImage.getPixelReader();
        WritableImage writableImage = new WritableImage(pixelReader, (int) binaryzation.coloredImage.getWidth(), (int) binaryzation.coloredImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i : map.keySet()) {
            Random rand = new Random();
            float r = rand.nextFloat(), g = rand.nextFloat(), b = rand.nextFloat();
            Color randomColor = new Color(r, g, b, 1);
            randomlyColorCluster(i, cluster, pixelWriter, randomColor);
        }
        binaryzation.coloredImage = writableImage;
    }
}