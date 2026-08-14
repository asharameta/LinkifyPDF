package supervisor.DTO;

public class ExistingLinkDTO {
    private int pageNumber;
    private double xNorm;
    private double yNorm;
    private double wNorm;
    private double hNorm;
    private String linkType;
    private String linkValue;

    public ExistingLinkDTO(int pageNumber, double xNorm, double yNorm, double wNorm, double hNorm,
                           String linkType, String linkValue) {
        this.pageNumber = pageNumber;
        this.xNorm = xNorm;
        this.yNorm = yNorm;
        this.wNorm = wNorm;
        this.hNorm = hNorm;
        this.linkType = linkType;
        this.linkValue = linkValue;
    }

    public int getPageNumber()  { return pageNumber; }
    public double getxNorm()    { return xNorm; }
    public double getyNorm()    { return yNorm; }
    public double getwNorm()    { return wNorm; }
    public double gethNorm()    { return hNorm; }
    public String getLinkType() { return linkType; }
    public String getLinkValue(){ return linkValue; }
}
