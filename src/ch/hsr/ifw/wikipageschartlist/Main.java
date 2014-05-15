package ch.hsr.ifw.wikipageschartlist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		String VERSION = "0.3.1";

		// default show number for Top Ranking Changes List and Top Visits List
		int SHOWMAX = 10;
		
		// refreshrate in minutes
		int REFRESH = 1440;
		
		// default URL
		URL url = null;
		try {
			url = new URL("http://gis.hsr.ch/wiki/Spezial:Popularpages");
//			url = new URL("http://gis.hsr.ch/wiki/Spezial:Meistverlinkte_Seiten");
//			url = new URL("http://de.wikipedia.org/wiki/Spezial:Meistverlinkte_Seiten");
		} catch (MalformedURLException e) {
			System.out.println("Ungültige URL" + url);
		}
		String url_string = url.toString();

		// Looking for arguments top / url / refresh/ old
		boolean args_old = false;
		for (int i = 0; i < args.length; ++i) {
			if (args[i].toLowerCase().endsWith("-top")) {
				int j = i + 1;
				if (args[j].length() > 0) {
					SHOWMAX = Integer.parseInt(args[j]);
				}
			}
			if (args[i].toLowerCase().endsWith("-url")) {
				int j = i + 1;
				if (args[j].length() > 4) {
					url_string = args[j];
				}
			}
			if (args[i].toLowerCase().endsWith("-refresh")) {
				int j = i + 1;
				if (args[j].length() > 0) {
					REFRESH = Integer.parseInt(args[j]);
				}
			}
			if (args[i].toLowerCase().endsWith("-old")) {
				args_old = true;
			}
			if (args[i].toLowerCase().endsWith("-help") || args[i].toLowerCase().endsWith("-?")) {
				System.out.println("\n" + "  WikiPagesChartlist Version " + VERSION + ", LGPL by IFS HSR, www.ifs.hsr.ch" + "\n\n" + 
				"  WikiPagesChartlist.jar [-url domain] [-top 10] [-refresh 1440] [-old] [-help]" + "\n\n" +
				"  * [-url yourwiki] URL zu einer passenden Wiki-Seite" + "\n" +
				"    (default http://gis.hsr.ch/wiki/Spezial:Popularpages)" + "\n" +
				"  * [-top 10] (default) gibt die Rangliste mit den ersten 10 Plätzen aus." + "\n" +
				"  * [-refresh 1440] (default) Setzt die Refresh-Rate auf 1440 Minuten (= 24h)." + "\n" +
				"  * [-old] vergleicht die Rangliste mit einer bis zu sieben Tag älteren Liste." + "\n" +
				"  * [-help] zeigt diese Hilfe." + "\n\n");
				System.exit(0);
			}
		}
		
		if (SHOWMAX < 0) {
			System.out.println("> Fix your stupidness, replaced -top with defaultvalue (10)...\n");
			SHOWMAX = 10;
		}
		if (REFRESH < 0) {
			System.out.println("> Let's Timetravel .. %$)%!´+§ .. possible loss of precision, replaced -refresh with Defaultvalue (24h)...\n");
			REFRESH = 1440;
		}
		
		// args -prev true/false
		String prev_file;
		if (args_old) {
			prev_file = "_prev_old.csv";
		} else {
			prev_file = "_prev.csv";
		}
		
		// Get Hostname and replace special characters with "_"
		String host = (url.getHost() + url.getFile()).toLowerCase();
		String host_char_repl = HTML_Parser.replace_char(host, "\\W", "_");
		
		// Parse HTML Sourcecode to determine number of entries, max. 1000
		String html = HTML_Parser.parse_html(url_string + "&limit=1000", url_string);
		int html_length = html.split("<li>").length;
		if (SHOWMAX >= html_length) {
			SHOWMAX = html_length - 1;
		}
		
		// Split and Write "Article;View" to File
		String tmp_file = HTML_Parser.split_write(html, host_char_repl);
		
		// Compare Files
		File_Compare.compare_files(host_char_repl, tmp_file, REFRESH);
		
		// Read Article List
		Map<String, Article> articlesCurr = new LinkedHashMap<String, Article>();
		articlesCurr = WikiPagesChartlist.readCSV(host_char_repl + "_curr.csv");
		Map<String, Article> articlesPrev = new LinkedHashMap<String, Article>();
		articlesPrev = WikiPagesChartlist.readCSV(host_char_repl + prev_file);
		
		// Read lastmod from *_curr file and caculate date difference
		String prev_last_mod = File_Compare.file_last_mod(host_char_repl + prev_file);
		String curr_last_mod = File_Compare.file_last_mod(host_char_repl + "_curr.csv");
		int date_diff = File_Compare.date_diff(host_char_repl + prev_file, host_char_repl + "_curr.csv");
		
		// Some println to show off :)
		System.out.println("\n" + "WikiPagesChartlist Version " + VERSION + ", LGPL by IFS HSR, www.ifs.hsr.ch");
		System.out.println("Anfrage vom " + curr_last_mod + " verglichen mit " + prev_last_mod);
		System.out.println("Zeitdifferenz " + calc_time(date_diff) + ", Refresh-Intervall " + calc_time(REFRESH));

		// Show Top Ranking Changes List and Top Visits List
		WikiPagesChartlist.toplist(SHOWMAX, articlesCurr, articlesPrev);
		WikiPagesChartlist.topview(SHOWMAX, articlesCurr, articlesPrev);
		
		// update files
		File_Compare.update_files(host_char_repl, tmp_file, REFRESH);
	}

	private static String calc_time (int date_diff) {
		int date_diff_hours = date_diff / 60;
		int date_diff_minutes = date_diff - (date_diff_hours*60);
		String date_diff_str = String.valueOf(date_diff_hours) + "h " + String.valueOf(date_diff_minutes) + "min";
		return date_diff_str;
	}
}
