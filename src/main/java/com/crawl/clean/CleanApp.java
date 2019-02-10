package com.crawl.clean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class CleanApp 
{
	private static final Logger logger = LoggerFactory.getLogger(CleanApp.class);
	private SsTruyenClean ssTruyenClean;
	private ZipContent zipContent;
	
	public CleanApp() {
		ssTruyenClean = new SsTruyenClean();
		zipContent = new ZipContent();
	}
	private void cleanSsTruyen() {
		ssTruyenClean.updateChapter();
	}
	
	private void zipSSTruyen() throws Exception{
		zipContent.zipChapter("D:\\data\\sstruyen", "D:\\data\\ouput\\sstruyen");
	}
    public static void main( String[] args ) throws Exception
    {
    	logger.info("start clean");
        CleanApp app = new CleanApp();
        //app.cleanSsTruyen();
        app.zipSSTruyen();
    }
}
