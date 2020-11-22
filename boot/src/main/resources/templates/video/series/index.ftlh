<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Series</title>
    <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
    <script src="/webjars/layui/2.5.6/layui.all.js"></script>
    <script src="/static/js/video/subject.js"></script>
    <link rel="stylesheet" href="/static/css/video.css">
    <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div>
    <table id="series" class="layui-table" lay-even>
        <thead>
        <tr>
            <th>No.</th>
            <th>Title</th>
            <th>Search</th>
        </tr>
        </thead>
        <tbody>
        <#list entities as entity>
            <tr>
                <td>${entity_index+1}</td>
                <td style="width: 50%">${entity.title}</td>
                <td>
                    <a href="javascript:void(0);" onclick="searchSeries(${entity.id?c})">Search</a>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>