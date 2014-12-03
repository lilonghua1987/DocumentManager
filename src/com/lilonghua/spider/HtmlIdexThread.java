/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.spider;

import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author wpf
 */
public class HtmlIdexThread extends Thread {
    @Override
    public void run() {
        IndexWriter writer = LuceneHtml.GetInstance();
        LuceneHtml.Close(); 
    }
}
