package com.recognition.process;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cluster {
    public Image originalImage, editableImage, borderedImage;
    public int width, height;
    public PixelReader pr;
    public PixelWriter pw;
    public WritableImage wi;

    public Cluster(FileInputStream userFile, int width, int height) {
        this.originalImage = new Image(userFile, width, height, false, true);
        this.editableImage = originalImage;
        this.width = (int) editableImage.getWidth();
        this.height = (int) editableImage.getHeight();
        this.pr = editableImage.getPixelReader();
        this.wi = new WritableImage(pr, width, height);
        this.pw = wi.getPixelWriter();
        this.borderedImage = editableImage;
    }

    public int calcFurtherLeftPixel(int posA, int posB) {
        return posA % width < posB % width ? posA : posB;
    }

    public int calcFurtherRightPixel(int posA, int posB) {
        return posA % width > posB % width ? posA : posB;
    }

    public int calcXFromIndex(int i) {
        return (i) % width;
    }

    public int calcYFromIndex(int i) {
        return (i) / width;
    }


    /**
     * 对识别出是水果的聚类画一个红色的矩形框
     *
     * @param startPixel
     * @param map
     */
    public void drawClusterBorder(int startPixel, HashMap<Integer, ArrayList<Integer>> map) {
        List<Integer> tmpList = map.get(startPixel); // 当前聚类的所有像素点位置
        // 判断矩形框的左右最远的点
        int furthestLeftPixel = startPixel, furthestRightPixel = startPixel;
        for (int i : tmpList) {
            furthestLeftPixel = i >= tmpList.size() ? calcFurtherLeftPixel(furthestLeftPixel, i) : furthestLeftPixel;
            furthestRightPixel = i >= tmpList.size() ? calcFurtherRightPixel(furthestRightPixel, i) : furthestRightPixel;
        }

        // 矩形框的终点像素
        int bottomPixel = tmpList.get(tmpList.size() - 1);
        // 矩形框的四个点
        int leftX = calcXFromIndex(furthestLeftPixel),
                rightX = calcXFromIndex(furthestRightPixel),
                topY = calcYFromIndex(startPixel),
                botY = calcYFromIndex(bottomPixel);
        // 画框
        drawBorder(leftX, rightX, topY, botY);
        borderedImage = wi;
    }

    public void drawBorder(int leftX, int rightX, int topY, int botY) {
        for (int x = leftX; x <= rightX; x++)
            pw.setColor(x, topY, Color.BLUE);
        for (int y = topY; y <= botY; y++)
            pw.setColor(rightX, y, Color.BLUE);
        for (int x = rightX; x >= leftX; x--)
            pw.setColor(x, botY, Color.BLUE);
        for (int y = botY; y >= topY; y--)
            pw.setColor(leftX, y, Color.BLUE);
    }

    public void setEditableImage(Image newImage) {
        this.editableImage = newImage;
    }
    public void setBorderedImage(Image newImg) {
        this.borderedImage = newImg;
    }

    public void resetEditableImage() {
        editableImage = originalImage;
        pr = editableImage.getPixelReader();
        wi = new WritableImage(pr, width, height);
        pw = wi.getPixelWriter();
    }


    public void editImagePixels(ColorAdjust colorAdjust) {
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                Color c = pr.getColor(x, y);
                c = c.deriveColor(colorAdjust.getHue(), colorAdjust.getSaturation(), colorAdjust.getBrightness(), 1);
                pw.setColor(x, y, c);
            }
        }
    }


}