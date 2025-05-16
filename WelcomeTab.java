import javax.naming.Context;

import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WelcomeTab {
    public static void showCreateDialog(PrimaryController context) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(context.getScene().getWindow());

        Pane emptyPane = new Pane();
        Scene dialogScene = new Scene(emptyPane);

        try {
            dialogScene.setRoot(App.loadFXML("dialogCreate"));
        } catch (Exception err) {
            System.out.println("Cannot load dialog window FXML: " + err.getMessage());
            return;
        }

        dialogScene.lookup("#button_create").setOnMouseClicked(e1 -> {
            int height, width;
            try {
                height = Integer.parseInt(((TextField) dialogScene.lookup("#textfield_height")).getText());
                width = Integer.parseInt(((TextField) dialogScene.lookup("#textfield_width")).getText());
            } catch (Exception err1) {
                ((Label) dialogScene.lookup("#label_info")).setText("Wprowadź liczby naturalne");
                return;
            }
            context.createCanvas(new Size(height, width));
            dialog.close();
        });

        dialog.setScene(dialogScene);
        dialog.show();
    }

    public WelcomeTab(PrimaryController context) {
        Button buttonWelcomeNewProject = (Button) context.getScene().getRoot().lookup("#button_welcome_new_project");
        buttonWelcomeNewProject.setOnAction(e -> {
            showCreateDialog(context);
        });
    }
}
