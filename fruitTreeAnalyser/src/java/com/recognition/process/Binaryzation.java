package com.recognition.process;
import com.recognition.util.TreeUtil;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

public class Binaryzation {
    public double hueDifference, saturationDifference, brightnessDifference;
    public PixelReader pr;
    public PixelWriter pw;
    public Image bwImage, coloredImage;
    public WritableImage wi;
    public TreeUtil treeUtil;

    public Binaryzation(double hueDifference, double saturationDifference, double brightnessDifference, PixelReader pr, PixelWriter pw, Image bwImage, Image coloredImage, WritableImage wi, TreeUtil treeUtil) {
        this.hueDifference = hueDifference;
        this.saturationDifference = saturationDifference;
        this.brightnessDifference = brightnessDifference;
        this.pr = pr;
        this.pw = pw;
        this.bwImage = bwImage;
        this.coloredImage = coloredImage;
        this.wi = wi;
        this.treeUtil = treeUtil;
    }

    public Binaryzation(Image image, Color fruitColor, int width, int height) {
        this.bwImage = image;
        this.pr = bwImage.getPixelReader();
        this.wi = new WritableImage(pr, width, height);
        this.pw = wi.getPixelWriter();
        this.hueDifference = 360;
        this.saturationDifference = 0.4;
        this.brightnessDifference = 0.3;
        this.treeUtil = new TreeUtil(width, height);
        this.bwImage = createBinaryImage(fruitColor);
        this.coloredImage = bwImage;
    }


    /**
     * 根据鼠标选中的水果颜色，对水果图片进行二值化
     * @param fruitColor
     * @return
     */
    private Image createBinaryImage(Color fruitColor) {
        boolean moreRed = decideRGBofFruit(fruitColor.getRed(), fruitColor.getGreen(), fruitColor.getBlue());
        for (int y = 0; y < bwImage.getHeight(); y++) {
            for (int x = 0; x < bwImage.getWidth(); x++) {
                Color pixelColor = pr.getColor(x, y);
                if (moreRed)
                    moreRedFruitRecog(fruitColor, pixelColor, x, y);
                else
                    moreBlueFruitRecog(fruitColor, pixelColor, x, y);
            }
        }

        treeUtil.unionFruitPixels();
        coloredImage = bwImage;
        return wi;
    }

    /**
     * 偏向红色的水果识别
     * @param fruitColor
     * @param pixelColor
     * @param x
     * @param y
     */
    private void moreRedFruitRecog(Color fruitColor, Color pixelColor, int x, int y) {
        double r = pixelColor.getRed();
        if (pixelIsPartOfFruit(fruitColor, pixelColor) && r > pixelColor.getGreen() && r > pixelColor.getGreen()) {
            pw.setColor(x, y, Color.WHITE);
            treeUtil.addFruitToArray(x, y);
        } else {
            pw.setColor(x, y, Color.BLACK);
            treeUtil.addNonFruitToArray(x, y);
        }
    }

    /**
     * 当前像素点是否是水果
     * 鼠标选中的像素点与当前像素点在色调、饱和度与亮度上的差值是否在差值以内
     * @param fruitColor 鼠标选中的像素点
     * @param pixelColor 当前像素点
     * @return
     */
    private boolean pixelIsPartOfFruit(Color fruitColor, Color pixelColor) {
        return compareHue(fruitColor.getHue(), pixelColor.getHue(), hueDifference)
                && compareSaturation(fruitColor.getSaturation(), pixelColor.getSaturation(), saturationDifference)
                && compareBrightness(fruitColor.getBrightness(), pixelColor.getBrightness(), brightnessDifference);
    }

    /**
     * 偏蓝（非偏红）的水果识别
     * @param fruitColor
     * @param pixelColor
     * @param x
     * @param y
     */
    private void moreBlueFruitRecog(Color fruitColor, Color pixelColor, int x, int y) {
        double b = pixelColor.getBlue();
        if (pixelIsPartOfFruit(fruitColor, pixelColor) && b > pixelColor.getRed() && b > pixelColor.getGreen()) {
            pw.setColor(x, y, Color.WHITE);
            treeUtil.addFruitToArray(x, y);
        } else {
            pw.setColor(x, y, Color.BLACK);
            treeUtil.addNonFruitToArray(x, y);
        }
    }

    /**
     * 偏向红色的像素点
     * @param r
     * @param g
     * @param b
     * @return
     */
    private boolean decideRGBofFruit(double r, double g, double b) {
        return r > g && r > b;
    }
    /**
     * 色调对比是否在差值以内
     * @param firstHue
     * @param secondHue
     * @param difference 差值
     * @return
     */
    private boolean compareHue(double firstHue, double secondHue, double difference) {
        return Math.abs(firstHue - secondHue) < difference?true:false;
    }



    /**
     * 饱和度对比是否在差值以内
     * @param firstSaturation
     * @param secondSaturation
     * @param difference 差值
     * @return
     */
    private boolean compareSaturation(double firstSaturation, double secondSaturation, double difference) {
        return Math.abs(firstSaturation - secondSaturation) < difference?true:false;
    }

    /**
     * 亮度对比是否在差值以内
     * @param firstBrightness
     * @param secondBrightness
     * @param difference 差值
     * @return
     */
    private boolean compareBrightness(double firstBrightness, double secondBrightness, double difference) {
        return Math.abs(firstBrightness - secondBrightness) < difference?true:false;
    }

}