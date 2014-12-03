/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.spider;

import com.lilonghua.utils.Tools;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author wpf
 */
public class TextController {
    private static final Logger log = Logger.getLogger(TextController.class.getName());

    public static void indexNetBySpider(String sPath, String dPath) throws IOException {
        try {
            String crawlStorageFolder = Tools.getIndexCrawlPath();
            //开启爬虫个数（线程个数）
            int numberOfCrawlers = 7;

            CrawlConfig config = new CrawlConfig();
            config.setCrawlStorageFolder(crawlStorageFolder);
            //开启增量爬虫（不删除之前爬去记录）
            config.setResumableCrawling(true);
            //伪装google爬虫
            config.setUserAgentString("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            
            List<String> crawlDomains = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sPath)))) {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if(line.trim() != null && !line.trim().isEmpty() && line.trim().startsWith("http://")){
                         crawlDomains.add(line);
                         log.info(line);
                    }
                }
            }

            /*
             * Instantiate the controller for this crawl.
             */
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            /*
             * For each crawl, you need to add some seed urls. These are the first
             * URLs that are fetched and then the crawler starts following links
             * which are found in these pages
             */
            for (String domain : crawlDomains) {
                controller.addSeed(domain);
            }

            TextCrawler.configure(crawlDomains, crawlStorageFolder,LuceneHtml.GetInstance());

            controller.start(TextCrawler.class, numberOfCrawlers);
            LuceneHtml.Close();
        } catch (Exception ex) {
            LuceneHtml.Close();
           log.error("网页索引失败", ex);
        }finally{
             LuceneHtml.Close();
        }
    }
}
