import java.io.Serializable;

/**
 * Zapisywanie wymiaru w sposób pozwalający na serializację
 */
public class Size implements Serializable {
    /**
     * Szerokość lub x
     */
    private Double width;
    /**
     * Wysokość lub y
     */
    private Double height;

    /**
     * Kontruktor
     * @param width szerokość lub x
     * @param height wysokość lub y
     */
    public Size(Double width, Double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Konstruktor dla liczb całkowitych
     * @param width szerokość lub x
     * @param height wysokość lub y
     */
    public Size(Integer width, Integer height) {
        this.width = width+0.0;
        this.height = height+0.0;
    }

    /**
     * Getter width
     * @return Double
     */
    public Double getWidth() {
        return this.width;
    }

    /**
     * Getter height
     * @return Double
     */
    public Double getHeight() {
        return this.height;
    }

    /**
     * Setter width
     * @param width szerokość lub x
     */
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Setter height
     * @param height wysokość lub y
     */
    public void setHeight(Double height) {
        this.height = height;
    }
}
