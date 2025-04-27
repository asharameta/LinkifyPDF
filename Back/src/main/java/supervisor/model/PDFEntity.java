package supervisor.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pdfs")
public class PDFEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

//    @OneToMany(mappedBy = "pdf", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SelectionEntity> selectionEntities;


    public PDFEntity() {

    }

//    public List<SelectionEntity> getSelections() {
//        return selectionEntities;
//    }
//
//    public void setSelections(List<SelectionEntity> selectionEntities) {
//        this.selectionEntities = selectionEntities;
//    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PdfEntity{" +
                "id=" + id + "\n"+
                "filename=" + filename + "\n" +
                "uploadedAt=" + uploadedAt +"\n"+
                '}';
    }
}

