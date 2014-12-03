/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 逗号分隔型取值格式（英文全称为Comma Separated Values，简称CSV），是一种纯文本格式，用来存储数据。
 * 在CSV中，数据的字段由逗号分开，程序通过读取文件重新创建正确的字段，方法是每次遇到逗号时开始新一段数据。
 */
package com.lilonghua.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author wpf
 */
public class CSVFile {

    private static final Logger Log = Logger.getLogger(CSVFile.class.getName());
    private static final String ENCODE = "GBK";
    private String[] cName;
    private List<String[]> resultList = new ArrayList<>();

    /**
     * @return the cName
     */
    public String[] getcName() {
        return cName;
    }

    /**
     * @param cName the cName to set
     */
    public void setcName(String[] cName) {
        this.cName = cName;
    }

    /**
     * @return the resultList
     */
    public List<String[]> getResultList() {
        return resultList;
    }

    /**
     * @param resultList the resultList to set
     */
    public void setResultList(List<String[]> resultList) {
        this.resultList = resultList;
    }

    public void readFile(String path) throws Exception {

        DataInputStream in = new DataInputStream(new FileInputStream(new File(path)));
        InputStreamReader isw = new InputStreamReader(in, ENCODE);
        BufferedReader reader = new BufferedReader(isw);
        long count = 0;
        // 读取直到最后一行 
        String line = "";
        while ((line = reader.readLine()) != null) {
            // 把一行数据分割成多个字段 
            String item[] = line.split(",");
            if (count == 0) {
                //lineList.toArray(cName);
                cName = item;
            } else {
                resultList.add(item);
            }
            ++count;
        }
        reader.close();

        setcName(cName);
        setResultList(resultList);
        
        setResultList(filterWeek(0,6));
    }
    
    public void doFilter(int index, int filter){
        switch(index){
            case 0:
                
        }
    }
    
    public List<String[]> filterWeek(int index, int filterIndex){
        List<String[]> filterList = new ArrayList();
        for (int i = 0; i < resultList.size(); i++) {
            if (index < resultList.get(i).length){
                String str = resultList.get(i)[index];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
                try {
                    Date date = sdf.parse(str);
                    Calendar cal = Calendar.getInstance(); // 创建一个日历对象。
                    cal.setTime(date);
                    if (cal.get(Calendar.DAY_OF_WEEK) == filterIndex){
                        filterList.add(resultList.get(i));
                    }
                } catch (ParseException ex) {
                   Log.error(filterIndex, ex);
                }
            }
        }       
        return filterList;
    }
    
    public void writeFile(String savePath, List<String[]> saveList) throws IOException {
        File csv = new File(savePath); // CSV文件 
        // 追记模式 
        BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));

        for (int i = 0; i < saveList.size(); i++) {
            StringBuilder str = new StringBuilder();
            for(int j = 0; j < saveList.get(i).length; j++){
                str.append(saveList.get(i)[j]);
                if (j < saveList.get(i).length - 1)
                    str.append(",");
            }
            bw.write(str.toString());
             // 新增一行数据 
            if (i < saveList.size() -1)
                bw.newLine();
        }
        bw.close();
    }
    
    public static void main (String[] arg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");            
        Date date = null;
        try {
            date = sdf.parse("2013-7-29");
        } catch (ParseException ex) {
            Log.debug(ex);
        }
        Calendar cal = Calendar.getInstance(); // 创建一个日历对象。
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.setTime(date);       
        System.out.println(cal.get(Calendar.DAY_OF_WEEK));
        System.out.println(cal.get(Calendar.DAY_OF_MONTH));
        System.out.println(cal.get(Calendar.MONTH));
        System.out.println(sdf.format(cal.getTime()));
    }
}