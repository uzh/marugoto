package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class PdfNotebookEntry extends NotebookEntry {
	
	@Ref
	private PdfResource pdfResource;

	public PdfResource getPdfResource() {
		return pdfResource;
	}

	public void setPdfResource(PdfResource pdfResource) {
		this.pdfResource = pdfResource;
	}
	
	public PdfNotebookEntry() {
		super();
	}
	
	public PdfNotebookEntry(PdfResource pdfResource) {
		super();
		this.pdfResource = pdfResource;
	}
}
