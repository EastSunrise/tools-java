<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Movies</title>
    <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
    <script src="/webjars/layui/2.5.6/layui.all.js"></script>
    <script src="/static/js/video/subject.js"></script>
    <link rel="stylesheet" href="/static/css/video.css">
    <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div>
    <table id="movies" class="layui-table" lay-even>
        <thead>
        <tr>
            <th>No.</th>
            <th>Title</th>
            <th>Resources</th>
            <th>Video</th>
        </tr>
        </thead>
        <tbody>
        <#list movies?sort_by('archived') as movie>
            <tr>
                <td>${movie_index+1}</td>
                <td>
                    <a href="https://movie.douban.com/subject/${movie.dbId?c}" target="_blank">${movie.title}</a>
                </td>
                <td>
                    <a href="javascript:void(0);"
                       onclick="searchMovie(${movie.id?c})" title="click to search">Search</a>
                </td>
                <td>
                    <#if movie.archived>
                        Archived
                    <#else>
                        <span class="click-tip" hidden="hidden"></span>
                        <a href="javascript:void(0);" class="movie-archive checked" data-id="${movie.id?c}"
                           title="click to archive">Archive</a>
                    </#if>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>