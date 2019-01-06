package us.ppgs.linkfarm.info;

import java.util.ArrayList;

public class LFList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	public void rotateMinus(int ord) {
		T col = remove(ord);
		if (ord == 0) {
			add(col);
		}
		else {
			add(ord - 1, col);
		}
	}
	
	public void rotatePlus(int ord) {
		T col = remove(ord);
		if (ord == size()) {
			add(0, col);
		}
		else {
			add(ord + 1, col);
		}
	}
}
