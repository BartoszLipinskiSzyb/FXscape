import java.io.Serializable;

public class SaveFileContent implements Serializable {
    public Size canvaSize;
    public MyShapeSerializable[] shapes;

    public SaveFileContent(Size canvaSize, MyShapeSerializable[] shapes) {
        this.canvaSize = canvaSize;
        this.shapes = shapes;
    }
}
