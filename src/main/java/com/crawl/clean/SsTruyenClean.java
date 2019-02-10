package com.crawl.clean;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.crawl.data.CategoryDao;
import com.crawl.data.ChapterDao;
import com.crawl.data.StoryDao;
import com.crawl.model.Chapter;
import com.crawl.model.Story;
import com.google.gson.Gson;

public class SsTruyenClean {
	private static final Logger logger = LogManager.getLogger(SsTruyenClean.class);
	private CategoryDao categoryDao;
	private StoryDao storyDao;
	private ChapterDao chapterDao;

	public SsTruyenClean() {
		categoryDao = new CategoryDao();
		storyDao = new StoryDao();
		chapterDao = new ChapterDao();
	}

	public void updateChapter() {
		logger.info("start updateChapter");
		List<Chapter> chapters = chapterDao.findAll();
		HashSet<String> udts = new HashSet<>();
		for (Chapter chapter : chapters) {
			udts.add(chapter.getStoryName());
		}
		
		logger.info("updateChapter size :  " + udts.size());

		// 3. Pool update chapter
		ExecutorService executor = Executors.newFixedThreadPool(1);
		List<Runnable> tasks = new ArrayList<>();
		for (String chapter : udts) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					logger.info("update Chapter:" + chapter);
					chapterDao.updateStoryId(chapter);
				}

			};
			tasks.add(task);
		}

		for (Runnable runnable : tasks) {
			executor.submit(runnable);
		}

		executor.shutdown();

	}

	public void updateStory() {
		logger.info("start updateStory");
		List<Story> stories = storyDao.findAll();
		HashSet<Story> storiesUpdate = new HashSet<>();
		for (Story story : stories) {
			String name = story.getName();
			// 1. Get list story by name and status = null
			List<Story> storiesByName = stories.stream()
					.filter(x -> x.getName().trim().equals(name.trim()) && x.getStatus() == null)
					.collect(Collectors.toList());
			if (storiesByName.size() > 0) {
				// 2. Get first story by name and status <> null
				Optional<Story> optionStory = stories.stream()
						.filter(x -> x.getName().trim().equals(name.trim()) && x.getStatus() != null).findFirst();
				if (optionStory.isPresent() == true) {
					for (Story story2 : storiesByName) {
						Story firstStory = optionStory.get();
						try {
							Story needUpdate = firstStory.clone();
							needUpdate.setId(story2.getId());
							storiesUpdate.add(needUpdate);
						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							logger.error(e);
						}

					}
				}
			}

		}
		System.out.println("storiesUpdate size =" + storiesUpdate.size());

		// 3. Pool update list story
		ExecutorService executor = Executors.newFixedThreadPool(50);
		List<Runnable> tasks = new ArrayList<>();
		for (Story story : storiesUpdate) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					logger.info("Clean story:" + new Gson().toJson(story));
					storyDao.update(story);
				}

			};
			tasks.add(task);
		}

		for (Runnable runnable : tasks) {
			executor.submit(runnable);
		}

		executor.shutdown();

	}

}
