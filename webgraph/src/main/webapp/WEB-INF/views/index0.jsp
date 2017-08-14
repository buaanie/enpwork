<%--
  Created by IntelliJ IDEA.
  User: ACT-NJ
  Date: 2017/3/15
  Time: 20:53
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>demo</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="../../static/src/css/test.css"/> "/>
    <script type="text/javascript" src="../../static/src/js/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="../../static/src/js/d3.min.js"></script>
    <script type="text/javascript" src="../../static/src/js/neod3.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            function getsearch() {
                var query = $("#isearch").val();
                $("#search-display").empty();
                $.ajax({
                    type: "GET",
                    url: '<c:url value="/searchFileByName?name="></c:url>' + encodeURIComponent(query),
                    dataType: "json",
                    success: function (data) {
                        if (data == null) {
                            alert("无效查询");
                            return;
                        }
                        drawneoGraphD3('#search-display', query, data.data);
                    },
                    error: function (e) {
                        alert("无查询结果");
                    }
                });
                return false;
            }
            getsearch();
            $("#search").submit(getsearch);
        });
    </script>
</head>
<body>
<div class = "show">
    <div class="searchBox">
        <form class="form-inline" role="search" id="search">
            <div class="search-group" style="display: inline;">
                <span>
                    <input type="text" placeholder="search for event name" value="data" class="form-control" id="isearch">
                </span>
                <span>
                    <button class="btn btn-default" type="submit">Search</button>
                </span>
            </div>
        </form>
    </div>
    <div class="row" style="width:1280px;margin-top: 12px;">
        <div id="search-display" style="height: 800px;"></div>
    </div>
</div>
</body>
</html>
