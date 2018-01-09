<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%@ page trimDirectiveWhitespaces="true" %>
<html lang="zh-CN">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">   <%-- 在IE运行最新的渲染模式 --%>
		<meta name="viewport" content="width=device-width, initial-scale=1">   <%-- 初始化移动浏览显示 --%>
		<meta name="Author" content="Dreamer-1.">
		
		<!-- 引入各种CSS样式表 -->
		<link rel="stylesheet" href="../../static/src/css/bootstrap.css">
		<link rel="stylesheet" href="../../static/src/css/font-awesome.css">
		<link rel="stylesheet" href="../../static/src/css/index.css">	<!-- 修改自Bootstrap官方Demon，你可以按自己的喜好制定CSS样式 -->
		<link rel="stylesheet" href="../../static/src/css/font-change.css">	<!-- 将默认字体从宋体换成微软雅黑（个人比较喜欢微软雅黑，移动端和桌面端显示效果比较接近） -->
		
		<script type="text/javascript" src="../../static/src/js/jquery.min.js"></script>
		<script type="text/javascript" src="../../static/src/js/bootstrap.min.js"></script>
	
		<title>- 毕设展示系统 -</title>
	</head>
	
	<body>
		<nav class="navbar navbar-inverse navbar-fixed-top">
      		<div class="container">
        		<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" >
		            	<span class="sr-only">Toggle navigation</span>
		            	<span class="icon-bar"></span>
		            	<span class="icon-bar"></span>
		            	<span class="icon-bar"></span>
					</button>
	          		<a class="navbar-brand" href="index.jsp">事件人物图谱</a>
        		</div>

				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">			            
						<li><a href="#" onclick="getPage('news.jsp')">新闻聚合</a></li>
						<li><a href="#" onclick="getPage('detail.jsp')">评论挖掘</a></li>
						<li><a href="#" onclick="getPage('graph.jsp')" >图谱展示</a></li>
					</ul>
          			
        		</div>
      		</div>
    	</nav>

		<div class="container-fluid">
            <div class="col-sm-3 col-md-2 sidebar" data-spy="affix">
                <ul id="myNav" class="nav nav-tabs nav-stacked">
                    <li><a href="#all" onclick="showAtRight('0')">全部事件</a></li>
                    <li><a href="#politics" onclick="showAtRight('1')">政治军事</a></li>
                    <li><a href="#nature" onclick="showAtRight('2')">自然灾害</a></li>
                    <li><a href="#public" onclick="showAtRight('3')">公共热点</a></li>
                </ul>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                    <!-- 载入左侧菜单指向的jsp（或html等）页面内容 -->
                    <div id="content">
                        <script type="x-tmpl-mustache" id="weiboShow">
                            <ul style="height: 555px;overflow-y:auto;width:480px;">
                                {{#content}}
                                <li style="background-color: #eeeeee;">
                                    <p class="eText">{{content}}</p>
                                    <p class="ehot">{{hot}}</p>
                                </li>
                                {{/content}}
                            </ul>
                        </script>
                    </div>
            </div>
        </div>
    <script type="text/javascript">
        function showAtRight(type) {
            $.ajax({
                type:"GET",
                url:'/event/getLastestByType?type='+type+'&period=3',
                dataType: "json",
                success: function (data) {
                    if(data==null || data.errors.length>0){
                        alert("参数有误");
                        return ;
                    }
                    alert("hhlo");
//                    var array = data.results[0].data;
//                    drawETable('neo4j-object',query,array);
                },
                error: function (e) {
                    alert("无查询结果");
                }
            });
            return false;
        }
    </script>
	</body>
</html>