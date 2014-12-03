/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.lucene;

import com.lilonghua.utils.Tools;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author wpf
 */
public class LuceneFile {

    private static IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, Tools.getAnalyzer());
    private static IndexWriter indexWriter;
    private static ArrayList<Thread> threadList = new ArrayList<>();
    static final Logger logger = Logger.getLogger(LuceneFile.class.getName());

    private LuceneFile() {
    }

    public static IndexWriter GetInstance() {
        synchronized (threadList) {
            if (indexWriter == null) {
                try {
                    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                    LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
                    mergePolicy.setMergeFactor(100);//控制多个segment合并的频率，值较大时建立索引速度较快，默认是10，可以在建立索引时设置为100。
                    indexWriterConfig.setMergePolicy(mergePolicy);

                    // 创建系统文件
                    Directory directory;
                    directory = new SimpleFSDirectory(new File(Tools.getIndexPath()));

                    indexWriter = new IndexWriter(directory, indexWriterConfig);
                } catch (IOException ex) {
                   logger.error("初始化索引文件失败", ex);
                }
            }

            if (!threadList.contains(Thread.currentThread())) {
                threadList.add(Thread.currentThread());
            }

            return indexWriter;
        }
    }

    public static void Close() {
        synchronized (threadList) {
            if (threadList.contains(Thread.currentThread())) {
                threadList.remove(Thread.currentThread());
            }

            if (threadList.isEmpty()) {
                if (indexWriter != null) {                    
                    try {
                        indexWriter.commit();
                        indexWriter.close();
                        indexWriter = null;
                    } catch (IOException ex) {
                        logger.error("关闭索引文件失败", ex);
                    }
                }
            }
        }
    }
}
