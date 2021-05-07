package us.ppgs.mirror.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public final class RemoteFileSystem {
	
	public static RemoteFileSystem getRemoteFileSystem(String url, String name) throws IOException {
		return new RemoteFileSystem(url, name);
	}
	
	private static long secret = 0x196a01161f093c78L;
	public static long getSecret() {
		return secret;
	}

	private String url;
	private String name;
	private DirNode root;
	
	private BufferedReader createPost(String... args) throws IOException {
		
		ByteArrayOutputStream postStream = new ByteArrayOutputStream(); 
		DataOutputStream dos = new DataOutputStream(postStream);
		dos.writeLong(secret);
		for (String arg : args) {
			dos.writeUTF(arg);
		}
		dos.close();

		HttpPost httppost = new HttpPost(url);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		httppost.setEntity(new ByteArrayEntity(postStream.toByteArray()));
		
		CloseableHttpResponse saveResponse = httpclient.execute(httppost);

		return new BufferedReader(new InputStreamReader(saveResponse.getEntity().getContent()));
	}
	
	private RemoteFileSystem(String url, String name) throws IOException {
		this.url = url;
		this.name = name;
		this.root = new DirNode(this, null, null);
		
		DirNode curDir = root;
		BufferedReader br = null;

		try {
			br = createPost("dir", name);
			String line;
			while ((line = br.readLine()) != null) {
				char c = line.charAt(0);
				if (c == 'D') {
					curDir = new DirNode(this, curDir, line.substring(1));
				}
				else if (c == 'E') {
					curDir = curDir.getParent();
				}
				else if (c == 'F') {
					String[] fileInfo = line.substring(1).split("\\|");
					new FileNode(this, curDir, fileInfo[0], Long.parseLong(fileInfo[1]), Long.parseLong(fileInfo[2]));
				}
			}
		}
		finally {
			if (br != null) {
				br.close();
			}
		}
	}
	
	public DirNode getRoot() {
		return root;
	}
	
	public void remove(Node node) throws IOException {

		BufferedReader br = null;

		try {
			br = createPost("delete", name, node.getPath());
			String response = br.readLine();
			if (!"OK".equals(response)) {
				throw new IOException(response);
			}
		}
		finally {
			if (br != null) {
				br.close();
			}
		}
	}

	public DirNode makeDirectory(DirNode node, String newDirName) throws IOException {
		
		DirNode newDir = new DirNode(this, node, newDirName);
		BufferedReader br = null;

		try {
			br = createPost("mkdir", name, newDir.getPath());
			String response = br.readLine();
			if (!"OK".equals(response)) {
				throw new IOException(response);
			}
		}
		finally {
			if (br != null) {
				br.close();
			}
		}
		
		return newDir;
	}

	public FileNode saveFile(DirNode node, File file) throws IOException {

		FileNode newFile = new FileNode(this, node, file.getName(), file.length(), file.lastModified());
		InputStream is = null;
		FileInputStream fis = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeLong(secret);
			dos.writeUTF("save");
			dos.writeUTF(name);
			dos.writeUTF(newFile.getPath());
			dos.writeLong(file.length());
			dos.writeLong(file.lastModified());

			HttpPost httppost = new HttpPost(url);
			final byte[] saveHeader = baos.toByteArray();
			fis = new FileInputStream(file);
			final FileInputStream ffis = fis;
			InputStream instream = new InputStream() {
				int savePos = 0;
				int saveLen = saveHeader.length;
				
				@Override
				public int read(byte[] b, int off, int len) throws IOException {
					if (savePos < saveLen) {
						int i = 0;
						while (savePos < saveLen && i<len) {
							b[off + i++] = saveHeader[savePos++];
						}
						return i;
					}

					return ffis.read(b, off, len);
				}

				@Override
				public int read() throws IOException {
					if (savePos < saveLen) {
						// send header
						return saveHeader[savePos++];
					}

					return ffis.read();
				}
			};
			httppost.setEntity(new InputStreamEntity(instream));
			CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			CloseableHttpResponse saveResponse = httpclient.execute(httppost);
			is = saveResponse.getEntity().getContent();

			// get response
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String response = br.readLine();
			if (!"OK".equals(response)) {
				throw new IOException(response);
			}
		}
		finally {
			if (is != null) {
				is.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		
		return newFile;
	}
}
