package com.crawler;

import com.model.ItemSubject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;

/**
 * Created by ACT-NJ on 2017/6/8.
 */
public class FilePipiLine implements Pipeline{
    @Override
    public void process(ResultItems resultItems, Task task) {
        ItemSubject item = (ItemSubject) resultItems.get("item");
        System.out.println(item.toString());
    }

}
