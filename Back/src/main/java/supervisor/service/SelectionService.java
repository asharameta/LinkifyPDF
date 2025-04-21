package supervisor.service;

import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;

import java.util.List;

public class SelectionService {
    private final SelectionEntityDAO selectionEntityDAO;

    public SelectionService(SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
    }

    public List<SelectionEntity> getAllPdfData(){
        return selectionEntityDAO.getAllSelectionData();
    }

    public SelectionEntity getPdfData(Long id){
        return selectionEntityDAO.getSelectionData(id);
    }

}
