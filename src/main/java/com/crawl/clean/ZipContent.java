package com.crawl.clean;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import com.crawl.util.FileUtil;

public class ZipContent {
	public void zipChapter(String srcDataDir, String destDir) throws Exception {
		System.out.println(new Date());
		FileUtil.delDir(destDir);
		FileUtil.createPath(destDir);
		File srcDir = new File(srcDataDir);
		File[] dirs = srcDir.listFiles();
		for (File dir : dirs) {
			File[] files = dir.listFiles();
			for (File file : files) {
				String fileName = file.getPath();
				String name = FilenameUtils.getBaseName(fileName);
				String ext = FilenameUtils.getExtension(fileName);
				FileUtil.zipFile(dir.getPath(), name, ext, destDir + File.separator + dir.getName());
			}
		}
		System.out.println(new Date());

	}

}
