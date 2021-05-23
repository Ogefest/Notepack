package notepack.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class MaterialIcon extends Label {

    private String name;
    private double scale = 0.9;

    public MaterialIcon() {

    }

    public MaterialIcon(String name) {
        this.name = name;
        refreshIcon();
    }

    public MaterialIcon(String name, double scale) {
        this.name = name;
        this.scale = scale;
        refreshIcon();
    }

    public void setName(String name) {
        this.name = name;
        refreshIcon();
    }

    public String getName() {
        return name;
    }

    public void setScale(double scale) {
        this.scale = scale;
        refreshIcon();
    }

    public double getScale() {
        return scale;
    }

    private void refreshIcon() {
        String iconContent = MaterialIconLoader.getSVG(name);

        SVGPath svg = new SVGPath();
        svg.setContent(iconContent);
        svg.setStyle("-fx-fill: text-color");
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        final Region svgShape = new Region();
        svgShape.setShape(svg);

        setGraphic(svg);
    }


}
