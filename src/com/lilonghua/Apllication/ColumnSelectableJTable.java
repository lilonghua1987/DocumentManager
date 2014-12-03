/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.Apllication;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 *
 * @author wpf
 */
public class ColumnSelectableJTable extends JTable{
    
    public ColumnSelectableJTable () {  
        super ();  
        //设置列选择模式  
        setColumnSelectionAllowed (true);  
        setRowSelectionAllowed (false);  
        final JTableHeader header = getTableHeader();  
        //表头增加监听  
        header.addMouseListener (new MouseAdapter() {  
                @Override
                public void mouseReleased (MouseEvent e) {  
                    if (! e.isShiftDown())  
                        clearSelection();  
                    //获取点击的列索引  
                    int pick = header.columnAtPoint(e.getPoint());  
                    //设置选择模型  
                    addColumnSelectionInterval (pick, pick);  
                }  
            });  
          
    }  
    
    public ColumnSelectableJTable (Object[][] items, Object[] headers) {  
        super (items, headers);  
        //设置列选择模式  
        setColumnSelectionAllowed (true);  
        setRowSelectionAllowed (false);  
        final JTableHeader header = getTableHeader();  
        //表头增加监听  
        header.addMouseListener (new MouseAdapter() {  
                @Override
                public void mouseReleased (MouseEvent e) {  
                    if (! e.isShiftDown())  
                        clearSelection();  
                    //获取点击的列索引  
                    int pick = header.columnAtPoint(e.getPoint());  
                    //设置选择模型  
                    addColumnSelectionInterval (pick, pick);  
                }  
            });  
          
    }  
}
