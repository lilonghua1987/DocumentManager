/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.test;

import com.lilonghua.utils.Tools;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 *
 * @author wpf
 */
public class Controller {
        public static void main(String[] args) throws Exception {
                String crawlStorageFolder = Tools.getIndexCrawlPath();
                int numberOfCrawlers = 7;

                CrawlConfig config = new CrawlConfig();
                config.setCrawlStorageFolder(crawlStorageFolder);
                
                String[] crawlDomains = new String[] { "http://cstest.scu.edu.cn/~liuyiguang/html/index.html" };

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
                
                MyCrawler.configure(crawlDomains, crawlStorageFolder);

                /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
                 */
                controller.start(MyCrawler.class, numberOfCrawlers);
        }
}
