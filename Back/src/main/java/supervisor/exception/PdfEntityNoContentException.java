package supervisor.exception;

import java.io.IOException;

public class PdfEntityNoContentException extends RuntimeException{
    public PdfEntityNoContentException() {
        super("No PDFs found or failed to retrieve them.");
    }
}
