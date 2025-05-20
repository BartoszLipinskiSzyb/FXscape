import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główna klasa aplikacji
 */
public class App extends Application {
    /** 
     * Inicjalizacja funkcjonalności aplikacji
     * @param stage główny Stage aplikacji, tworzony przez JavaFX
     * @throws IOException niemożliwość zainicjalizowania aplikacji
     */
    @Override
    public void start(Stage stage) throws IOException {
        new PrimaryController(stage);
        stage.setTitle("Inkscape");
    }

    /** 
     * Załadowanie FXML z pliku
     * @param fxml nazwa pliku fxml do załadowania, bez rozszerzenia
     * @return Parent
     * @throws IOException niemożliwość załadowania pliku FXML
     */
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /** 
     * Start
     * @param args argumenty linii poleceń
     */
    public static void main(String[] args) {
        launch();
    }
}
