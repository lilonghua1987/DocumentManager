/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.spider;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author wpf
 */
public class TextCrawler extends WebCrawler {

    private final static Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private static final Pattern htmlPatterns = Pattern.compile(".*(\\.(htm?|html?|shtml))$");
    private static File storageFolder;
    private static List<String> crawlDomains;
    private static IndexWriter indexWriter;
    
    static final Logger log = Logger.getLogger(TextCrawler.class.getName());

    public static void configure(List<String> domain, String storageFolderName,IndexWriter indexWriter) {
        TextCrawler.crawlDomains = domain;
        TextCrawler.indexWriter = indexWriter;

        storageFolder = new File(storageFolderName);
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        if (filters.matcher(href).matches()) {
            return false;
        }

//        if (htmlPatterns.matcher(href).matches()) {
//            return true;
//        }

        for (String domain : crawlDomains) {
            if (href.startsWith(domain) && htmlPatterns.matcher(href).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (!htmlPatterns.matcher(url).matches()) {
            return;
        }

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            List<WebURL> links = htmlParseData.getOutgoingUrls();

//            System.out.println("Text length: " + text.length());
//            System.out.println("Html length: " + html.length());
//            System.out.println("Number of outgoing links: " + links.size());

            Document document = new Document();
            document.add(new TextField("path", url, Field.Store.YES));
            document.add(new TextField("Title", htmlParseData.getTitle(), Field.Store.YES));
            document.add(new TextField("contents", text, Field.Store.NO));
            try {
                indexWriter.addDocument(document);
            } catch (IOException ex) {
                log.error("网页建立索引失败", ex);
            }
        }
    }
}