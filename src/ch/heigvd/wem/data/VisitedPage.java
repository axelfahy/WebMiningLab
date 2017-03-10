package ch.heigvd.wem.data;

public class VisitedPage {

	private Metadata metadata = null;
	private String content = null;
	
	public VisitedPage(Metadata metadata, String content) {
		this.metadata = metadata;
		this.content = content;
	}
	
	public Metadata getMetadata() { return metadata; }
	public String getContent() { return content; }

	public boolean isComplete() {
		if(this.metadata == null || this.content == null) return false;
		return this.metadata.isComplete();
	}
	
}
