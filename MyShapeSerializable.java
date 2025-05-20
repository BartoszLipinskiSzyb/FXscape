import java.io.Serializable;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class MyShapeSerializable implements Serializable {
    public double[] shapeRGBA = new double[4];
    public Size shapeOrigin;
    public Size shapeSize;
    public double shapeRotation;
    public double shapeScale;
    public Size shapeTranslation;
    public Size[] shapePolygonVertices;

    public Boolean selected;
    public MyShape.Type type;

    public MyShapeSerializable(MyShape shape) {
        if (shape.type == MyShape.Type.POLYGON) {
            shapePolygonVertices = shape.getPolygonVertices();
        }
        shapeSize = new Size(shape.originalSize.getWidth(), shape.originalSize.getHeight());
        shapeOrigin = new Size(shape.originalOrigin.getWidth(), shape.originalOrigin.getHeight());
        System.out.println("Shape origin: " + shapeOrigin.getWidth() + " " + shapeOrigin.getHeight());

        shapeRotation = shape.shape.getRotate();
        shapeScale = shape.shape.getScaleX(); // zapisujemy tylko jedną oś, jako że program nie obsługuje skalowania w osiach osobno
        shapeTranslation = new Size(shape.shape.getTranslateX(), shape.shape.getTranslateY());
        System.out.println("Shape translation: " + shapeTranslation.getWidth() + " " + shapeTranslation.getHeight());


        shapeRGBA[0] = ((Color)shape.shape.getFill()).getRed();
        shapeRGBA[1] = ((Color)shape.shape.getFill()).getGreen();
        shapeRGBA[2] = ((Color)shape.shape.getFill()).getBlue();
        shapeRGBA[3] = ((Color)shape.shape.getFill()).getOpacity();

        type = shape.type;
    }

    public static MyShape createMyShape(MyShapeSerializable mss, AnchorPane canva) {
        MyShape shape = new MyShape();

        shape.type = mss.type;

        switch (mss.type) {
            case POLYGON:
                shape.shape = new Polygon();
                for (Size point : mss.shapePolygonVertices) {
                    ((Polygon) shape.shape).getPoints().addAll(point.getWidth(), point.getHeight());
                }
                break;
            case ELLIPSE:
                shape.shape = ShapeCalc.calcEllipse(
                    new Size(0, 0),
                    new Size(mss.shapeSize.getWidth(), mss.shapeSize.getHeight())
                );
                shape.tempOrigin = new Size(mss.shapeOrigin.getWidth(), mss.shapeOrigin.getHeight());
                break;
            case RECTANGLE:
                shape.shape = ShapeCalc.calcRectangle(
                    new Size(0, 0),
                    new Size(mss.shapeSize.getWidth(), mss.shapeSize.getHeight())
                );
                shape.tempOrigin = new Size(mss.shapeOrigin.getWidth(), mss.shapeOrigin.getHeight());
                break;
            default:
                System.out.println("Error: cannot read shape type");
                return null;
        }

        shape.originalSize = mss.shapeSize;
        shape.originalOrigin = mss.shapeOrigin;

        shape.shape.setRotate(mss.shapeRotation);
        shape.shape.setScaleX(mss.shapeScale);
        shape.shape.setScaleY(mss.shapeScale);
        shape.shape.setTranslateX(mss.shapeTranslation.getWidth());
        shape.shape.setTranslateY(mss.shapeTranslation.getHeight());

        Color shapeFill = new Color(mss.shapeRGBA[0], mss.shapeRGBA[1], mss.shapeRGBA[2], mss.shapeRGBA[3]);
        shape.shape.setFill(shapeFill);

        shape.createSelectionRectangle();

        return shape;
    }
}
