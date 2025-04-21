package supervisor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "selections")
public class SelectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pdf_id", nullable = false)
    private PDFEntity pdf;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(nullable = false)
    private double width;

    @Column(nullable = false)
    private double height;

    // Getters and Setters


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

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
        return "Selection{" + "\n"+
                "url=" + url + "\n"+
                "x=" + x + "\n"+
                "y=" + y + "\n"+
                "width=" + width + "\n"+
                "height=" + height + "\n"+
                '}';
    }
}
