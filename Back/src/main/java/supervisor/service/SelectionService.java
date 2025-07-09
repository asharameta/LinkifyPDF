package supervisor.service;

import org.springframework.stereotype.Service;
import supervisor.model.SelectionEntity;
import supervisor.model.SelectionEntityDAO;

import java.util.List;

@Service
public class SelectionService {
    private final SelectionEntityDAO selectionEntityDAO;

    public SelectionService(SelectionEntityDAO selectionEntityDAO) {
        this.selectionEntityDAO = selectionEntityDAO;
    }

    public List<SelectionEntity> getAllSelectionData(){
        return selectionEntityDAO.getAllSelectionData();
    }

    public List<SelectionEntity> getSelectionData(Long id){
        return selectionEntityDAO.getSelectionData(id);
    }

}
