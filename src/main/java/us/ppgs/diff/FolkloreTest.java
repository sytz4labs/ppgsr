package us.ppgs.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FolkloreTest {

	public static void main(String[] args) {

		try {
			long start = System.currentTimeMillis();
			final byte[] ba1 = "sequence comparison".getBytes();
			final byte[] ba2 = "theory and practice".getBytes();

			Folklore fc = new Folklore(ba1.length, ba2.length) {
				@Override
				public boolean compare(int col, int row) {
					return ba1[col] == ba2[row];
				}

				@Override
				public void diff(int colFrom, int colLen, int rowFrom, int rowLen) {
					System.out.println("diff '" + new String(ba1, colFrom, colLen)
										+ "' '" + new String(ba2, rowFrom, rowLen) + "'");
				}

				@Override
				public void same(int colFrom, int rowFrom, int len) {
					System.out.println("same '" + new String(ba1, colFrom, len) + "'");
				}
			};
			fc.diff();

			long end = System.currentTimeMillis();
			System.out.println((end - start) + "ms");
			start = System.currentTimeMillis();
			
			final String[] s1 = getFile("c:/my/work/xdoc/ja8/projects/common/src/com/ntrs/xdoc/model/XDocModel.java");
			final String[] s2 = getFile("c:/my/work/xdoc/ja11-workspace/projects/common/src/com/ntrs/xdoc/model/XDocModel.java");
			
			fc = new Folklore(s1.length, s2.length) {
				@Override
				public boolean compare(int col, int row) {
					return s1[col].equals(s2[row]);
				}

				@Override
				public void diff(int colFrom, int colLen, int rowFrom, int rowLen) {

					StringBuilder colDiff = new StringBuilder();
					while (colLen > 0) {
						colDiff.append(s1[colFrom++]).append('\n');
						colLen--;
					}
					
					StringBuilder rowDiff = new StringBuilder();
					while (rowLen > 0) {
						rowDiff.append(s2[rowFrom++]).append('\n');
						rowLen--;
					}
					
					System.out.println("diff '" + colDiff.toString() 
							+ "' '" + rowDiff.toString() + "'");
				}

				@Override
				public void same(int colFrom, int rowFrom, int len) {
					StringBuilder diff = new StringBuilder();
					while (len > 0) {
						diff.append(s1[colFrom++]).append('\n');
						len--;
					}
					
					System.out.println("same '" + diff.toString() + "'");
				}
			};
			fc.diff();

			end = System.currentTimeMillis();
			System.out.println((end - start) + "ms");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getFile(String qs) throws IOException {
		
		ArrayList<String> strings = new ArrayList<String>(); 
		File f = new File(qs);
		BufferedReader br = new BufferedReader(new FileReader(f));

		String line;
		while ((line = br.readLine()) != null) {
			strings.add(line);
		}

		br.close();
		
		return strings.toArray(new String[0]);
	}
}
