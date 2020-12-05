<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Subjects</title>
    <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
    <script src="/webjars/layui/2.5.6/layui.all.js"></script>
    <script src="/static/js/video/subject.js"></script>
    <link rel="stylesheet" href="/static/css/video.css">
    <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div class="layui-tab">
    <ul class="layui-tab-title">
        <li class="layui-this">Movies</li>
        <li>TV</li>
    </ul>
    <ul class="layui-tab-content">
        <li class="layui-tab-item layui-show">
            <div>
                <table id="movies" class="layui-table" lay-even>
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Title</th>
                        <th>Year</th>
                        <th>Durations</th>
                        <th>Resources</th>
                        <th>Video</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list movies as movie>
                        <tr>
                            <td>${movie_index+1}</td>
                            <td>
                                <a href="https://movie.douban.com/subject/${movie.dbId?c}"
                                   target="_blank">${movie.title}</a>
                            </td>
                            <td>${movie.year?c}</td>
                            <td>${movie.durations}</td>
                            <td>
                                <a href="javascript:void(0);" class="movie-search" data-id="${movie.id?c}"
                                   title="click to search">Search</a>
                                /
                                <a href="javascript:void(0);" class="movie-input" data-id="${movie.id?c}"
                                   title="click to search with a key">Input</a>
                                /
                                <a href="/video/resource/index?dbId=${movie.dbId?c!''}&imdbId=${movie.imdbId!''}&chosen=true"
                                   target="_blank" title="show all links">Links</a>
                            </td>
                            <td style="width: 200px">
                                <#if movie.archived>
                                    Archived
                                <#else>
                                    <span class="click-tip" hidden="hidden"></span>
                                    <a href="javascript:void(0);" class="movie-archive checked" data-id="${movie.id?c}"
                                       title="click to archive"> ${movie.status.text} </a>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </li>
        <li class="layui-tab-item">
            <div>
                <table id="series" class="layui-table">
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Title</th>
                        <th>Resources</th>
                        <th>Season</th>
                        <th>Year</th>
                        <th>Episodes</th>
                        <th>Durations</th>
                        <th>Video</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list tvs?sort_by('unarchived')?reverse as tv>
                        <#assign series=tv.series>
                        <#assign seasons=tv.seasons?sort_by('currentSeason')>
                        <tr>
                            <#assign rows=(seasons?size)+1>
                            <td rowspan="${rows}">${tv_index+1}</td>
                            <td rowspan="${rows}">
                                <a href="https://imdb.cn/title/${series.imdbId}/" target="_blank">${series.title}</a>
                            </td>
                            <td rowspan="${rows}">
                                <a href="javascript:void(0);" class="series-search" data-id="${series.id?c}"
                                   title="click to search">Search</a>
                                /
                                <a href="javascript:void(0);" class="series-input" data-id="${series.id?c}"
                                   title="click to search with a key">Input</a>
                                /
                                <a href="/video/resource/index?imdbId=${series.imdbId}&chosen=true" target="_blank"
                                   title="show all links">Links</a>
                            </td>
                        </tr>
                        <#list seasons as season>
                            <tr>
                                <td>
                                    <a href="https://movie.douban.com/subject/${season.dbId?c}/"
                                       target="_blank">${season.title}</a>
                                </td>
                                <td>${season.year?c}</td>
                                <td>${season.episodesCount?c}</td>
                                <td>${season.durations}</td>
                                <td style="width: 300px">
                                    <#if season.archived>
                                        Archived
                                    <#else>
                                        <a href="/video/resource/index?dbId=${season.dbId?c!''}&chosen=true"
                                           target="_blank" title="show all links">Links</a>
                                        /
                                        <span class="click-tip" hidden="hidden"></span>
                                        <a href="javascript:void(0);" class="season-archive checked"
                                           data-id="${season.id?c}" title="click to archive"> ${season.status.text} </a>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                    </#list>
                    </tbody>
                </table>
            </div>
        </li>
    </ul>
</div>
</body>
</html>