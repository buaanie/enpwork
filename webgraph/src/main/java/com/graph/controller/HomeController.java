package com.graph.controller;

import com.alibaba.fastjson.JSONObject;
import com.store.FilesOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletRequest;

/**
 * Created by ACT-NJ on 2017/3/15.
 */

// 注解标注此类为springmvc的controller，url映射为"/"
@Controller
public class HomeController {
    private FilesOpt filesOpter = new FilesOpt();
    //添加一个日志器
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    //映射一个action
    @RequestMapping(method = RequestMethod.GET, value = {"/index","/"})
    public String index(Model model, ServletRequest request) {
        //输出日志文件
        logger.info("the first jsp pages");
        //返回一个index0.jsp这个视图
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/daily"})
    public String daily(Model model, ServletRequest request) {
        //输出日志文件
        logger.info("the daily news jsp pages");
        //返回一个index0.jsp这个视图
        return "daily";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/graph"})
    public String graph(Model model, ServletRequest request) {
        logger.info("the daily news jsp pages");
        //返回一个index0.jsp这个视图
        return "graph";
    }

    @RequestMapping("/test")
    public ModelAndView index2(){
        ModelAndView view= new ModelAndView("test");
        return view;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,value = "/searchFileByName")
    public Object getFileByName(
            @RequestParam(value = "name", defaultValue = "data") String name,
            Model model, ServletRequest request){
//        model.addAllAttributes(request.getParameterMap());
        String path = "D:\\Jworkspace\\enpwork\\webdata\\files\\"+name+".json";
        JSONObject jsonr = (JSONObject) filesOpter.readFile(path,"json");
        return jsonr.toJSONString();
//        return new ResponseEntity<String>(jsonr, HttpStatus.OK);
    }
}
