package supervisor.exception;

public class PdfEntityNotFoundException extends RuntimeException{
    public PdfEntityNotFoundException(Long id) {
        super("PDFEntity with id: " + id + " not found.");
    }
}
