package com.crawler.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ACT-NJ on 2017/7/22.
 */
public class PostHelper {
    private  HttpClient httpClient = new HttpClient();
    public  void doPost(String request, Map<String, String> para) {
        PostMethod postMethod = new PostMethod(request);

//		int size = para.size();
//		NameValuePair[] postData = new NameValuePair[size];
//		postData[0] = new NameValuePair("", "");
//		postMethod.addParameters(postData);

        for (String name : para.keySet()) {
            postMethod.addParameter(name, para.get(name));
        }

        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(postMethod);
            System.out.println("status : " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                String response = postMethod.getResponseBodyAsString();
                postMethod.releaseConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
