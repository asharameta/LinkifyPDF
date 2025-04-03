package supervisor.model;

public class Selection {
    private String url;
    private double x;
    private double y;
    private double width;
    private double height;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Selection{" +
                "url=" + url +
                "x=" + x +
                "y=" + y +
                "width=" + width +
                "height=" + height +
                '}';
    }
}
