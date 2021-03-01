package us.ppgs.mirror.client;


public class FileNode extends Node {

	private long length;
	private long modified;

	public FileNode(RemoteFileSystem rfs, DirNode parent, String name, long length, long modified) {
		super(rfs, parent, name, false);
		this.length = length;
		this.modified = modified;
	}
	
	public long getLength() {
		return length;
	}
	public long getModified() {
		return modified;
	}
}
