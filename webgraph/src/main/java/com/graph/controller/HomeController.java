package com.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;

/**
 * Created by ACT-NJ on 2017/3/15.
 */

// 注解标注此类为springmvc的controller，url映射为"/"
@Controller
public class HomeController {
    //添加一个日志器
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    //映射一个action
    @RequestMapping(method = RequestMethod.GET, value = {"/index","/"})
    public String index(Model model, ServletRequest request) {
        //输出日志文件
        logger.info("the first jsp pages");
        //返回一个index.jsp这个视图
        return "index";
    }

    @RequestMapping("/index2")
    public ModelAndView index2(){

        System.out.print("-----");
        ModelAndView view2= new ModelAndView("index");
        return view2;
    }
}
