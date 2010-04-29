/**
 * 
 */
package de.videomonkey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.PropertyConfigurator;

import de.videomonkey.utils.FileUtils;
import de.videomonkey.utils.ThreadInfos;

/**
 * @author Azrail
 * 
 */
public class Search extends AbstractTableModel {

	public static HashMap<String, Movie> movie = new HashMap<String, Movie>();

	// private static Logger log = Logger.getRootLogger();

	ArrayList<File> fileList = FileUtils.searchFile(new File("C:\\TASTEMP"), Movie.get_wantedExtensions());
	
	private int rowCount = 0;
	
	
	public Search() throws InterruptedException {
		PropertyConfigurator.configureAndWatch("log.properties", 60 * 1000);
		//ArrayList<File> fileList = FileUtils.searchFile(new File("C:\\TASTEMP"), Movie.get_wantedExtensions());

		rowCount = fileList.size();
		
		for (File file : fileList) {
			ThreadInfos thread = new ThreadInfos(file.getAbsolutePath(),new Movie(file));
			thread.start();
		}

		JTable table = new JTable();

		table.setModel(this);

		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		while (Thread.activeCount() > 2) {
			System.out.println(Thread.activeCount());
			System.out.println(movie.toString());
			System.out.println(fileList.get(0).getAbsolutePath());
			System.out.println(movie.size());
			
			for (int i = 0; i < movie.size(); i++) {
				String imdb = ((Movie) movie.get(fileList.get(i).getAbsolutePath())).getMovieIMDBId();
				System.out.println(imdb);
				System.out.println("table.getValueAt(0, 1): " + table.getValueAt(0, 1));
				table.setValueAt( imdb, i, 1);
				this.fireTableDataChanged();
			}
			
			
			//setValueAt( movie.get(fileList.get(0).getAbsolutePath()).getMovieIMDBId(), 0, 3);
			Thread.sleep(200);
		}
		
		
	}
	
	
	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int row, int col) {
		if (col == 0)
			return fileList.get(row).getName();
		else if (col == 1)
			return fileList.get(row).getAbsolutePath();
		else 
			return "" + (row * row);

	}
	
	public static void main(String[] args) throws InterruptedException {
			new Search();

	}

}
