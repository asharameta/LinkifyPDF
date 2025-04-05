package supervisor.model;

import java.util.ArrayList;
import java.util.List;

public class PDFDataDAO {
    public List<PDFData> tempPDF;

    public PDFDataDAO(){
        tempPDF = new ArrayList<>();
    }

    public List<PDFData> getAllPDFData(){
        return tempPDF;
    }

    public PDFData getPDFData(int i){
        return tempPDF.get(i);
    }

    public void addPDFData(PDFData data){
        System.out.println(data.toString());
        tempPDF.add(data);
    }

    public void deletePDFData(int id){
        tempPDF.remove(id);
    }
}
