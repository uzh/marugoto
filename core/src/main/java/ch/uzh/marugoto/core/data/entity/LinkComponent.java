package ch.uzh.marugoto.core.data.entity;

import java.net.URL;

public class LinkComponent extends Component {

	private boolean forcedDownload;
	private boolean universityAccess;
	private URL url;
	private Resource resource;
	
	public boolean isForceDownload() {
		return forcedDownload;
	}
	
	public void setForceDownload(boolean forcedDownload) {
		this.forcedDownload = forcedDownload;
	}
	
	public boolean isUniversityAccess() {
		return universityAccess;
	}
	
	public void setUniversityAccess(boolean universityAccess) {
		this.universityAccess = universityAccess;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public LinkComponent() {
		super();
	}

	public LinkComponent(URL url, boolean forcedDownload, boolean universityAccess) {
		this.url = url;
		this.forcedDownload = forcedDownload;
		this.universityAccess = universityAccess;
	}

	public LinkComponent(boolean forcedDownload, boolean universityAccess, Resource resource) {
		super();
		this.forcedDownload = forcedDownload;
		this.universityAccess = universityAccess;
		this.resource = resource;
	}
		
}
