import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Główny kontroler odpowiadający za przyciski w menu, okno z zakładkami i zapisywanie projektów
 */
public class PrimaryController {
    private Scene scene;
    private ToggleGroup toolButtonsGroup;
    private ToggleButton toolPointer;
    private ToggleButton toolRectangle;
    private ToggleButton toolCircle;
    private ToggleButton toolPolygon;
    private TabPane tabPaneWithCanvas;
    private ColorPicker colorPicker;
    private MenuButton menuButtonFile;

    private Button buttonInfo;

    /**
     * Rodzaje narzędzi
     */
    public enum TOOLS {
        /**
         * Wskaźnik, pozwalający zaznaczać, przesuwać
         */
        POINTER,
        /**
         * Rysowanie prostokąta
         */
        RECTANGLE,
        /**
         * Rysowanie elipsy
         */
        CIRCLE,
        /**
         * Rysowanie wielokąta
         */
        POLYGON,
        /**
         * Nie wybrano narzędzia
         */
        NONE
    }

    private TOOLS selectedTool = TOOLS.NONE;

    /**
     * Getter selectedTool - wybranego narzędzia na pasku po lewej
     * @return TOOLS
     */
    public TOOLS getSelectedTool() {
        return this.selectedTool;
    }

    /**
     * Getter colorPicker.value - wybranego koloru na pasku na górze
     * @return Color
     */
    public Color getSelectedColor() {
        return this.colorPicker.getValue();
    }

    /**
     * Konstruktor
     * @param stage główny stage aplikacji
     * @throws IOException bład przy inicjalizacji kontrolera
     */
    public PrimaryController(Stage stage) throws IOException {
        this.scene = new Scene(App.loadFXML("primary"), 1200, 800);
        this.scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        stage.setScene(this.scene);

        initializeUI();
        stage.show();
        initializePostRender();
    }

    private ArrayList<CanvasTab> canvasTabs = new ArrayList<>();

    /**
     * Stworzenie kanwy i dodanie jej jako zakładki
     * @param canvasSize rozmiar kanwy
     */
    public void createCanvas(Size canvasSize) {
        CanvasTab newTab = new CanvasTab(this, canvasSize);

        canvasTabs.add(newTab);

        this.tabPaneWithCanvas.getTabs().add(newTab);
        this.tabPaneWithCanvas.getSelectionModel().select(newTab);
    }

    /**
     * Załadowanie kanwy z pliku
     * @param filename nazwa pliku
     */
    public void loadCanvas(String filename) {
        CanvaSaver cs = new CanvaSaver();
        CanvasTab newTab = cs.loadCanva(filename, this);

        canvasTabs.add(newTab);

        this.tabPaneWithCanvas.getTabs().add(newTab);
        this.tabPaneWithCanvas.getSelectionModel().select(newTab);

        // dodanie kształtów po wyrenderowaniu parenta dla nich, aby można je było pozycjonować
        cs.loadShapes(this, newTab);
    }

    /**
     * Zapisanie kanwy do pliku
     * @param filename nazwa pliku
     */
    public void saveCanvas(String filename) {
        try {
            CanvasTab tabToSave = (CanvasTab) this.tabPaneWithCanvas.getSelectionModel().getSelectedItem();
            if (!CanvaSaver.saveCanva(filename, tabToSave.getShapes(), tabToSave.getCanvasDimension())) {
                System.out.println("Failed to save canva: " + filename);
            }
        } catch (Exception e) {
            System.out.println("Cannot save tab that is not CanvasTab");
        }
    }

    /**
     * Inicjalizacja przycisków i zakładki powitania
     */
    private void initializeUI() {
        toolPointer = (ToggleButton) this.scene.getRoot().lookup("#togglebutton_pointer");
        toolRectangle = (ToggleButton) this.scene.getRoot().lookup("#togglebutton_rectangle");
        toolCircle = (ToggleButton) this.scene.getRoot().lookup("#togglebutton_circle");
        toolPolygon = (ToggleButton) this.scene.getRoot().lookup("#togglebutton_polygon");

        toolPointer.setOnMouseClicked(e -> {
            this.selectedTool = TOOLS.POINTER;
        });
        toolRectangle.setOnMouseClicked(e -> {
            this.selectedTool = TOOLS.RECTANGLE;
        });
        toolCircle.setOnMouseClicked(e -> {
            this.selectedTool = TOOLS.CIRCLE;
        });
        toolPolygon.setOnMouseClicked(e -> {
            this.selectedTool = TOOLS.POLYGON;
        });

        toolButtonsGroup = new ToggleGroup();
        toolPointer.setToggleGroup(toolButtonsGroup);
        toolRectangle.setToggleGroup(toolButtonsGroup);
        toolCircle.setToggleGroup(toolButtonsGroup);
        toolPolygon.setToggleGroup(toolButtonsGroup);

        tabPaneWithCanvas = (TabPane) this.scene.getRoot().lookup("#tabpane_with_canvas");
        tabPaneWithCanvas.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
        tabPaneWithCanvas.tabDragPolicyProperty().set(TabDragPolicy.REORDER);

        new WelcomeTab(this);
    }

    /**
     * Inicjalizacja przycisków po wyrenderowaniu sceny - obejście
     */
    private void initializePostRender() {
        colorPicker = (ColorPicker) this.scene.getRoot().lookup("#colorpicker_topbar");
        colorPicker.setValue(Color.BLACK);

        buttonInfo = (Button) scene.getRoot().lookup("#button_info");
        buttonInfo.setOnMouseClicked(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initOwner(scene.getWindow());

            Pane emptyPane = new Pane();
            Scene dialogScene = new Scene(emptyPane);

            try {
                dialogScene.setRoot(App.loadFXML("dialogInfo"));
            } catch (Exception err) {
                System.out.println("Cannot load dialog window FXML: " + err.getMessage());
                return;
            }
            ((Label) dialogScene.lookup("#label_info")).setText(Consts.programInfo);
            dialogScene.lookup("#button_info_close").setOnMouseClicked(e1 -> {
                dialog.close();
            });

            dialog.setScene(dialogScene);
            dialog.show();
        });

        menuButtonFile = (MenuButton) this.scene.getRoot().lookup("#menubutton_file");
        menuButtonFile.getItems().get(0).setOnAction(e -> {
            WelcomeTab.showCreateDialog(this);
        });
        menuButtonFile.getItems().get(1).setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Otwórz projekt");
            File fileToLoad = fc.showOpenDialog(this.scene.getWindow());
            if (fileToLoad == null) { return; }
            loadCanvas(fileToLoad.getAbsolutePath());
            tabPaneWithCanvas.getSelectionModel().getSelectedItem().setText(fileToLoad.getName());
        });
        menuButtonFile.getItems().get(2).setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Zapisz projekt");
            fc.setInitialFileName("projekt.cnv");
            File fileToSave = fc.showSaveDialog(this.scene.getWindow());
            if (fileToSave == null) { return; }
            saveCanvas(fileToSave.getAbsolutePath());
            tabPaneWithCanvas.getSelectionModel().getSelectedItem().setText(fileToSave.getName());
        });
    }

    /**
     * Getter scene
     * @return Scene
     */
    public Scene getScene() {
        return this.scene;        
    }
}