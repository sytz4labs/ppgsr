package us.ppgs.mirror;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MirrorLocal extends Thread {

	private static String MSUBDIR = "_mirror/";
	public static void main(String args[]) {

		if (args.length != 2) {
			System.out.println("Usage: java Mirror <source_dir> <dest_dir>");
		} else {
			
			try {
				MirrorLocal mrr = new MirrorLocal(new File(args[0]), new File(args[1]));
				mrr.start();
				
				NumberFormat fmt = NumberFormat.getIntegerInstance();
				
				while(!mrr.done) {
					Thread.sleep(2000);
					System.out.println("Backed up " + fmt.format(mrr.statDirsProcessed) + " dirs, " +
										fmt.format(mrr.statFilesProcessed) + " files, " +
										fmt.format(mrr.statBytesBackedUp) + " bytes " +
										"of " + fmt.format(mrr.statBytesProcessed) +
										", Archived " + fmt.format(mrr.statBytesArchived) + " bytes " +
										mrr.formatDuration());
				}
			} catch (FileNotFoundException fnfe) {
				System.err.println("Error: File not found: " + args[2] + ": "
						+ fnfe);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private PrintWriter _errLog;
	private byte buffer[] = new byte[102400];
	
	private long statBytesBackedUp = 0;
	private long statBytesArchived = 0;
	private long statBytesProcessed = 0;
	private long statDirsProcessed = 0;
	private long statFilesProcessed = 0;
	private long statTime = 0;
	
	private boolean done = false;
	private File srcRootDir;
	private File mirrorRootDir;
	private File archiveRootDir;
	
	public void run() {
		backupDir(null);
		done = true;
	}
	
	public MirrorLocal(File aSrcRootDir, File aDestRootDir) throws FileNotFoundException {

		statTime = System.currentTimeMillis();
		
		String key = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());

		srcRootDir = aSrcRootDir;
		File destRootDir = aDestRootDir;
		if (!destRootDir.exists()) {
			destRootDir.mkdirs();
		}

		mirrorRootDir = new File(aDestRootDir, MSUBDIR);
		if (!mirrorRootDir.exists()) {
			mirrorRootDir.mkdirs();
		}

		archiveRootDir = new File(aDestRootDir, key);

		_errLog = new PrintWriter(new FileOutputStream(new File(destRootDir, key + ".log")));
	}

	private String formatDuration() {
		long diff = (System.currentTimeMillis() - statTime) / 1000;
		
		long hours = diff / 3600;
		long minutes = (diff % 3600) / 60;
		long seconds = diff % 60;
		
		StringBuffer sb = new StringBuffer();
		if (hours != 0) {
			sb.append(hours).append("h ");
		}
		if (hours != 0 || minutes != 0) {
			sb.append(minutes).append("m ");
		}
		sb.append(seconds).append("s");
		return sb.toString();
	}
	
	private void backupDir(String path) {

		File srcDir = path == null ? srcRootDir : new File(srcRootDir, path);
		File destDir = path == null ? mirrorRootDir : new File(mirrorRootDir, path);

		//System.out.println("Backup dir " + destDir.getAbsolutePath());
		// make sure the source directory exists
		if (srcDir.isDirectory()) {
			// make sure destination directory exists
			if (destDir.exists() && destDir.isFile() && path != null) {
				archiveFile(destDir, path);
			}

			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			statDirsProcessed ++;
			// make sure destination directory is actually a directory
			if (destDir.isDirectory()) {
				String lFileList[] = srcDir.list();
				// for each file in the source directory
				if (lFileList != null) {
					for (int i = 0; i < lFileList.length; i++) {
						File lCurSrc = new File(srcDir, lFileList[i]);
						if (!exclude(lFileList[i])) {
							// if its a directory
							String curPath = (path == null ? "" : path + "/") + lFileList[i];
							if (lCurSrc.isDirectory())
								backupDir(curPath);
							else
								backupFile(curPath);
						}
					}
				}
				lFileList = destDir.list();
				if (lFileList != null) {
					// for each file in the destination directory
					for (int i = 0; i < lFileList.length; i++) {
						File lCurSrc = new File(srcDir, lFileList[i]);
						File lCurDest = new File(destDir, lFileList[i]);
						// if the file does not exist in the source directory
						if (!lCurSrc.exists()) {
							String curPath = (path == null ? "" : path + "/") + lFileList[i];
							archiveFile(lCurDest, curPath);
						}
					}
				}
			} else
				_errLog.println("Error: " + destDir.getAbsolutePath() + " is not a directory.");
		} else {
			_errLog.println("Error: " + srcDir.getAbsolutePath() + " is not a directory.");
		}
	}

	private void backupFile(String path) {

		File srcFile = new File(srcRootDir, path);
		File destFile = new File(mirrorRootDir, path);

		try {
			statBytesProcessed += srcFile.length();
			statFilesProcessed++;
			if (!destFile.exists() || (srcFile.lastModified() != destFile.lastModified())) {
				//System.out.println("Backup " + path);
				if (destFile.exists()) {
					archiveFile(destFile, path);
				}
				destFile.createNewFile();
				FileInputStream lSrc = new FileInputStream(srcFile);
				FileOutputStream lDest = new FileOutputStream(destFile);
				int lLen = 0;
				while ((lLen = lSrc.read(buffer)) >= 0) {
					lDest.write(buffer, 0, lLen);
					statBytesBackedUp += lLen;
				}
				lSrc.close();
				lDest.close();
				destFile.setLastModified(srcFile.lastModified());
			}
		} catch (IOException ioe) {
			_errLog.println("Error: " + ioe);
		}
	}

	private void archiveFile(File destFile, String path) {

		//System.out.println("Archive " + destFile + " " + path);
// when dir is passed in dir length is zero.
		statBytesArchived += destFile.length();
		
		File archFile = new File(archiveRootDir, path);
		File parent = archFile.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			_errLog.println("mkdirs " + parent.getAbsolutePath() + " failed");
		}
		if (!destFile.renameTo(archFile)) {
			_errLog.println("renameTo " + archFile.getAbsolutePath() + " failed");
		}
	}
/*
	private void deleteDir(File aDestDir) {

		System.out.println("Delete dir " + aDestDir.getAbsolutePath());
		String lFileList[] = aDestDir.list();
		// for each file in the destination directory
		for (int i = 0; i < lFileList.length; i++) {
			File lCurDest = new File(aDestDir, lFileList[i]);
			// if its a directory
			if (lCurDest.isDirectory())
				deleteDir(lCurDest);
			else
				archiveFile(lCurDest);
		}
		aDestDir.delete();
	}
*/
	private boolean exclude(String aFileName) {

/*		if (aFileName.substring(
				aFileName.length() < 4 ? 0 : aFileName.length() - 4)
				.equalsIgnoreCase(".log"))
			return true;

		if (aFileName.equalsIgnoreCase("logs"))
			return true;
*/
		if (aFileName.equalsIgnoreCase("pagefile.sys"))
			return true;

		return false;
	}
}
