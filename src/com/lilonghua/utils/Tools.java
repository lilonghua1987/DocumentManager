/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 *
 * @author wpf
 */
public class Tools {

    private static String projectPath = ""; // 索引库目录
    private static Analyzer analyzer; // 分词器

    private static String getProjectPath() {
        if ("".equals(projectPath)) {
            projectPath = System.getProperty("user.dir");
        }
        return projectPath;
    }

    public static String getIndexPath() {
        return getProjectPath() + "/indexFiles/fileDate";
    }

    public static String getIndexWebPath() {
        return getProjectPath() + "/indexFiles/webData";
    }

    public static String getIndexCrawlPath() {
        return getProjectPath() + "/indexFiles/crawlData";
    }

    public static Analyzer getAnalyzer() {
        if (analyzer == null) {
            analyzer = new IKAnalyzer(true);//使用智能分词
        }
        return analyzer;
    }

    public static void getAllFiles(String strPath, List<String> filePathList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getAllFiles(files[i].getAbsolutePath(), filePathList);
            } else {
                filePathList.add(files[i].getAbsolutePath());
            }
        }
    }

    public static void getAllFiles(String strPath, List<String> filePathList, String ext) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getAllFiles(files[i].getAbsolutePath(), filePathList, ext);
            } else {
                String strFileName = files[i].getName().toLowerCase();
                if (getFileEXT(strFileName) != null && getFileEXT(strFileName).equals(ext)) {
                    //System.out.println("---"+strFileName);
                    filePathList.add(files[i].getAbsolutePath());
                }
            }
        }
    }

    public static void getAllFiles(String strPath, List<String> filePathList, List<String> extList) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getAllFiles(files[i].getAbsolutePath(), filePathList, extList);
            } else {
                String strFileName = files[i].getName().toLowerCase();
                if (getFileEXT(strFileName) != null && !extList.isEmpty() && extList.contains(getFileEXT(strFileName))) {
                    filePathList.add(files[i].getAbsolutePath());
                }
            }
        }
    }

    public static String getFileEXT(String fileName) {

        if (fileName == null) {
            return null;
        } else if (fileName.isEmpty() || !fileName.contains(".")) {
            return null;
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }

    }

    public static String getFileLowerEXT(String fileName) {

        if (fileName == null) {
            return null;
        } else if (fileName.isEmpty() || !fileName.contains(".")) {
            return null;
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }

    }

    public static String getDateTime() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");
        String fdate = dateformat.format(new Date());
        return fdate;
    }

    public static void setLogger(StringBuilder log, String str) {
        log.append(getDateTime());
        log.append(":");
        log.append(str);
        log.append("\n");
    }

    public static String getLogger(String str) {
        StringBuilder log = new StringBuilder();
        log.append(getDateTime());
        log.append(":");
        log.append(str);
        log.append("\n");
        return log.toString();
    }

    public static void main(String args[]) {
        List<String> filesList = new ArrayList<>();
        getAllFiles("D:/快盘", filesList, "pdf");

        for (String fileName : filesList) {
            System.out.println(fileName);
        }
    }
}
