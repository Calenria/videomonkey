/**
 * 
 */
package de.videomonkey.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Azrail
 * 
 */
public class FileUtils {

	public void copyDir(File quelle, File ziel) throws FileNotFoundException,
			IOException {

		File[] files = quelle.listFiles();
		File newFile = null;
		ziel.mkdirs();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				newFile = new File(ziel.getAbsolutePath()
						+ System.getProperty("file.separator")
						+ files[i].getName());
				if (files[i].isDirectory()) {
					copyDir(files[i], newFile);
				} else {
					copyFile(files[i], newFile);
				}
			}
		}
	}

	public void copyFile(File file, File ziel) throws FileNotFoundException,IOException {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(ziel, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
	}

	public void deleteDir(File dir) {

		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDir(files[i]);
				} else {
					files[i].delete();
				}
			}
			dir.delete();
		}
	}

	public static ArrayList<File> searchFile(File dir, ArrayList<String> finds) {

		File[] files = dir.listFiles();
		ArrayList<File> matches = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {

				for (String find : finds) {
					if (files[i].getName().endsWith(find)) {
						matches.add(files[i]);
					}
				}

				if (files[i].isDirectory()) {
					matches.addAll(searchFile(files[i], finds));
				}
			}
		}
		return matches;
	}

	public static void listDir(File dir) {

		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				System.out.print(files[i].getAbsolutePath());
				if (files[i].isDirectory()) {
					System.out.print(" (Ordner)\n");
					listDir(files[i]);
				} else {
					System.out.print(" (Datei)\n");
				}
			}
		}
	}

	public long getDirSize(File dir) {

		long size = 0;
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					size += getDirSize(files[i]);
				} else {
					size += files[i].length();
				}
			}
		}
		return size;
	}

}
