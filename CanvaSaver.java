import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Zapisywanie i wczytywanie projektów z pliku
 */
public class CanvaSaver {
    /**
     * Zawartość wczytana z pliku
     */
    public SaveFileContent content;


    /** 
     * Zapisywanie projektu do pliku
     * @param filename nazwa pliku
     * @param shapes kształty do zapisania
     * @param canvasDimension wymiar kanwy do zapisania
     * @return Boolean
     */
    public static Boolean saveCanva(String filename, ArrayList<MyShape> shapes, Size canvasDimension) {
        MyShapeSerializable[] shapesToSave = new MyShapeSerializable[shapes.size()];

        for (int i = 0; i < shapesToSave.length; ++i) {
            shapesToSave[i] = new MyShapeSerializable(shapes.get(i));
        }

        SaveFileContent fileContent = new SaveFileContent(canvasDimension, shapesToSave);

        try {
            FileOutputStream out = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(fileContent);

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

    
    /** 
     * Wczytywanie projektu z pliku
     * @param filename nazwa pliku
     * @param context kontekst primary controller
     * @return CanvasTab
     */
    public CanvasTab loadCanva(String filename, PrimaryController context) {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(in);

            this.content = (SaveFileContent) ois.readObject();

            CanvasTab canva = new CanvasTab(context, this.content.canvaSize);

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

    
    /** 
     * Ustawianie kształtów na kanwie po wcześniejszym wczytaniu przez loadCanva
     * @param context kontekst primary controller
     * @param canva kanwa na którą mają być wczytane kształty
     * @see loadCanva
     */
    public void loadShapes(PrimaryController context, CanvasTab canva) {
        for (MyShapeSerializable shapeSerializable : this.content.shapes) {
            MyShape shape = MyShapeSerializable.createMyShape(shapeSerializable, canva.getCanvas());
            shape.setParentCanvas(canva.getCanvas());

            canva.applyShapeEventListeners(context, shape);

            canva.addShape(shape);
       
            if (shape.type != MyShape.Type.POLYGON) {
                shape.shape.setLayoutX(shape.tempOrigin.getWidth());
                shape.shape.setLayoutY(shape.tempOrigin.getHeight());
            }
        }
    }
}
