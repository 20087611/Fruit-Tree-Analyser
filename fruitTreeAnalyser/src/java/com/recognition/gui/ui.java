package com.recognition.gui;
import com.recognition.process.Binaryzation;
import com.recognition.process.Cluster;
import com.recognition.util.ClusterUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.text.*;

import java.util.*;
import java.io.*;

public class ui {

    Color fruitColor;
    Cluster cluster;
    Binaryzation binaryzation;
    ClusterUtil clusterUtil;
    Boolean sizesAreShown = false;

    @FXML
    ImageView chosenImageView;
    @FXML
    ImageView blackWhiteImageView;

    @FXML
    StackPane stack, sizePane;
    @FXML
    RadioButton radio, outliers, colorRadio;

    @FXML
    Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML
    Label yourImage, bwVersion, start;

    @FXML
    HBox menu, recogSettings, colorSettings;
    @FXML
    TextField minClusterSize, totalFruits;

    @FXML
    Button bwBut;
    @FXML
    AnchorPane noMenu;


    /**
     * 选择图片文件
     * @throws FileNotFoundException
     */
    public void chooseFile() throws FileNotFoundException {
            resetImageGUI();
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File("image"));
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null); // 选择图片文件

            // 初始化水果树的图片
            cluster = new Cluster(new FileInputStream(file), (int) chosenImageView.getFitWidth(), (int) chosenImageView.getFitHeight());
            chosenImageView.setImage(cluster.originalImage);
            yourImage.setVisible(true);
            createTooltip(bwBut, "尝试选择不同区域!");
            start.setVisible(true);

    }

    /**
     * 提示语
     * @param placement
     * @param text
     */
    public void createTooltip(Node placement, String text) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText(text);
        Tooltip.install(placement, tooltip);
    }

    /**
     * 清空图片显示显示
     */
    public void resetImageGUI() {
        minimizeMenu();
        bwVersion.setVisible(false);
        yourImage.setVisible(false);
        chosenImageView.setImage(null);
        blackWhiteImageView.setImage(null);
        start.setVisible(false);
        outliers.disarm();
        minClusterSize.clear();
        hueSlider.setValue(0);
        saturationSlider.setValue(1);
        brightnessSlider.setValue(1);
    }

    /**
     * 获取鼠标点击时的像素值
     *
     * @param mouseEvent
     */
    public void getColourAtMouse(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            if (chosenImageView != null) {
                start.setVisible(false);
                fruitColor = chosenImageView.getImage().getPixelReader().getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
                if (blackWhiteImageView.getImage() != null) {
                    displayBlackWhiteImage();
                }
            }
        } catch (Exception ignore) {
        }
    }

    public void getClusterAtMouse(javafx.scene.input.MouseEvent event) {
        try {
            int x = (int) event.getX(), y = (int) event.getY();
            if (binaryzation != null)
                if (binaryzation.treeUtil.pixelIsWhite(binaryzation.treeUtil.calculateArrayPosition(y, x))) {
                    int root = binaryzation.treeUtil.find(binaryzation.treeUtil.pixelArray, binaryzation.treeUtil.calculateArrayPosition(y, x));
                    Tooltip tooltip = new Tooltip();
                    int rank = clusterUtil.getClusterSizeRank(root);
                    tooltip.setText("Fruit/Cluster number: " + rank + "\n" + "Estimated size (pixel units): " + clusterUtil.map.get(root).size());
                    if (sizesAreShown)
                        Tooltip.install(sizePane, tooltip);
                    else Tooltip.install(chosenImageView, tooltip);
                }
        } catch (Exception ignore) {
        }
    }

    /**
     * 对水果图片进行二值化处理，并展示
     */
    public void displayBlackWhiteImage() {
        try {
            binaryzation = initialiseBlackWhiteImage();
            blackWhiteImageView.setImage(binaryzation.bwImage);
            bwVersion.setVisible(true);
            colorRadio.setSelected(false);
            sizePane.getChildren().removeAll();
            cluster.setBorderedImage(cluster.editableImage);
            chosenImageView.setImage(cluster.borderedImage);
        } catch (Exception e) {
            Alert error;
            if (cluster == null) error = alter("提示", "请先选择水果图片!");
            else if (fruitColor == null) error = alter("提示", "请先点击水果图片!");
            else error = alter("提示", e.toString());
            error.show();
        }
    }

    /**
     * 初始化二值化图片
     */
    public Binaryzation initialiseBlackWhiteImage() {
        return new Binaryzation(cluster.editableImage, fruitColor, cluster.width, cluster.height);
    }

    /**
     * 弹窗
     * @param title
     * @param header
     * @return
     */
    public Alert alter(String title, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(header);
        ImageView imageView = new ImageView(new Image("image/warning.png"));
        a.setGraphic(imageView);
        return a;
    }

    /**
     * 是否在矩形框上显示标号数字
     */
    public void showOnScreenSizes() {
        if (radio.isSelected()) {
            sizePane.toFront();
            sizesAreShown = true;
        } else {
            sizePane.toBack();
            sizesAreShown = false;
        }
    }

    /**
     * 图片上各矩形框的标号数字
     */
    public void createSizePane() {
        sizePane = new StackPane();
        stack.getChildren().add(sizePane);
        sizePane.toBack();
        for (int i : clusterUtil.map.keySet()) {
            Font font = Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 10);
            Label label = new Label(String.valueOf(clusterUtil.getClusterSizeRank(i)));
            label.setTextFill(Color.WHITE);
            label.setFont(font);
            sizePane.getChildren().add(label);
            label.setTranslateX(cluster.calcXFromIndex(i) - (cluster.width >> 1) + 3);
            label.setTranslateY(cluster.calcYFromIndex(i) - (cluster.width >> 1) + 3);
        }
    }

    /**
     * 执行水果识别
     */
    public void setPixelBorders() {
        try {
            cluster.resetEditableImage();
            clusterUtil = new ClusterUtil();
            clusterUtil.startCluster(binaryzation.treeUtil);

            if (outliers.isSelected() && !minClusterSize.getText().equals("")) {
                clusterUtil.removeOutliers(Integer.parseInt(minClusterSize.getText()), true);
            } else if (outliers.isSelected()) {
                clusterUtil.removeOutliers(2, true);
            } else if (!minClusterSize.getText().equals("")) {
                clusterUtil.removeOutliers(Integer.parseInt(minClusterSize.getText()), false);
            }

            for (int i : clusterUtil.map.keySet()) {
                cluster.drawClusterBorder(i, clusterUtil.map);
            }
            chosenImageView.setImage(cluster.borderedImage);
            createSizePane();
            colorFruits();
            totalFruits.setText("识别到 "+clusterUtil.totalFruits() + " 个水果");
        } catch (Exception e) {
            Alert a = alter("提示", "识别错误!");
            if (chosenImageView.getImage() == null)
                a = alter("提示", "请先选择图片!");
            else if (binaryzation == null)
                a = alter("提示", "请先进行二值化!");
            a.show();
        }
    }

    public void displayMenu() {
        menu.setVisible(true);
        noMenu.setVisible(false);
    }

    public void minimizeMenu() {
        menu.setVisible(false);
        noMenu.setVisible(true);
        recogSettings.setVisible(false);
        colorSettings.setVisible(false);
    }

    public void displayRecogMenu() {
        recogSettings.setVisible(true);
        colorSettings.setVisible(false);
    }

    public void displayColorMenu() {
        recogSettings.setVisible(false);
        colorSettings.setVisible(true);
    }

    public void sliderAdjustments() {
        try {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hueSlider.getValue());
            colorAdjust.setSaturation(saturationSlider.getValue());
            colorAdjust.setBrightness(brightnessSlider.getValue());
            cluster.editImagePixels(colorAdjust);
            cluster.setEditableImage(cluster.wi);
            chosenImageView.setImage(cluster.editableImage);
            displayBlackWhiteImage();
        } catch (Exception e) {
            alter("提示", "请先转换图片!");
        }
    }

    /**
     * 根据二值化的图片最后识别出来的水果
     */
    public void colorFruits() {
        try {
            clusterUtil.colorAllClusters(binaryzation, cluster);
        } catch (Exception e) {
            alter("提示", "请先进行二值化!");
        }
    }

    /**
     * 在二值化图片上展示识别出来的水果
     */
    public void showColoredFruits() {
        if (chosenImageView.getImage() != null && blackWhiteImageView.getImage() != null) {
            if (colorRadio.isSelected()) {
                if (binaryzation.coloredImage == binaryzation.bwImage) {
                    alter("提示", "请先识别图片!");
                } else blackWhiteImageView.setImage(binaryzation.coloredImage);
            } else {
                blackWhiteImageView.setImage(binaryzation.bwImage);
            }
        } else {
            alter("提示", "请先转换图片!");
        }
    }

    /**
     * 选择随机分类时，把识别出来的水果用不同颜色分类
     * @param event
     */
    public void colourIndividualCluster(javafx.scene.input.MouseEvent event) {
        try {
            binaryzation.coloredImage = binaryzation.bwImage;
            int x = (int) event.getX(), y = (int) event.getY();
            Random rand = new Random();
            float r = rand.nextFloat(), g = rand.nextFloat(), b = rand.nextFloat();
            Color randomColor = new Color(r, g, b, 1);
            clusterUtil.randomlyColorCluster(binaryzation.treeUtil.find(binaryzation.treeUtil.pixelArray,
                    binaryzation.treeUtil.calculateArrayPosition(y, x)), cluster, binaryzation.pw, randomColor);
            blackWhiteImageView.setImage(binaryzation.coloredImage);
        } catch (Exception ignore) {
        }
    }

}