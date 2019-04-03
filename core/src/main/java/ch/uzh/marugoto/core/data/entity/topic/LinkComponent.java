package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Ref;

public class LinkComponent extends Component {

	private ImageResource icon;
	private String linkText;
	@Ref
	private Resource resource;
	
	public LinkComponent() {
		super();
	}

	public LinkComponent(Resource resource, String linkText) {
		this();
		this.resource = resource;
		this.linkText = linkText;
	}
	
	public ImageResource getIcon() {
		return icon;
	}

	public void setIcon(ImageResource icon) {
		this.icon = icon;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}	
}
