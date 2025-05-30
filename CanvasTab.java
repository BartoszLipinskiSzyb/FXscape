import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Obsługa zdarzeń na kanwie, zachowanie kształtów
 */
public class CanvasTab extends Tab {
    private AnchorPane canvas;

    private ArrayList<MyShape> shapes = new ArrayList<>();

    
    /** 
     * canvas getter
     * @return AnchorPane
     */
    public AnchorPane getCanvas() {
        return this.canvas;
    }

    private Size canvasDimension;

    /**
     * canvasDimension getter
     * @return Size
     */
    public Size getCanvasDimension() {
        return this.canvasDimension;
    }

    private Size clickStart;
    private Size lastClickScene;
    private Boolean shapeIsDrawn = false;

    private ContextMenu colorPickerPopup;
    private ColorPicker cpEdit;

    private Boolean isPolygonDrawn = false;

    /**
     * shapes setter
     * @param shapes kształty do ustawienia
     */
    public void setShapes(ArrayList<MyShape> shapes) {
        this.shapes = shapes;
    }

    /**
     * shapes getter
     * @return ArrayList
     */
    public ArrayList<MyShape> getShapes() {
        return this.shapes;
    }

    /**
     * Dodawanie kształtu
     * @param shape kształt do dodania
     */
    public void addShape(MyShape shape) {
        this.shapes.add(shape);
        this.canvas.getChildren().add(shape.shape);
    }

    /**
     * Usuwanie kształtu
     * @param shape kształt do usunięcia
     */
    public void removeShape(MyShape shape) {
        this.shapes.remove(shape);
        this.canvas.getChildren().remove(shape.shape);
    }

    /**
     * Przypisanie kształtom metod służącym zanzaczaniu i zmienianiu koloru
     * @param context kontekst primary controller
     * @param currShape kształt, któremu zostaną przypisane listenery
     */
    public void applyShapeEventListeners(PrimaryController context, MyShape currShape) {
        currShape.shape.setOnMousePressed(e1 -> {
            currShape.translationBeforeTranslation = new Size(currShape.shape.getTranslateX(), currShape.shape.getTranslateY());
            if (context.getSelectedTool() == PrimaryController.TOOLS.POINTER) {
                if (e1.getButton() == MouseButton.PRIMARY) {
                    currShape.lastPointClicked = new Size(e1.getX(), e1.getY());

                    for (MyShape shape1 : this.shapes) {
                        shape1.deselect();
                    }

                    if (!e1.isShiftDown()) {
                        currShape.select();
                    }
                } else if (e1.getButton() == MouseButton.SECONDARY) {
                    colorPickerPopup.hide();
                    colorPickerPopup.show(context.getScene().getWindow(), e1.getScreenX(), e1.getScreenY());

                    cpEdit.setValue((Color) currShape.shape.getFill());

                    cpEdit.setOnAction(e2 -> {
                        currShape.shape.setFill(cpEdit.getValue());
                        colorPickerPopup.hide();
                    });
                }
            }
        });

        currShape.shape.setOnMouseDragged(e1 -> {
            if (currShape.selected && context.getSelectedTool() == PrimaryController.TOOLS.POINTER) {
                double dx = e1.getSceneX() - lastClickScene.getWidth();
                double dy = e1.getSceneY() - lastClickScene.getHeight();

                currShape.shape.setTranslateX(currShape.translationBeforeTranslation.getWidth() + dx);
                currShape.shape.setTranslateY(currShape.translationBeforeTranslation.getHeight() + dy);;
                currShape.updateSelectionRectangle();
            }
        });
    }

    /**
     * Usunięcie zaznaczenia na wszystkich kształtach
     */
    private void deselectAllShapes() {
        for (MyShape shape : this.shapes) {
            shape.deselect();
        }
    }

