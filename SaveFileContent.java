import java.io.Serializable;

/**
 * Klasa zbierająca informacje o projekcie zapisywane w pliku
 */
public class SaveFileContent implements Serializable {
    /**
     * Rozmiar kanwy
     */
    public Size canvaSize;
    /**
     * Kształty
     */
    public MyShapeSerializable[] shapes;

    /**
     * Konstruktor
     * @param canvaSize rozmiar kanwy
     * @param shapes kształty do zapisania
     */
    public SaveFileContent(Size canvaSize, MyShapeSerializable[] shapes) {
        this.canvaSize = canvaSize;
        this.shapes = shapes;
    }
}
