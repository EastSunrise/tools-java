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
    <table id="series" class="layui-table">
        <thead>
        <tr>
            <th>No.</th>
            <th>Title</th>
            <th>Resources</th>
            <th>Season</th>
            <th>Video</th>
        </tr>
        </thead>
        <tbody>
        <#list tvs as tv>
            <#assign series=tv.series>
            <#assign seasons=tv.seasons?sort_by('currentSeason')>
            <tr>
                <#assign rows=(seasons?size)+1>
                <td rowspan="${rows}">${tv_index+1}</td>
                <td rowspan="${rows}">
                    <a href="https://imdb.com/title/${series.imdbId}/" target="_blank">${series.title}</a>
                </td>
                <td rowspan="${rows}">
                    <a href="javascript:void(0);" onclick="searchSeries(${series.id?c})">Search</a>
                </td>
            </tr>
            <#list seasons as season>
                <tr>
                    <td>
                        <a href="https://movie.douban.com/subject/${season.dbId?c}/" target="_blank">${season.title}</a>
                    </td>
                    <td>
                        <#if season.archived>
                            Archived
                        <#else>
                            <span class="click-tip" hidden="hidden"></span>
                            <a href="javascript:void(0);" class="season-archive checked" data-id="${season.id?c}"
                               title="click to archive">Archive</a>
                        </#if>
                    </td>
                </tr>
            </#list>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>