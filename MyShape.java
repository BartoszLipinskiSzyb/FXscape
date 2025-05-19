import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class MyShape implements Cloneable {
    public Shape shape;
    public Boolean selected;
    public Rectangle selectionRec;
    public AnchorPane parentCanvas;
    public Boolean isColorMenuOpened;
    public Size lastPointClicked;
    public Type type;
    public Size centerOfRotation;
    public Size originOnTranslateStart;
    public Size translationBeforeTranslation;
    public Size originalSize;

    public enum Type {
        RECTANGLE,
        ELLIPSE,
        POLYGON
    }

    public MyShape(Shape shape, AnchorPane parentCanvas, Type type) {
        this.shape = shape;
        this.parentCanvas = parentCanvas;
        this.selected = false;
        this.isColorMenuOpened = false;
        this.type = type;
        createSelectionRectangle();
    }

    public MyShape() {
        this.selected = false;
    }

    // zamiana wewnętrznego zapisu punktów klasy Polygon na wygodniejszy w użyciu
    public Size[] getPolygonVertices() {
        if (this.type != Type.POLYGON) { return null; }

        Polygon poly = (Polygon) this.shape;

        Size[] result = new Size[poly.getPoints().size()/2];

        for (int i = 0; i < poly.getPoints().size(); i += 2) {
            result[i/2] = new Size(poly.getPoints().get(i), poly.getPoints().get(i+1));
        }

        return result;
    }

    public void addPolygonVertex(Point2D vertex) {
        if (this.type != Type.POLYGON) { return; }

        ((Polygon) this.shape).getPoints().addAll(vertex.getX(), vertex.getY());
    }

    public void setPolygonVertex(Size vertex, int index) {
        if (this.type != Type.POLYGON) { return; }

        Polygon poly = (Polygon) this.shape;

        if (index < 0 || index >= poly.getPoints().size()/2) { return; }

        poly.getPoints().set(2*index, vertex.getWidth());
        poly.getPoints().set(2*index+1, vertex.getHeight());
    }

    public Size getPolygonVertex(int index) {
        if (this.type != Type.POLYGON) { return null; }

        return getPolygonVertices()[index];
    }

    public void removeLastPolygonVertex() {
        if (this.type != Type.POLYGON) { return; }

        ((Polygon) this.shape).getPoints().removeLast();
        ((Polygon) this.shape).getPoints().removeLast();
    }

    public void setParentCanvas(AnchorPane canva) {
        this.parentCanvas = canva;
    }

    public void saveCenterOfRotation() {
        Bounds bounds = this.shape.getBoundsInParent();
        this.centerOfRotation = new Size(bounds.getCenterX(), bounds.getCenterY());
    }

    // stworzenie prostokąta pojawiąjącego się kiedy kształt jest zaznaczony
    public void createSelectionRectangle() {
        Bounds bounds = this.shape.getBoundsInParent();
        this.selectionRec = ShapeCalc.calcRectangle(new Size(bounds.getMinX(), bounds.getMinY()), new Size(bounds.getMaxX(), bounds.getMaxY()));
        this.selectionRec.setFill(Color.LIGHTSKYBLUE);
        this.selectionRec.setStroke(Color.BLUE);
        this.selectionRec.setOpacity(0.1);
        this.selectionRec.setStrokeWidth(5);
        this.selectionRec.setMouseTransparent(true);
    }

    // aktualizacja wymiarów prostokąta zaznaczenia
    public void updateSelectionRectangle() {
        Bounds bounds = this.shape.getBoundsInParent();
        this.selectionRec.setX(bounds.getMinX());
        this.selectionRec.setY(bounds.getMinY());
        this.selectionRec.setWidth(bounds.getWidth());
        this.selectionRec.setHeight(bounds.getHeight());
    }

    public void select() {
        if (this.parentCanvas == null || this.selected) return;

        this.selected = true;
        this.updateSelectionRectangle();
        this.parentCanvas.getChildren().add(this.selectionRec);
    }

    public void deselect() {
        if (this.parentCanvas == null || !this.selected) return;

        this.selected = false;
        if (!this.parentCanvas.getChildren().contains(this.selectionRec)) {
            System.out.println("Error");
        }
        this.parentCanvas.getChildren().remove(this.selectionRec);
    }
}
