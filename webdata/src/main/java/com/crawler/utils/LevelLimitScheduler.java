package com.crawler.utils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

/**
 * Created by ACT-NJ on 2017/6/9.
 */
public class LevelLimitScheduler extends PriorityScheduler{
    public int levelLimit = 3;
    public LevelLimitScheduler(int highestLevel){
        this.levelLimit = highestLevel;
    }
    public synchronized void push(Request request, Task task){
        if (((Integer) request.getExtra("_level")) <= levelLimit) {
            super.push(request, task);
        }
    }
}
