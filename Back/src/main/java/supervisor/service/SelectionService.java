package supervisor.service;

import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;

import java.util.List;

public class SelectionService {
    private final SelectionEntityDAO selectionEntityDAO;

    public SelectionService(SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
    }

    public List<List<SelectionEntity>> getAllSelectionData(){
        return selectionEntityDAO.getAllSelectionData();
    }

    public List<SelectionEntity> getSelectionData(Long id){
        return selectionEntityDAO.getSelectionData(id);
    }

}
