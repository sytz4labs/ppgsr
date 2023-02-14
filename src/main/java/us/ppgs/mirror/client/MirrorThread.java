package us.ppgs.mirror.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import us.ppgs.mirror.info.MirrorClientInfo;
import us.ppgs.mirror.info.MirrorClientInfo.MirrorDirInfo;

@Component
public class MirrorThread {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm_ss");
	
	@Async
	public Future<String> run() {

		CompletableFuture<String> cf = new CompletableFuture<>();
		
		try {
			MirrorClientInfo mci = MirrorClientInfo.getConfig();
	    	File logDir = new File(mci.getLogDir());
	    	String logTime = sdf.format(new Date());

			for (MirrorDirInfo dir : mci.getDirs()) {
				PrintStream ps = new PrintStream(new File(logDir, logTime + "_" + dir.getName() + ".log"));

				try {
					backup(ps, mci.getServerUrl(), dir);
				}
				finally {
					ps.close();
				}
			}
			cf.complete("OK");
		}
		catch (Exception e) {
			cf.complete(e.getMessage());
			e.printStackTrace();
		}

		return cf;
	}

	private static void backup(PrintStream ps, String serverUrl, MirrorDirInfo dir) throws IOException {

		// get server representation of directory
		RemoteFileSystem rfs = RemoteFileSystem.getRemoteFileSystem(serverUrl, dir.getName());
		// Backup current dir
		backupDir(new File(dir.getDirectory()), rfs.getRoot());
		// send deletes for unused files and dirs.
		deleteNonVisited(rfs.getRoot());
	}

	private static void deleteNonVisited(DirNode rDir) throws IOException {

		for (Node rNode : rDir.getChildren()) {
			if (!rNode.isVisited()) {
				rNode.remove();
			}
			else {
				if (rNode.isDirectory()) {
					deleteNonVisited((DirNode) rNode);
				}
			}
		}
	}

	private static void backupDir(File dir, DirNode rDir) throws IOException {
		
		// for each file in dir
		String[] files = dir.list();
		if (files != null) {
			for (int i=0; i<files.length; i++) {
				File lFile = new File(dir, files[i]);
				Node rNode = rDir.getChild(files[i]);
				if (rNode != null) {
					//exits remotely
					if (lFile.isFile()) {
						// local is file
						// if remote is dir
						if (rNode.isDirectory()) {
							// remove remote dir
							rNode.remove();
							// save file
							FileNode newFile = rDir.saveFile(lFile);
							// mark visited
							newFile.setVisited();
						}
						else {
							FileNode rFile = (FileNode) rNode;
							// if date or length not same
							if (lFile.length() != rFile.getLength() || lFile.lastModified() != rFile.getModified()) {
								// save file
								rFile = rDir.saveFile(lFile);
							}
							// mark visited
							rFile.setVisited();
						}
					}
					else {
						// local must be dir
						DirNode newDir = null;
						if (rNode.isDirectory()) {
							newDir = (DirNode) rNode;
						}
						else {
							// remote is file
							// remove file
							rNode.remove();
							// make remote dir
							newDir = rDir.makeDirectory(files[i]);
						}
						// mark visited.
						newDir.setVisited();
						// backup dir
						backupDir(lFile, newDir);
					}
				}
				else { // doesn't exist remotely
					if (lFile.isDirectory()) {
						// create dir
						DirNode newDir = rDir.makeDirectory(files[i]);
						// mark visited
						newDir.setVisited();
						// backup dir
						backupDir(lFile, newDir);
					}
					else {
						// save file
						FileNode newFile = rDir.saveFile(lFile);
						// mark visited
						newFile.setVisited();
					}
				}
			}
		}
	}
}
