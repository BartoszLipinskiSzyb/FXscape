import java.io.Serializable;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Zapis kształtu pozwlający na serializację
 */
public class MyShapeSerializable implements Serializable {
    /**
     * Kolor wypełnienia
     */
    public double[] shapeRGBA = new double[4];
    /**
     * Początkowy lewy górny róg prostokąta ograniczającego kształt
     */
    public Size shapeOrigin;
    /**
     * Początkowy rozmiar
     */
    public Size shapeSize;
    /**
     * Obrót
     */
    public double shapeRotation;
    /**
     * Skala
     */
    public double shapeScale;
    /**
     * Przesunięcie
     */
    public Size shapeTranslation;
    /**
     * Wierzchołki wielokąta
     */
    public Size[] shapePolygonVertices;
    /**
     * Typ
     */
    public MyShape.Type type;

    /**
     * Konstruktor
     * @param shape kształt
     */
    public MyShapeSerializable(MyShape shape) {
        if (shape.type == MyShape.Type.POLYGON) {
            shapePolygonVertices = shape.getPolygonVertices();
        }
        shapeSize = new Size(shape.originalSize.getWidth(), shape.originalSize.getHeight());
        shapeOrigin = new Size(shape.originalOrigin.getWidth(), shape.originalOrigin.getHeight());

        shapeRotation = shape.shape.getRotate();
        shapeScale = shape.shape.getScaleX(); // zapisujemy tylko jedną oś, jako że program nie obsługuje skalowania w osiach osobno
        shapeTranslation = new Size(shape.shape.getTranslateX(), shape.shape.getTranslateY());


        shapeRGBA[0] = ((Color)shape.shape.getFill()).getRed();
        shapeRGBA[1] = ((Color)shape.shape.getFill()).getGreen();
        shapeRGBA[2] = ((Color)shape.shape.getFill()).getBlue();
        shapeRGBA[3] = ((Color)shape.shape.getFill()).getOpacity();

        type = shape.type;
    }

    /**
     * Odtworzenie kształtu MyShape z serializowalnego formatu
     * @param mss serializowalny kształt
     * @param canva kanwa, na której ma być odtworzony kształt
     * @return MyShape
     */
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
