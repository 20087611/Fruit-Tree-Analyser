package com.recognition.util;

public class TreeUtil {
    public int[] pixelArray;
    public int width, height;

    public TreeUtil(int imgWidth, int height) {
        this.width = imgWidth;
        this.height = height;
        this.pixelArray = new int[imgWidth * height];
    }


    public void addNonFruitToArray(int x, int y) {
        pixelArray[calculateArrayPosition(y, x)] = -1;
    }

    public void addFruitToArray(int x, int y) {
        int pos = calculateArrayPosition(y, x);
        pixelArray[pos] = pos;
    }

    public int calculateArrayPosition(int y, int x) {
        return (y * width) + x;
    }

    public void unionFruitPixels() {
        for (int i = 0; i < pixelArray.length; i++) {
            if (pixelIsWhite(i)) {
                int top = i - width, right = i + 1, bottom = i + width, left = i - 1;
                if (!atTopEdge(i) && pixelIsWhite(top))
                    unionPixels(i, top);
                if (!atRightEdge(i) && pixelIsWhite(right))
                    unionPixels(i, right);
                if (!atBottomEdge(i) && pixelIsWhite(bottom))
                    unionPixels(i, bottom);
                if (!atLeftEdge(i) && pixelIsWhite(left))
                    unionPixels(i, left);
            }
        }
    }

    public void unionPixels(int a, int b) {
        if (find(pixelArray, a) < find(pixelArray, b)) {
            quickUnion(pixelArray, a, b);
        } else {
            quickUnion(pixelArray, b, a);
        }
    }

    public void quickUnion(int[] a, int p, int q) {
        pixelArray[find(a, q)] = find(a, p);
    }

    public int find(int[] a, int id) {
        return a[id] == id ? id : (a[id] = find(a, a[id]));
    }

    public boolean pixelIsWhite(int i) {
        return pixelArray[i] != -1;
    }

    public boolean atBottomEdge(int i) {
        return i + width > width * height;
    }

    public boolean atLeftEdge(int i) {
        return i % width == 0;
    }

    public boolean atTopEdge(int i) {
        return i - width < 0;
    }

    public boolean atRightEdge(int i) {
        return (2 * (i + 1)) % width == 0;
    }

}