package ch.hsr.ifw.wikipageschartlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class File_Compare {

	private static boolean update = false;

	public static void compare_files(String host, String tmp_file, int refresh) {
		
		String prev = host + "_prev.csv";
		String curr = host + "_curr.csv";
		String prev_bak = host + "_prev_old.csv";
		
		// create file
		File past_file = new File(prev);
		File curr_file = new File(curr);
		File prev_bak_file = new File(prev_bak);
		try {
			if (past_file.createNewFile()) {
				System.out.println(past_file + " nicht vorhanden, wird erstellt...\n");
				copyfile(tmp_file, prev);
			}
			if (curr_file.createNewFile()) {
				System.out.println(curr_file + " nicht vorhanden, wird erstellt...\n");
			}
			if (prev_bak_file.createNewFile()) {
				System.out.println(prev_bak_file + " nicht vorhanden, wird erstellt...\n");
				copyfile(tmp_file, prev_bak);
			}
		} catch (IOException e) {
			System.out.println("Datei nicht erstellbar");
		}
		
		// Comparison in Hours, overwrite if needed
		int tmpcurr_diff = date_diff(curr, tmp_file);
		if (tmpcurr_diff <= refresh) {
			if (!update) {
				copyfile(tmp_file, curr);
			}
			int currprev_diff = date_diff(prev, curr);
			if (currprev_diff > refresh) {
				copyfile(curr, prev);
			}
		} else {
			copyfile(curr, prev);
			String last_mod = file_last_mod(curr);
			SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy HH:mm");
			Date date = null;
			try {
				date = sdf.parse(last_mod);
			} catch (ParseException e) {
				System.out.println(e);
			}
			// change lastmod of *_past.cvs to *_curr.cvs
			// =>	show always a chart at startup even
			//		24h/ 48h been past.
			past_file.setLastModified(date.getTime());
			copyfile(tmp_file, curr);
		}
		// check if prev_old.csv is older than seven days
		int prevprevbak_diff = date_diff(prev_bak, prev);
		if (prevprevbak_diff > (7*refresh)) {
			copyfile(prev, prev_bak);
		}
		File delete_tmp_file = new File(tmp_file);
		delete_tmp_file.deleteOnExit();
	}
 
	
	public static void update_files(String host, String tmp_file, int refresh) {
		update = true;
		compare_files(host, tmp_file, refresh);
		update = false;
	}

	
	public static String file_last_mod(String file) {
		File file_date = new File(file);
		Date file_lastmod = new Date (file_date.lastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy HH:mm");
		String format_file_lastmod = sdf.format(file_lastmod);
		return format_file_lastmod;
	}

	public static int date_diff(String past_file_tmp, String curr_file_tmp) {
		// create file
		File past_file = new File(past_file_tmp);
		File curr_file = new File(curr_file_tmp);
		Date past_date = new Date(past_file.lastModified());
		Date curr_date = new Date(curr_file.lastModified());
		Calendar cal_past_date = Calendar.getInstance();
		Calendar cal_curr_date = Calendar.getInstance();
		cal_past_date.setTime(past_date);
		cal_curr_date.setTime(curr_date);

		// difference in Hours
		int diff = 0;
		if (cal_past_date.before(cal_curr_date)) {
			diff = countDiff(cal_past_date, cal_curr_date);
		} else {
			diff = countDiff(cal_curr_date, cal_past_date);
		}
		return diff;
	}

	private static int countDiff(Calendar cal_past_date, Calendar cal_curr_date) {
		int returnInt = 0;
		while (!cal_past_date.after(cal_curr_date)) {
			cal_past_date.add(Calendar.MINUTE, 1);
			returnInt++;
		}
		if (returnInt > 0) {
			returnInt = returnInt - 1;
		}
		return (returnInt);
	}

	private static void copyfile(String fromfile, String tofile) {
		File inputFile = new File(fromfile);
		File outputFile = new File(tofile);
		try {
			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int readin;
			while ((readin = in.read()) != -1) {
				out.write(readin);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Datei nicht gefunden");
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen der Datei");
		}
	}
}
