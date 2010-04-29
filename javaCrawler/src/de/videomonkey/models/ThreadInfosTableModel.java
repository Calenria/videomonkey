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
		this.fireTableRowsInserted(this.rows.size() - 1, this.rows.size() - 1);
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ThreadInfos row = rows.get(rowIndex);
		System.out.println(row);
		System.out.println("Path: " + row.getPath());

		switch (columnIndex) {
			case 0:
				row.getPath();
				break;
			case 1:
				row.getPath();
				break;
			case 2:
				row.getCurrentStatus();
				break;
			default:
				break;
		}
		return null;
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
		return String.class;
	}

}
