import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Wrapper dla klasy Shape
 */
public class MyShape {
    /**
     * JavaFX-owy kształt
     */
    public Shape shape;
    /**
     * Czy figura jest zaznaczona
     */
    public Boolean selected;
    /**
     * Prostokąt zaznaczający figurę
     */
    public Rectangle selectionRec;
    /**
     * Kanwa, na której znajduje się kształt
     */
    public AnchorPane parentCanvas;
    /**
     * Ostatni kształt kliknięty na kształcie
     */
    public Size lastPointClicked;
    /**
     * Rodzaj kształtu
     */
    public Type type;
    /**
     * Obecne przesunięcie kształtu zapiywanie przed kolejnym przesunięciem
     */
    public Size translationBeforeTranslation;
    /**
     * Początkowy rozmiar kształtu po utworzeniu
     */
    public Size originalSize;
    /**
     * Początkowy lewy górny róg prostokąta ograniczającego kształt po utworzeniu
     */
    public Size originalOrigin;

    /** 
     * Tymczasowa zmienna pozwalająca obejść problem z ustawieniem layoutX i layoutY dla kształtu
     */
    public Size tempOrigin;

    /**
     * Typ kształtu
     */
    public enum Type {
        /**
         * Prostokąt
         */
        RECTANGLE,
        /**
         * Elipsa
         */
        ELLIPSE,
        /**
         * Wielokąt
         */
        POLYGON
    }

    /**
     * Konstruktor
     * @param shape kształt
     * @param parentCanvas kanwa, na której będzie kształt
     * @param type rodzaj kształtu
     */
    public MyShape(Shape shape, AnchorPane parentCanvas, Type type) {
        this.shape = shape;
        this.parentCanvas = parentCanvas;
        this.selected = false;
        this.type = type;
        createSelectionRectangle();
    }

    /**
     * Konstruktor
     */
    public MyShape() {
        this.selected = false;
    }

    /**
     * Zamiana wewnętrznego zapisu punktów klasy Polygon na wygodniejszy w użyciu
     * @return Size[]
     */
    public Size[] getPolygonVertices() {
        if (this.type != Type.POLYGON) { return null; }

        Polygon poly = (Polygon) this.shape;

        Size[] result = new Size[poly.getPoints().size()/2];

        for (int i = 0; i < poly.getPoints().size(); i += 2) {
            result[i/2] = new Size(poly.getPoints().get(i), poly.getPoints().get(i+1));
        }

        return result;
    }

    /**
     * Dodanie wierzchołka do wielokąta
     * @param vertex koordynaty x y wierzchołka
     */
    public void addPolygonVertex(Point2D vertex) {
        if (this.type != Type.POLYGON) { return; }

        ((Polygon) this.shape).getPoints().addAll(vertex.getX(), vertex.getY());
    }

    /**
     * Zmiana pozycji wierzchołka
     * @param vertex koordynaty wierzchołka
     * @param index indeks
     */
    public void setPolygonVertex(Size vertex, int index) {
        if (this.type != Type.POLYGON) { return; }

        Polygon poly = (Polygon) this.shape;

        if (index < 0 || index >= poly.getPoints().size()/2) { return; }

        poly.getPoints().set(2*index, vertex.getWidth());
        poly.getPoints().set(2*index+1, vertex.getHeight());
    }

    /**
     * Getter pobierający jeden wierzchołek
     * @param index indeks
     * @return Size
     */
    public Size getPolygonVertex(int index) {
        if (this.type != Type.POLYGON) { return null; }

        return getPolygonVertices()[index];
    }

    /**
     * Usunięcie ostatnio dodanego wierzchołka wielokąta
     */
    public void removeLastPolygonVertex() {
        if (this.type != Type.POLYGON) { return; }

        // usuwam 2 razy, ponieważ punkty są zapisane jako double[] gdzie parzyste indexy to xn, a nieparzyste yn
        ((Polygon) this.shape).getPoints().removeLast();
        ((Polygon) this.shape).getPoints().removeLast();
    }

    /**
     * Setter parentCanvas
     * @param canva kanwa
     */
    public void setParentCanvas(AnchorPane canva) {
        this.parentCanvas = canva;
    }

    /**
     * Stworzenie prostokąta pojawiąjącego się kiedy kształt jest zaznaczony
     */
    public void createSelectionRectangle() {
        Bounds bounds = this.shape.getBoundsInParent();
        this.selectionRec = ShapeCalc.calcRectangle(new Size(bounds.getMinX(), bounds.getMinY()), new Size(bounds.getMaxX(), bounds.getMaxY()));
        this.selectionRec.setFill(Color.LIGHTSKYBLUE);
        this.selectionRec.setStroke(Color.BLUE);
        this.selectionRec.setOpacity(0.1);
        this.selectionRec.setStrokeWidth(5);
        this.selectionRec.setMouseTransparent(true);
    }

    /** 
     * Aktualizacja wymiarów prostokąta zaznaczenia
     */
    public void updateSelectionRectangle() {
        Bounds bounds = this.shape.getBoundsInParent();
        this.selectionRec.setX(bounds.getMinX());
        this.selectionRec.setY(bounds.getMinY());
        this.selectionRec.setWidth(bounds.getWidth());
        this.selectionRec.setHeight(bounds.getHeight());
    }

    /**
     * Zaznaczenie kształtu
     */
    public void select() {
        if (this.parentCanvas == null || this.selected) return;

        this.selected = true;
        this.updateSelectionRectangle();
        this.parentCanvas.getChildren().add(this.selectionRec);
    }

    /**
     * Odznaczenie kształtu
     */
    public void deselect() {
        if (this.parentCanvas == null || !this.selected) return;

        this.selected = false;
        if (!this.parentCanvas.getChildren().contains(this.selectionRec)) {
            System.out.println("Error");
        }
        this.parentCanvas.getChildren().remove(this.selectionRec);
    }
}
