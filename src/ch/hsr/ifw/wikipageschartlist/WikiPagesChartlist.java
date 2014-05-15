package ch.hsr.ifw.wikipageschartlist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WikiPagesChartlist {

	public static void toplist(int SHOWMAX, Map<String, Article> articlesCurr, Map<String, Article> articlesPrev) {
		System.out.println("\nTop Ranking Changes Chart:");
		
		// Chart of articles
		int counter = 0;
		for(Iterator<String> iterator = articlesCurr.keySet().iterator(); iterator.hasNext();) {
			Object article = iterator.next();
			int prev_rank = 0;
			int curr_rank = articlesCurr.get(article).getRank();
			int curr_view = articlesCurr.get(article).getViews();
			if (articlesPrev.containsKey(article) == true) {
				prev_rank = articlesPrev.get(article).getRank();
			} else {
				prev_rank = -1;
			}
			counter = calculateTrend(SHOWMAX, curr_rank, prev_rank, article, curr_view, curr_rank, counter);
		}
		if (counter == 0) {
			System.out.println("- nothing has changed yet");
		}
		// Chart of outdated articles
		int j = 0;
		for(Iterator<String> iterator = articlesPrev.keySet().iterator(); iterator.hasNext(); ) {
			Object article = iterator.next();
			if (articlesCurr.containsKey(article) == false) {
				int prev_rank = articlesPrev.get(article).getRank() + 1;
				if (j < SHOWMAX) {
					System.out.println("Out(" + prev_rank + ") " + article);
				}
			}
			j++;
		}
	}
	
	public static void topview (int SHOWMAX, Map<String, Article> articlesCurr, Map<String, Article> articlesPrev) {
		List<Article> view_list = new ArrayList<Article>();
		
		// Chart with most views
		for(Iterator<String> iterator = articlesCurr.keySet().iterator(); iterator.hasNext(); ) {
			Object article = iterator.next();
			int prev_view = 0;
			int curr_rank = articlesCurr.get(article).getRank();
			int curr_view = articlesCurr.get(article).getViews();
			String curr_article = articlesCurr.get(article).getName();
			if (articlesPrev.containsKey(article) == true) {
				prev_view = articlesPrev.get(article).getViews();
			}
			int diff_views = curr_view - prev_view;
			view_list.add(new Article(curr_rank + 1, curr_article, diff_views, curr_view));
		}
		Collections.sort(view_list, new SortView());
		System.out.println("\nTop Visits Chart:");
		for (int k = 0; k < SHOWMAX; k++) {
			System.out.printf("%3d" + "  %3d. " + view_list.get(k).getName() + " (" + view_list.get(k).getViews()
					+ ")\n", view_list.get(k).getDiffViews(), view_list.get(k).getRank());
		}
	}

	public static Map<String, Article> readCSV(String filename) {
		Map<String, Article> articles = new LinkedHashMap<String, Article>();
        BufferedReader f;
        String line_in;
        try {
            f = new BufferedReader(new FileReader(filename));
            int rank = 0;
            while ((line_in= f.readLine()) != null) {
            	StringTokenizer string_token= new StringTokenizer(line_in, ";");
                String name = string_token.nextToken();
                int views = Integer.parseInt(string_token.nextToken());
                articles.put(name, new Article(rank, name, views));
                rank++;
            }
            f.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei " + filename);
        }
        return articles;
    }
	
    private static int calculateTrend(int SHOWMAX, int curr, int prev, Object article, int curr_view, int curr_rank, int counter) {
        String trend= "   ";
        int diff= prev - curr;
        boolean use_trend = false;
        if ((prev == -1)) {
            trend= "++  ";
            use_trend = true;
        }
        else if (diff > 0) {
            trend= " +" + diff;
            use_trend = true;
        }
        else if (diff < 0) {
            trend= " " + diff;
            use_trend = true;
        }
        if (use_trend == true && counter < SHOWMAX) {
        	if (diff < 0) {
        		System.out.printf("%2s" + "  %3d." + "    " + article + " (" + curr_view + ")\n", trend, curr_rank + 1);
        	} else {
        		System.out.printf("%2s" + "  %3d." + " " + article + " (" + curr_view + ")\n", trend, curr_rank + 1);
        	}
        	counter++;
        }
        return counter;
    }
}
