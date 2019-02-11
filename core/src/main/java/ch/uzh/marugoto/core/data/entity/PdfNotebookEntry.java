package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class PdfNotebookEntry extends NotebookEntry {
	
	@Ref
	private PdfResource pdf;

	public PdfResource getPdf() {
		return pdf;
	}

	public void setPdf(PdfResource pdf) {
		this.pdf = pdf;
	}
	
	public PdfNotebookEntry() {
		super();
	}
	
	public PdfNotebookEntry(PdfResource pdf) {
		super();
		this.pdf = pdf;
	}
}
