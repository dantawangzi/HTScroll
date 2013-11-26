/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mediavirus.parvis.model;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wdou1
 */
public class DataTable extends AbstractTableModel{
    private String[] columnNames;
    private Object[][] data;

    public DataTable(Object[][] d, String[] cn){
        columnNames = cn;
        data = d;
    }
    
    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public Class getColumnClass(int c){
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col){
        data[row][col] = value;
        fireTableCellUpdated(row,col);
    }

    public void setEntireTable(Object[][] update){
        data = update;
        fireTableDataChanged();
    }
}
