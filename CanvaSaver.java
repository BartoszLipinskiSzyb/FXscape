import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.geometry.Dimension2D;

public class CanvaSaver {
    public static Boolean saveCanva(String filename, ArrayList<MyShape> shapes, Size canvasDimension) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(canvasDimension.getWidth());
            oos.writeObject(canvasDimension.getHeight());

            oos.close();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return false;
        } catch (IOException e) {
            System.out.println("Cannot write to file: " + filename);
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    public static CanvasTab loadCanva(String filename, PrimaryController context) {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(in);

            Size canvasSize = new Size((Double) ois.readObject(), (Double) ois.readObject());
            CanvasTab canva = new CanvasTab(context, canvasSize);

            // canva.setShapes((ArrayList<MyShape>) ois.readObject());

            ois.close();
            in.close();

            return canva;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (IOException e) {
            System.out.println("Cannot read from file: " + filename);
        } catch (ClassNotFoundException e) {
            System.out.println("File corrupted: " + filename);
        }

        return null;
    }
}
