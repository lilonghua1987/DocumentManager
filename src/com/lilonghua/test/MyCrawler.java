/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.test;

import com.lilonghua.crawler4j.examples.imagecrawler.Cryptography;
import com.lilonghua.spider.LuceneHtml;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author wpf
 */
public class MyCrawler extends WebCrawler {

    private final static Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private static final Pattern htmlPatterns = Pattern.compile(".*(\\.(html?|shtml))$");
    private static File storageFolder;
    private static String[] crawlDomains;

    public static void configure(String[] domain, String storageFolderName) {
        MyCrawler.crawlDomains = domain;

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

        if (!href.startsWith("http://cstest.scu.edu.cn/~liuyiguang/html")) {
            return false;
        }

        if (htmlPatterns.matcher(href).matches()) {
            return true;
        }

        for (String domain : crawlDomains) {
            if (href.startsWith(domain)) {
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

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
//                        System.out.println("getFetchResponseHeaders: " + page.getFetchResponseHeaders());
//                        System.out.println("getContentType: " + page.getContentType());
//                        System.out.println("getContentEncoding: " + page.getContentEncoding());
//                        System.out.println("getTitle: " + htmlParseData.getTitle());
//                        System.out.println("text: " + html);
            IndexWriter writer = LuceneHtml.GetInstance();
            Document document = new Document();
            document.add(new TextField("Url", url, Field.Store.YES));
            document.add(new TextField("Title", htmlParseData.getTitle(), Field.Store.YES));
            document.add(new TextField("Contents", text, Field.Store.NO));
            try {
                writer.addDocument(document);
            } catch (IOException ex) {
                Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                 LuceneHtml.Close();
            }

            // get a unique name for storing this image
//            String extension = url.substring(url.lastIndexOf("."));
//            String hashedName = Cryptography.MD5(url) + extension;
//
//            // store image
//            IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + hashedName);
//            //IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + url.substring(url.lastIndexOf("/")));
//
//            System.out.println("Stored: " + url);
        }
    }
}
