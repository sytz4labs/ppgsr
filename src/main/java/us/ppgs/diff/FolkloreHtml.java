package us.ppgs.diff;

import java.io.IOException;
import java.io.PrintWriter;

public class FolkloreHtml extends Folklore {

	public static void doIt(PrintWriter out) throws IOException {

		String[] s1;
		String[] s2;

		s1 = FolkloreTest.getFile("c:/my/work/xdoc/ja8/projects/common/src/com/ntrs/xdoc/model/XDocModel.java");
		s2 = FolkloreTest.getFile("c:/my/work/xdoc/ja11-workspace/projects/common/src/com/ntrs/xdoc/model/XDocModel.java");

		FolkloreHtml fh = new FolkloreHtml(s1, s2);
		fh.diff();
		fh.dump(out);
	}

	public FolkloreHtml(String[] sa1, String[] sa2) {
		super(sa1.length, sa2.length);
		this.sa1 = sa1.clone();
		this.sa2 = sa2.clone();
	}
	
	private void dump(PrintWriter out) throws IOException {
		out.println("<div id=\"div1\">");
		out.println(div1);
		out.println("</div>");

		out.println("<div id=\"div2\">");
		out.println(div2);
		out.println("</div>");
	}

	private String[] sa1;
	private String[] sa2;
	private StringBuilder div1 = new StringBuilder();
	private StringBuilder div2 = new StringBuilder();
	
	@Override
	public boolean compare(int col, int row) {
		return sa1[col].equals(sa2[row]);
	}

	@Override
	public void diff(int colFromParm, int colLenParm, int rowFromParm, int rowLenParm) {

		int colFrom = colFromParm;
		int colLen = colLenParm;
		int rowFrom = rowFromParm;
		int rowLen = rowLenParm;
		
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		int fill1 = 0;
		int fill2 = 0;
		
		if (colLen != rowLen) {
			if (colLen > rowLen) {
				fill2 = colLen - rowLen;
			}
			else {
				fill1 = rowLen - colLen;
			}
		}
		
		while (colLen > 0) {
			sb1.append(sa1[colFrom++]);
			colLen--;
			if (colLen > 0) {
				sb1.append('\n');
			}
		}

		while (rowLen > 0) {
			sb2.append(sa2[rowFrom++]);
			rowLen--;
			if (rowLen > 0) {
				sb2.append('\n');
			}
		}

		final byte[] ba1 = sb1.toString().replaceAll("<", "&lt;").getBytes();
		final byte[] ba2 = sb2.toString().replaceAll("<", "&lt;").getBytes();
		
		sb1.setLength(0);
		sb2.setLength(0);
		
		Folklore f = new Folklore(ba1.length, ba2.length) {
			
			@Override
			public boolean compare(int col, int row) {
				return ba1[col] == ba2[row];
			}
			
			@Override
			public void same(int colFrom, int rowFrom, int len) {
				while (len > 0) {
					sb1.append((char) ba1[colFrom]);
					sb2.append((char) ba1[colFrom++]);
					len--;
				}
			}
			
			@Override
			public void diff(int colFrom, int colLen, int rowFrom, int rowLen) {

				sb1.append("<font color=red>");
				while (colLen > 0) {
					sb1.append((char) ba1[colFrom++]);
					colLen--;
				}
				sb1.append("</font>");

				sb2.append("<font color=red>");
				while (rowLen > 0) {
					sb2.append((char) ba2[rowFrom++]);
					rowLen--;
				}
				sb2.append("</font>");
				
			}
		};
		f.diff();
		
		div1.append("<pre class=\"bcDiff\">");
		div2.append("<pre class=\"bcDiff\">");

		div1.append(sb1.toString());
		div2.append(sb2.toString());

		div1.append("</pre>");
		div2.append("</pre>");
		
		if (fill1 > 0) {
			div1.append("<pre class=\"bcEmpty\">");
			while (fill1-- > 0) {
				div1.append("&nbsp;\n");
			}
			div1.append("</pre>");
		}
		
		if (fill2 > 0) {
			div2.append("<pre class=\"bcEmpty\">");
			while (fill2-- > 0) {
				div2.append("&nbsp;\n");
			}
			div2.append("</pre>");
		}
	}

	@Override
	public void same(int colFromParam, int rowFromParam, int lenParam) {

		int colFrom = colFromParam;
		int rowFrom = rowFromParam;
		int len = lenParam;
		
		div1.append("<pre>");
		div2.append("<pre>");

		while (len > 0) {
			div1.append(sa1[colFrom++]);
			div2.append(sa2[rowFrom++]);
			len--;
			if (len > 0) {
				div1.append('\n');
				div2.append('\n');
			}
		}
		
		div1.append("</pre>");
		div2.append("</pre>");
	}

	public String getS1() {
		return div1.toString();
	}

	public String getS2() {
		return div2.toString();
	}
}
