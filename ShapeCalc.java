import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * Kalkulator kształtów
 */
public class ShapeCalc {
    /**
     * Tworzy prostokąt o lewym górnym rogu w point1 i prawym dolnym rogu w point2
     * @param point1 lewy górny róg
     * @param point2 prawy dolny róg
     * @return Rectangle
     */
    public static Rectangle calcRectangle(Size point1, Size point2) {
        double leftTopX = Math.min(point1.getWidth(), point2.getWidth());
        double leftTopY = Math.min(point1.getHeight(), point2.getHeight());
        double width = Math.abs(point1.getWidth()-point2.getWidth());
        double height = Math.abs(point1.getHeight()-point2.getHeight());
        Rectangle rec = new Rectangle(leftTopX, leftTopY, width, height);
        return rec;
    }

    /**
     * Tworzy elipsę ograniczoną prostokątem o lewym górnym rogu w point1 i prawym dolnym rogu w point2
     * @param point1 lewy górny róg ograniczenia
     * @param point2 prawy dolny róg ograniczenia
     * @return Ellipse
     */
    public static Ellipse calcEllipse(Size point1, Size point2) {
        double centerX = (point1.getWidth() + point2.getWidth())/2;
        double centerY = (point1.getHeight() + point2.getHeight())/2;
        double radiusX = Math.abs(point1.getWidth()-point2.getWidth())/2;
        double radiusY = Math.abs(point1.getHeight()-point2.getHeight())/2;
        Ellipse ell = new Ellipse(centerX, centerY, radiusX, radiusY);
        return ell;
    }
}
