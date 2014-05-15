package ch.hsr.ifw.wikipageschartlist;

public class Article {
	
	private int rank;
	private String name;
	private int views;
	private int diff_views;

	public Article(int rank, String name, int views) {
		this.rank = rank;
		this.name = name;
		this.views = views;
	}
	
	public Article(int rank, String name, int diff_views, int views){
		this.rank = rank;
		this.name = name;
		this.diff_views = diff_views;
		this.views = views;
	}
	
	public int getRank() {
		return rank;
	}
	
	public String getName() {
		return name;
	}
	
	public int getViews() {
		return views;
	}
	
	public int getDiffViews() {
		return diff_views;
	}
	
	public boolean equals(Article a) {
		return this.name.equals(a.name);
	}

	public int compareTo(Article a){
		return this.name.compareTo(a.name);
	}
}
