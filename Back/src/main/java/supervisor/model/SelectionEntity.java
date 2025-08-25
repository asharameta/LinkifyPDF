package supervisor.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "selections")
public class SelectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    // Normalized coordinates [0..1]
    @Column(name = "x_norm", nullable = false, precision = 6, scale = 5)
    private double  xNorm;

    @Column(name = "y_norm", nullable = false, precision = 6, scale = 5)
    private double  yNorm;

    @Column(name = "w_norm", nullable = false, precision = 6, scale = 5)
    private double  wNorm;

    @Column(name = "h_norm", nullable = false, precision = 6, scale = 5)
    private double  hNorm;

    @Column(name = "link_type", nullable = false)
    private LinkType linkType = LinkType.URL;

    @Column(name = "link_value", nullable = false)
    private String linkValue;

    public SelectionEntity(){}

    public DocumentEntity getDocumentId() {
        return document;
    }

    public void setDocumentId(long documentId) {
        this.document.setId(documentId);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public double  getxNorm() {
        return xNorm;
    }

    public void setxNorm(double  xNorm) {
        this.xNorm = xNorm;
    }

    public double getyNorm() {
        return yNorm;
    }

    public void setyNorm(double yNorm) {
        this.yNorm = yNorm;
    }

    public double getwNorm() {
        return wNorm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setwNorm(double wNorm) {
        this.wNorm = wNorm;
    }

    public double gethNorm() {
        return hNorm;
    }

    public void sethNorm(double hNorm) {
        this.hNorm = hNorm;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }
}
