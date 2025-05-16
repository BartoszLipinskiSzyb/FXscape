import java.io.Serializable;

public class Size implements Serializable {
    private Double width;
    private Double height;

    public Size(Double width, Double height) {
        this.width = width;
        this.height = height;
    }

    public Size(Integer width, Integer height) {
        this.width = width+0.0;
        this.height = height+0.0;
    }

    public Double getWidth() {
        return this.width;
    }

    public Double getHeight() {
        return this.height;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
