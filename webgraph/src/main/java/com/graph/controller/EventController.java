package com.graph.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletRequest;

/**
 * Created by stcas on 2017/12/24.
 */
@Controller
public class EventController {
    @RequestMapping(method = RequestMethod.GET, value = {"/events/getLastestByType"})
    public Object getLastest(
            @RequestParam(value = "type", defaultValue = "0") int type,
            @RequestParam(value = "period", defaultValue = "3") int time,
            Model model, ServletRequest request){
        JSONObject jsonrs = new JSONObject();
        return new ResponseEntity<String>(jsonrs.toString(), HttpStatus.OK);
    }
}
