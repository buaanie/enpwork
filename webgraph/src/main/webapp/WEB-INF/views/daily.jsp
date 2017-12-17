<%--
  Created by IntelliJ IDEA.
  User: stcas
  Date: 2017/11/8
  Time: 17:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<html lang="zh-CN" >
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">   <%-- 在IE运行最新的渲染模式 --%>
    <meta name="viewport" content="width=device-width, initial-scale=1">   <%-- 初始化移动浏览显示 --%>
    <meta name="Author" content="niejian">

    <link rel="stylesheet" type="text/css" href="../../static/src/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="../../static/src/css/index.css">
    <script src="../../static/src/js/jquery.min.js"></script>
    <script src="../../static/src/js/bootstrap.min.js"></script>
    <title> 每日新闻 </title>
</head>
<body>
    <header>
        <%@include file="header.jsp"%>
    </header>
    <%--左侧菜单栏--%>
    <div class="container-fluid">
        <div class="row-fluie">
            <div class="col-sm-3 col-md-2 sidebar">
                <ul class="nav nav-sidebar">
                    <li class="nav-item active"><a href="#">全部事件</a></li>
                    <li class="nav-item">自然灾害</li>
                    <li class="nav-item">事故灾难</li>
                    <li class="nav-item">公共安全</li>
                    <li class="nav-item">社会安全</li>
                    <li class="nav-item">政治军事</li>
                </ul>
            </div>
        </div>
    </div>
    <%--右侧展示--%>
    <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
        <h1 class="page-header"><i class="fa fa-cog fa-spin"></i>&nbsp;控制台<small>&nbsp;&nbsp;&nbsp;新闻展示</small></h1>
        <!-- 载入左侧菜单指向的jsp（或html等）页面内容 -->
        <div id="content">
            <h4>
                <strong>使用指南：</strong><br>
                <br><br>默认页面内容……
            </h4>
        </div>
    </div>
</body>
</html>
