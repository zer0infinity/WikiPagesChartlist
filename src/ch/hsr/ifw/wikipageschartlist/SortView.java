package ch.hsr.ifw.wikipageschartlist;


import java.util.Comparator;

public class SortView implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		int views1 = ((Article) o1).getDiffViews();
		int views2 = ((Article) o2).getDiffViews();
		
		if (views1 < views2) {
			return 1;
		} else if (views1 == views2) {
			return 0;
		} else {
			return -1;
		}
	}
}
