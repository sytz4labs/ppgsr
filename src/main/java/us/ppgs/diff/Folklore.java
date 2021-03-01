package us.ppgs.diff;

import java.util.ArrayList;
import java.util.Stack;

public abstract class Folklore {

	private class Pair {
		public Pair(int col, int row) {
			this.col = col;
			this.row = row;
		}
		
		int col;
		int row;
	}

	private int cols;
	private int rows;
	
	protected Folklore(int cols, int rows) {

		this.cols = cols;
		this.rows = rows;
	}
	
	public abstract boolean compare(int col, int row);
	public abstract void diff(int colFrom, int colLen, int rowFrom, int rowLen);
	public abstract void same(int colFrom, int rowFrom, int len);

	public void diff() {
		ArrayList<ArrayList<Pair>> matches = new ArrayList<ArrayList<Pair>>();

		int[] lastRow = new int[cols + 1];
		int[] curRow = new int[cols + 1];
		ArrayList<Pair> zeroPair = new ArrayList<Pair>();
		zeroPair.add(new Pair(0,0));
		matches.add(zeroPair);
		
		for (int row=1; row<=rows; row++) {
			for (int col=1; col<=cols; col++) {
				if (compare(col-1, row-1)) {
					int pos = lastRow[col-1] + 1;
					if (   pos != lastRow[col]
						&& pos != curRow[col-1]) {
						if (matches.size() > pos) {
							matches.get(pos).add(new Pair(col, row));
						}
						else {
							ArrayList<Pair> newPairList = new ArrayList<Pair>();
							newPairList.add(new Pair(col, row));
							matches.add(newPairList);
						}
					}
					curRow[col] = pos;
				}
				else {
					if (curRow[col-1] > lastRow[col]) {
						curRow[col] = curRow[col-1];
					}
					else {
						curRow[col] = lastRow[col];
					}
				}
			}
			
			int[] tmpRow = curRow;
			curRow = lastRow;
			lastRow = tmpRow;
		}
		
		Stack<Pair> prstk = new Stack<Pair>();

		Pair pr = null;
		while (matches.size() > 1) {
			ArrayList<Pair> lastPairs = matches.remove(matches.size() - 1);
			if (pr == null) {
				pr = lastPairs.get(0);
			}
			else {
				Pair np; 
				do {
					np = lastPairs.remove(0);
				} while (np.row >= pr.row || np.col >= pr.col);
				pr = np;
			}
			prstk.push(new Pair(pr.col-1, pr.row-1));
		}
		
		Pair ppr = new Pair(0,0);
		int sameLen = 0;
		while (prstk.size() > 0) {
			pr = prstk.pop();
			if ((pr.col-ppr.col) != 0 || (pr.row-ppr.row) != 0) {
				// difference gap from previous so print accumulated same
				if (sameLen != 0) {
					same(ppr.col-sameLen, ppr.row-sameLen, sameLen);
					sameLen = 0;
				}
				diff(ppr.col, pr.col-ppr.col, ppr.row, pr.row-ppr.row);
			}
			sameLen++;
//			same(pr.col, pr.row, 1);
			// advance to next match
			ppr = new Pair(pr.col + 1, pr.row + 1);
		}
		if (sameLen != 0) {
			same(ppr.col-sameLen, ppr.row-sameLen, sameLen);
		}
		diff(ppr.col, cols-ppr.col, ppr.row, rows-ppr.row);
	}
}
