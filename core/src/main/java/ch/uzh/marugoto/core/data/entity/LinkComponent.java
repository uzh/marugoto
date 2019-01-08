package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class LinkComponent extends Component {

	private boolean forcedDownload;
	private boolean universityAccess;
	private String url;
	@Ref
	private Resource resource;
	
	public boolean isForcedDownload() {
		return forcedDownload;
	}
	
	public void setForcedDownload(boolean forcedDownload) {
		this.forcedDownload = forcedDownload;
	}
	
	public boolean isUniversityAccess() {
		return universityAccess;
	}
	
	public void setUniversityAccess(boolean universityAccess) {
		this.universityAccess = universityAccess;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
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

	public LinkComponent(String url, boolean forcedDownload, boolean universityAccess) {
		super();
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
