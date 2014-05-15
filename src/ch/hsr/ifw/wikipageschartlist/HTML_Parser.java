package ch.hsr.ifw.wikipageschartlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTML_Parser {

	public static String parse_html (String address, String error_msg) {
		
		final String PAGE = address;
		
		URL url = null;
		BufferedReader br = null;

		// Check URL
		try {
			url = new URL(PAGE);
		} catch (MalformedURLException e) {
			errorExit("ungültige URL: " + error_msg);
		}
		
		// Check URL connection
		try {
			URLConnection urlc = url.openConnection();
			br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
		} catch (IOException e) {
			errorExit("Verbindung zu " + error_msg + " konnte nicht hergestellt werden.");
		}

		String str;
		StringBuffer tmp = new StringBuffer();

		// Read Source line by line
		try {
			while ((str = br.readLine()) != null) {
				tmp.append(str);
			}
		} catch (IOException e) {
			errorExit("IO-Fehler beim Lesen der Seite.");
		}

		String html = tmp.toString();
		return html;
	}
	
	public static String split_write(String html, String host) {
		String tmp_file = host + "_temp.csv";
		
		// Split source and write to file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp_file));
			for (int j = 1; j < (html.split("<li>").length); j++) {
				String[] parts = html.split("<li>");
//				parts = parts[j].split("</li>");
				String li = parts[j];

				String[] parts_article = li.split("title=\"");
				parts_article = parts_article[1].split("\">");
				String article = parts_article[0];
				
				// Convert HTML character entities to Java String
				article = NCRDecoder.decode(article);
				
				String[] parts_view_tmp = li.split("</a>");
				parts_view_tmp = parts_view_tmp[1].split("</li>");
				String view_tmp = parts_view_tmp[0];
				
				// decode URL, replace "_" with " ", remove article
				view_tmp = URLDecoder.decode(view_tmp, "UTF-8");
				view_tmp = replace_char(view_tmp, "_", " ");
				view_tmp = replace_char(view_tmp, article, "");
				
				// Remove special Characters
				String view = replace_char(view_tmp, "\\D", "");

				bw.write(article + ";" + view);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			errorExit("Konnte Datei nicht erstellen");
		}
		return tmp_file;
	}
	
	public static String replace_char(String str_to_replace, String replace_char, String char_to_repl) {
		Pattern pattern = Pattern.compile(replace_char);
		Matcher matcher = pattern.matcher(str_to_replace);
		String replaced_string = matcher.replaceAll(char_to_repl);
		return replaced_string;
	}

	private static void errorExit(String errMsg) {
		System.err.println(errMsg);
		System.exit(1);
	}
}
