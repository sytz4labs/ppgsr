package us.ppgs.mirror.client;

import java.io.IOException;

public abstract class Node {

	protected String name = null;
	protected DirNode parent = null;
	protected boolean directory = true;
	protected RemoteFileSystem rfs = null;
	protected boolean visited = false;

	public Node(RemoteFileSystem rfs, DirNode parent, String name, boolean directory) {
		this.rfs = rfs;
		this.parent = parent;
		this.name = name;
		this.directory = directory;
		
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public DirNode getParent() {
		return parent;
	}
	public void setParent(DirNode parent) {
		this.parent = parent;
	}

	public boolean isDirectory() {
		return directory;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited() {
		visited = true;
	}

	public void remove() throws IOException {
		rfs.remove(this);
	}

	public String getPath() {
		if (parent == null) {
			return null;
		}
		else {
			String parentPath = parent.getPath();
			if (parentPath == null) {
				return name;
			}
			else {
				return parentPath + "/" + name;
			}
		}
	}
}
