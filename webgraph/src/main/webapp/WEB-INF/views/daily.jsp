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

    <link rel="stylesheet" type="text/css" href="../../static/src/css/bootstrap.min.css">
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
    <script type="text/javascript">
        /*
         * 对选中的标签激活active状态，对先前处于active状态但之后未被选中的标签取消active
         * （实现左侧菜单中的标签点击后变色的效果）
         */
        $(document).ready(function () {
            $('ul.nav > li').click(function (e) {
                //e.preventDefault();	加上这句则导航的<a>标签会失效
                $('ul.nav > li').removeClass('active');
                $(this).addClass('active');
            });
        });
        /*
         * 解决ajax返回的页面中含有javascript的办法：
         * 把xmlHttp.responseText中的脚本都抽取出来，不管AJAX加载的HTML包含多少个脚本块，我们对找出来的脚本块都调用eval方法执行它即可
         */
        function executeScript(html)
        {
            var reg = /<script[^>]*>([^\x00]+)$/i;
            //对整段HTML片段按<\/script>拆分
            var htmlBlock = html.split("<\/script>");
            for (var i in htmlBlock)
            {
                var blocks;//匹配正则表达式的内容数组，blocks[1]就是真正的一段脚本内容，因为前面reg定义我们用了括号进行了捕获分组
                if (blocks = htmlBlock[i].match(reg))
                {
                    //清除可能存在的注释标记，对于注释结尾-->可以忽略处理，eval一样能正常工作
                    var code = blocks[1].replace(/<!--/, '');
                    try
                    {
                        eval(code) //执行脚本
                    }
                    catch (e)
                    {
                    }
                }
            }
        }
        /*
         * 利用div实现左边点击右边显示的效果（以id="content"的div进行内容展示）
         * 注意：
         *   ①：js获取网页的地址，是根据当前网页来相对获取的，不会识别根目录；
         *   ②：如果右边加载的内容显示页里面有css，必须放在主页（即例中的index.jsp）才起作用
         *   （如果单纯的两个页面之间include，子页面的css和js在子页面是可以执行的。 主页面也可以调用子页面的js。但这时要考虑页面中js和渲染的先后顺序 ）
         */
        function showAtRight(url) {
            var xmlHttp;
            if (window.XMLHttpRequest) {
                // code for IE7+, Firefox, Chrome, Opera, Safari
                xmlHttp=new XMLHttpRequest();	//创建 XMLHttpRequest对象
            }
            else {
                // code for IE6, IE5
                xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlHttp.onreadystatechange=function() {
                //onreadystatechange — 当readystate变化时调用后面的方法

                if (xmlHttp.readyState == 4) {
                    //xmlHttp.readyState == 4	——	finished downloading response

                    if (xmlHttp.status == 200) {
                        //xmlHttp.status == 200		——	服务器反馈正常

                        document.getElementById("content").innerHTML=xmlHttp.responseText;	//重设页面中id="content"的div里的内容
                        executeScript(xmlHttp.responseText);	//执行从服务器返回的页面内容里包含的JavaScript函数
                    }
                    //错误状态处理
                    else if (xmlHttp.status == 404){
                        alert("出错了☹   （错误代码：404 Not Found），……！");
                        /* 对404的处理 */
                        return;
                    }
                    else if (xmlHttp.status == 403) {
                        alert("出错了☹   （错误代码：403 Forbidden），……");
                        /* 对403的处理  */
                        return;
                    }
                    else {
                        alert("出错了☹   （错误代码：" + request.status + "），……");
                        /* 对出现了其他错误代码所示错误的处理   */
                        return;
                    }
                }

            }
            //把请求发送到服务器上的指定文件（url指向的文件）进行处理
            var real_path_url = "\\WEB-INF\\views\\"+url
            alert(real_path_url)
            xmlHttp.open("GET", real_path_url, true);		//true表示异步处理
            xmlHttp.send();
        }
    </script>
</body>
</html>
