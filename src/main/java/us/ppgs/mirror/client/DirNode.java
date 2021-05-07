package us.ppgs.mirror.client;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class DirNode extends Node {

	private Map<String, Node> children = new TreeMap<String, Node>();
	
	public DirNode(RemoteFileSystem rfs, DirNode parent, String name) {
		super(rfs, parent, name, true);
	}

	public void addChild(Node node) {
		children.put(node.name, node);
	}

	public Collection<Node> getChildren() {
		return children.values();
	}

	public Node getChild(String name) {

		return (Node) children.get(name);
	}

	public DirNode makeDirectory(String name) throws IOException {
		return rfs.makeDirectory(this, name);
	}

	public FileNode saveFile(File file) throws IOException {
		return rfs.saveFile(this, file);
	}
}