    /**
     * Konstruktor, tworzy kanwę i ustawia jej event listenery
     * @param context kontekst primary controller
     * @param canvasSize rozmiar kanwy
     */
    public CanvasTab(PrimaryController context, Size canvasSize) {
        this.canvasDimension = canvasSize;

        ScrollPane scrollPaneCanvasRoot = new ScrollPane();
        this.canvas = new AnchorPane();
        this.canvas.setMaxWidth(canvasSize.getWidth());
        this.canvas.setMaxHeight(canvasSize.getHeight());
        this.canvas.setMinWidth(canvasSize.getWidth());
        this.canvas.setMinHeight(canvasSize.getHeight());

        this.cpEdit = new ColorPicker();
        CustomMenuItem colorItem = new CustomMenuItem(cpEdit);
        colorItem.setHideOnClick(false);
        this.colorPickerPopup = new ContextMenu(colorItem);
        this.colorPickerPopup.setStyle("-fx-background-color: #ffffff00;");

        context.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                for (MyShape shape : this.shapes) {
                    if (shape.selected) {
                        shape.deselect();
                        this.removeShape(shape);
                        break;
                    }
                }
            }
        });

        this.canvas.setOnMousePressed(e -> {
            clickStart = new Size(e.getX(), e.getY());
            lastClickScene = new Size(e.getSceneX(), e.getSceneY());
            MyShape newShape;
            switch (context.getSelectedTool()) {
                case RECTANGLE:
                    newShape = new MyShape(ShapeCalc.calcRectangle(clickStart, clickStart), this.canvas, MyShape.Type.RECTANGLE);
                    newShape.shape.setFill(context.getSelectedColor());
                    this.addShape(newShape);
                    this.shapeIsDrawn = true;
                    deselectAllShapes();
                    break;
                case CIRCLE:
                    newShape = new MyShape(ShapeCalc.calcEllipse(clickStart, clickStart), this.canvas, MyShape.Type.ELLIPSE);
                    newShape.shape.setFill(context.getSelectedColor());
                    this.addShape(newShape);
                    this.shapeIsDrawn = true;
                    deselectAllShapes();
                    break;
                case POLYGON:
                    if (!this.isPolygonDrawn) {
                        this.isPolygonDrawn = true;
                        newShape = new MyShape(new Polygon(), this.canvas, MyShape.Type.POLYGON);

                        newShape.addPolygonVertex(new Point2D(e.getX(), e.getY()));
                        newShape.addPolygonVertex(new Point2D(e.getX(), e.getY()));

                        newShape.shape.setFill(context.getSelectedColor());
                        newShape.shape.setStroke(Color.BLACK);
                        newShape.shape.setStrokeWidth(1);
                        this.addShape(newShape);

                        deselectAllShapes();
                    } else {
                        MyShape currShape = shapes.get(shapes.size()-1);
                        if (e.getButton() == MouseButton.PRIMARY) {
                            if (e.isShiftDown()) {
                                // cofnięcie narysowania wierzchołka wielokąta
                                currShape.removeLastPolygonVertex();
                                if (currShape.getPolygonVertices().length == 0) {
                                    this.removeShape(currShape);
                                    this.isPolygonDrawn = false;
                                }
                            } else {
                                // narysowania wierzchołka wielokąta
                                currShape.addPolygonVertex(new Point2D(e.getX(), e.getY()));
                            }
                        } else if (e.getButton() == MouseButton.SECONDARY) {
                            // koniec rysowania wielokąta
                            this.isPolygonDrawn = false;
                            currShape.shape.setStrokeWidth(0);
                            this.applyShapeEventListeners(context, currShape);
                            Bounds b = currShape.shape.getBoundsInParent();
                            currShape.originalOrigin = new Size(b.getWidth(), b.getHeight());
                            currShape.originalSize = new Size(b.getWidth(), b.getHeight());
                        }
                    }
                    break;
                case POINTER:
                default:
                    break;
            }
        });

        this.canvas.setOnMouseDragged(e -> {
            if (!shapeIsDrawn) return;

            MyShape currShape = this.shapes.get(this.shapes.size()-1);

            double currX = e.getX(), currY = e.getY();
            // rysowanie kwadratu/koła kiedy wciśnięty jest shift
            if (e.isShiftDown()) {
                double dx = currX - this.clickStart.getWidth();
                double dy = currY - this.clickStart.getHeight();
                if (Math.abs(dx) > Math.abs(dy)) {
                    currY = clickStart.getHeight() + dx;
                } else {
                    currX = clickStart.getWidth() + dy;
                }
            }
            switch (context.getSelectedTool()) {
                case RECTANGLE:
                    Bounds newRecBounds = ShapeCalc.calcRectangle(clickStart, new Size(currX, currY)).getBoundsInParent();
                    ((Rectangle)currShape.shape).setX(newRecBounds.getMinX());
                    ((Rectangle)currShape.shape).setY(newRecBounds.getMinY());
                    ((Rectangle)currShape.shape).setWidth(newRecBounds.getWidth());
                    ((Rectangle)currShape.shape).setHeight(newRecBounds.getHeight());
                    break;
                case CIRCLE:
                    Bounds newEllBounds = ShapeCalc.calcEllipse(clickStart, new Size(currX, currY)).getBoundsInParent();
                    ((Ellipse)currShape.shape).setCenterX(newEllBounds.getCenterX());
                    ((Ellipse)currShape.shape).setCenterY(newEllBounds.getCenterY());
                    ((Ellipse)currShape.shape).setRadiusX(newEllBounds.getWidth()/2);
                    ((Ellipse)currShape.shape).setRadiusY(newEllBounds.getHeight()/2);
                    break;
                default:
                    return;
            }

        });

        this.canvas.setOnMouseReleased(e -> {
            switch (context.getSelectedTool()) {
                case RECTANGLE:
                case CIRCLE:
                    MyShape currShape = this.shapes.get(this.shapes.size() - 1);
                    this.applyShapeEventListeners(context, currShape);
                    this.shapeIsDrawn = false;
                    Bounds b = currShape.shape.getBoundsInParent();
                    currShape.originalSize = new Size(b.getWidth(), b.getHeight());
                    currShape.originalOrigin = new Size(b.getMinX(), b.getMinY());
                default:
                    break;
            }
        });

        this.canvas.setOnScroll(e -> {
            MyShape selectedShape = null;
            for (MyShape shape : shapes) {
                if (shape.selected) {
                    selectedShape = shape;
                }
            }
            if (selectedShape == null) { return; }

            double scrollValue = e.getDeltaY()/e.getMultiplierY();
            if (e.isControlDown()) {
                selectedShape.shape.setRotate(selectedShape.shape.getRotate() + scrollValue*3);
            } else {
                selectedShape.shape.setScaleX(selectedShape.shape.getScaleX() + scrollValue*0.05);
                selectedShape.shape.setScaleY(selectedShape.shape.getScaleY() + scrollValue*0.05);
            }

            selectedShape.updateSelectionRectangle();
        });

        this.canvas.setOnMouseMoved(e -> {
            if (!this.isPolygonDrawn) { return; }

            MyShape currShape = this.shapes.get(this.shapes.size()-1);
            try {
                Polygon poly = (Polygon) currShape.shape;
            } catch (Exception exc) {
                System.out.println("Error: polygon is drawn, but shape is not a polygon");
                return;
            }

            currShape.setPolygonVertex(new Size(e.getX(), e.getY()), currShape.getPolygonVertices().length-1);
        });

        BorderStroke borderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1), new Insets(5));
        BorderPane borderPane = new BorderPane();
        borderPane.setBorder(new Border(borderStroke));
        borderPane.setCenter(this.canvas);
        borderPane.setStyle("-fx-background-color: white");

        scrollPaneCanvasRoot.setContent(borderPane);

        this.setText("*");
        this.setContent(scrollPaneCanvasRoot);
    }
}
