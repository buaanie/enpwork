<%--
  Created by IntelliJ IDEA.
  User: stcas
  Date: 2017/11/7
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<script src="../../static/src/js/bootstrap.min.js"></script>

<div>
    <%--导航栏--%>
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar" >
                    <span class="sr-only">导航切换</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">毕设展示</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li class="active"><a href="/daily" onclick="location='daily.jsp'"> 每日新闻</a></li>
                    <li><a href="/evolution" onclick="location='evolution.jsp'"></i> 演化展示</a></li>
                    <li><a href="/graph" onclick="location='graph.jsp'" >事件图谱</a></li>
                </ul>
            </div>
        </div>
    </nav>
</div>

