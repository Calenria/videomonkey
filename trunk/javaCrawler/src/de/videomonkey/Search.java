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
import javax.swing.event.TableModelListener;

import org.apache.log4j.PropertyConfigurator;

import de.videomonkey.models.ThreadInfosTableModel;
import de.videomonkey.utils.FileUtils;
import de.videomonkey.utils.ThreadInfos;

/**
 * @author Azrail
 * 
 */
public class Search implements TableModelListener {

	private JTable table;
	private ThreadInfosTableModel model;
	public static HashMap<String, Movie> movies = new HashMap<String, Movie>();

	ArrayList<File> fileList = FileUtils.searchFile(new File("C:\\TASTEMP"),Movie.getWantedExtensions());

	public Search() throws InterruptedException {
		PropertyConfigurator.configureAndWatch("log.properties", 60 * 1000);

		model = new ThreadInfosTableModel();
		table = new JTable();

		table.setModel(model);
		model.addTableModelListener(this);

		for (File file : fileList) {
			ThreadInfos thread = new ThreadInfos(file.getAbsolutePath(),new Movie(file));
			thread.start();
			model.addRow(thread);
		}

		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) throws InterruptedException {
		new Search();
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		table.revalidate();
	}
}
