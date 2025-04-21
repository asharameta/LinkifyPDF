package supervisor.exception;

public class SelectionEntityNotFoundException extends RuntimeException{
    public SelectionEntityNotFoundException(Long id) {
        super("SelectionEntity with id: " + id + " not found.");
    }
}
