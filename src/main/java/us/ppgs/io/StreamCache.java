package us.ppgs.io;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StreamCache {

	private static int bufferLength = 1024;
	private boolean full = false;	// once the cache has been filled, data can only
									// be extracted as many times as you want.

	// cache buffer
	private List<byte[]> buffers = null;
	private byte[] lastBuffer = null;
	private int pos = 0; // position of the next character to be written in the last buffer

	private void newBuffer() {
		lastBuffer = new byte[bufferLength];
		buffers.add(lastBuffer);
		pos = 0;
	}

	public StreamCache() {
		// create new list of buffers
		buffers = new LinkedList<byte[]>();
		newBuffer();
	}

	// there are two ways to fill this cache.
	//	1. read from an input stream
	//  2. write through an output stream
	public void fillFromInputStream(InputStream is) throws Exception {
		// read lines into a linked list of buffers
		if (full) {
			throw new Exception("StreamCache has already been filled");
		}
		int len = 0;
		do {
			len = is.read(lastBuffer, pos, bufferLength - pos);
			if (len >= 0) {
				pos += len;
				if (pos == bufferLength) {
					newBuffer();
				}
			}
		} while (len > 0);

		is.close();
		full = true;
	}

	public void close() {
		full = true;
	}

	public OutputStream getFillOutputStream() throws IOException {
		if (full) {
			throw new IOException("StreamCache has already been filled");
		}
		final StreamCache dest = this;
		return new OutputStream() {
		    public void write(int b) throws IOException {
				dest.lastBuffer[dest.pos++] = (byte) b;
				if (dest.pos == bufferLength) {
					dest.newBuffer();
				}
			}

		    public void close() throws IOException {
				dest.full = true;
			}
		};
	}

	// there are three ways to get the contents of this cache.
	//	1. read through an input stream
	//  2. write to output stream
	//  3. get an array of bytes
	public void writeToOutputStream(OutputStream os) throws Exception {
		// for each buffer in the linked list write to os
		if (!full) {
			throw new Exception("StreamCache has not been filled");
		}

		for (byte[] buffer : buffers) {
			os.write(buffer, 0, buffer == lastBuffer ? pos : buffer.length);
		}
	}

	public InputStream getInputStream() throws Exception {

		if (!full) {
			throw new Exception("StreamCache has not been filled");
		}
		final StreamCache source = this;

		return new InputStream() {

			private Iterator<byte[]> iter;
			private byte[] curBuffer; // current buffer being read
			private int curPos; // next character to be read
			private boolean initialized = false;

		    public int read() throws IOException {
				if (!initialized) {
					iter = buffers.iterator();
					curBuffer = iter.next();
					curPos = 0;
					initialized = true;
				}

				// check for end of file
				if (   curBuffer == source.lastBuffer
					&& curPos == source.pos) {
					return -1;
				}

				// return next character
				int ret = curBuffer[curPos++];

				// check if next pos is eob
				if (curPos == bufferLength && curBuffer != source.lastBuffer) {
					curBuffer = iter.next();
					curPos = 0;
				}

				return ret;
			}
		};
	}

	public int size() {
		return (buffers.size() - 1) * bufferLength + pos;
	}
	
	public byte[] getBytes() throws IOException  {
		// for each buffer in the linked list write to os
		if (!full) {
			throw new IOException("StreamCache has not been filled");
		}

		byte[] retBuf = new byte[size()];
		int retBufPos = 0;

		for (byte[] aBuffer : buffers) {
			System.arraycopy(aBuffer,
							 0,
							 retBuf,
							 retBufPos,
							 aBuffer == lastBuffer ? pos : bufferLength);
			retBufPos += bufferLength;
		}

		return retBuf;
	}

}
