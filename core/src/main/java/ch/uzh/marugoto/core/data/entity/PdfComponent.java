package ch.uzh.marugoto.core.data.entity;

public class PdfComponent extends Component {

    private PdfResource pdf;

    public PdfComponent() {
        super();
    }

    public PdfComponent(PdfResource pdf) {
        this();
        this.pdf = pdf;
    }

    public PdfResource getPdf() {
        return pdf;
    }

    public void setPdf(PdfResource pdf) {
        this.pdf = pdf;
    }
}
