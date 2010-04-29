package de.videomonkey.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.videomonkey.utils.ThreadInfos;

public class ThreadInfosTableModel extends AbstractTableModel {

	private Vector<ThreadInfos> rows = new Vector<ThreadInfos>();

	public ThreadInfosTableModel() {
	}

	public void addRow(ThreadInfos threadInfos) {
		this.rows.add(threadInfos);
		this.fireTableRowsInserted(this.rows.size() - 1, this.rows.size());
	}

	@Override
	public String getColumnName(int column) {

		String val = "";

		switch (column) {
		case 0:
			val = "Pfad";
			break;
		case 1:
			val = "Titel";
			break;
		case 2:
			val = "Status";
			break;
		case 3:
			val = "Rename";
			break;
		case 4:
			val = ".nfo Generieren";
			break;
		default:
			break;
		}

		return val;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ThreadInfos row = rows.get(rowIndex);
		Object val = new Object();

		switch (columnIndex) {
		case 0:
			val = row.getPath();
			break;
		case 1:
			if (!row.isMovieReady()) {
				val = "Suche noch nach Titel";
			} else {
				val = row.getMovie().wiIMDB.get_title();
			}
			break;
		case 2:
			val = row.getCurrentStatus();
			break;
		case 3:
			val = row.getRenameMovieFile();
			// System.out.println(row.getUseMovie());
			break;
		case 4:
			val = row.getCreateMovieNfo();
			// System.out.println(row.getUseMovie());
			break;
		default:
			break;
		}

		this.fireTableCellUpdated(rowIndex, columnIndex);

		return val;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		ThreadInfos row = rows.get(rowIndex);

		switch (columnIndex) {
		case 0:
			row.setPath((String) aValue);
			break;
		case 1:
			row.setPath((String) aValue);
			break;
		case 2:
			row.getCurrentStatus();
			break;
		default:
			break;
		}
	}

	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 3:
			return Boolean.class;
		case 4:
			return Boolean.class;
		default:
			return String.class;
		}

		
		
	}

}
