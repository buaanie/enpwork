<%--
  Created by IntelliJ IDEA.
  User: stcas
  Date: 2017/11/8
  Time: 23:20
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Bootstrap 实例 - 响应式的导航栏</title>
    <link rel="stylesheet" type="text/css" href="../../static/src/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="../../static/src/css/index.css">
    <script src="../../static/src/js/jquery.min.js"></script>
    <script src="../../static/src/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="navbar">
                <div class="navbar-inner">
                    <div class="container-fluid">
                        <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar"><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></a> <a href="#" class="brand">网站名</a>
                        <div class="nav-collapse collapse navbar-responsive-collapse">
                            <ul class="nav">
                                <li class="active">
                                    <a href="#">主页</a>
                                </li>
                                <li>
                                    <a href="#">链接</a>
                                </li>
                                <li>
                                    <a href="#">链接</a>
                                </li>
                                <li class="dropdown">
                                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">下拉菜单<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                        <li>
                                            <a href="#">下拉导航1</a>
                                        </li>
                                        <li>
                                            <a href="#">下拉导航2</a>
                                        </li>
                                        <li>
                                            <a href="#">其他</a>
                                        </li>
                                        <li class="divider">
                                        </li>
                                        <li class="nav-header">
                                            标签
                                        </li>
                                        <li>
                                            <a href="#">链接1</a>
                                        </li>
                                        <li>
                                            <a href="#">链接2</a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                            <ul class="nav pull-right">
                                <li>
                                    <a href="#">右边链接</a>
                                </li>
                                <li class="divider-vertical">
                                </li>
                                <li class="dropdown">
                                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">下拉菜单<strong class="caret"></strong></a>
                                    <ul class="dropdown-menu">
                                        <li>
                                            <a href="#">下拉导航1</a>
                                        </li>
                                        <li>
                                            <a href="#">下拉导航2</a>
                                        </li>
                                        <li>
                                            <a href="#">其他</a>
                                        </li>
                                        <li class="divider">
                                        </li>
                                        <li>
                                            <a href="#">链接3</a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>

                    </div>
                </div>

            </div>
        </div>
    </div>
    <div class="row-fluid">
        <div class="span2">
            <ul class="nav nav-list">
                <li class="nav-header">
                    列表标题
                </li>
                <li class="active">
                    <a href="#">首页</a>
                </li>
                <li>
                    <a href="#">库</a>
                </li>
                <li>
                    <a href="#">应用</a>
                </li>
                <li class="nav-header">
                    功能列表
                </li>
                <li>
                    <a href="#">资料</a>
                </li>
                <li>
                    <a href="#">设置</a>
                </li>
                <li class="divider">
                </li>
                <li>
                    <a href="#">帮助</a>
                </li>
            </ul>
        </div>
        <div class="span6">
            <h2>
                标题
            </h2>
            <p>
                本可视化布局程序在HTML5浏览器上运行更加完美, 能实现自动本地化保存, 即使关闭了网页, 下一次打开仍然能恢复上一次的操作.
            </p>
            <p>
                <a class="btn" href="#">查看更多 »</a>
            </p>
            <ul class="thumbnails">
                <li class="span4">
                    <div class="thumbnail">
                        <img alt="300x200" src="img/people.jpg" />
                        <div class="caption">
                            <h3>
                                冯诺尔曼结构
                            </h3>
                            <p>
                                也称普林斯顿结构，是一种将程序指令存储器和数据存储器合并在一起的存储器结构。程序指令存储地址和数据存储地址指向同一个存储器的不同物理位置。
                            </p>
                            <p>
                                <a class="btn btn-primary" href="#">浏览</a> <a class="btn" href="#">分享</a>
                            </p>
                        </div>
                    </div>
                </li>
                <li class="span4">
                    <div class="thumbnail">
                        <img alt="300x200" src="img/city.jpg" />
                        <div class="caption">
                            <h3>
                                哈佛结构
                            </h3>
                            <p>
                                哈佛结构是一种将程序指令存储和数据存储分开的存储器结构，它的主要特点是将程序和数据存储在不同的存储空间中，进行独立编址。
                            </p>
                            <p>
                                <a class="btn btn-primary" href="#">浏览</a> <a class="btn" href="#">分享</a>
                            </p>
                        </div>
                    </div>
                </li>
                <li class="span4">
                    <div class="thumbnail">
                        <img alt="300x200" src="img/sports.jpg" />
                        <div class="caption">
                            <h3>
                                改进型哈佛结构
                            </h3>
                            <p>
                                改进型的哈佛结构具有一条独立的地址总线和一条独立的数据总线，两条总线由程序存储器和数据存储器分时复用，使结构更紧凑。
                            </p>
                            <p>
                                <a class="btn btn-primary" href="#">浏览</a> <a class="btn" href="#">分享</a>
                            </p>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
        <div class="span4">
            <div class="hero-unit well">
                <h1>
                    Hello, world!
                </h1>
                <p>
                    这是一个可视化布局模板, 你可以点击模板里的文字进行修改, 也可以通过点击弹出的编辑框进行富文本修改. 拖动区块能实现排序.
                </p>
                <p>
                    <a class="btn btn-primary btn-large" href="#">参看更多 »</a>
                </p>
            </div>
        </div>
    </div>
</div>

</body>
</html>