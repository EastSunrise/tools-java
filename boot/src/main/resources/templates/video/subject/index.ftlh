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
                        <th>Language</th>
                        <th>Durations</th>
                        <th>Resources</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list movies as movie>
                        <tr>
                            <td>${movie_index+1}</td>
                            <td>
                                <a href="https://movie.douban.com/subject/${movie.dbId?c}" target="_blank">${movie.title}</a>
                                <a href="https://www.baidu.com/s?wd=${movie.title}" target="_blank"><i class="layui-icon layui-icon-search"></i></a>
                            </td>
                            <td>${movie.year?c}</td>
                            <td>${(movie.languages[0].title)!""}</td>
                            <td>${movie.durations}</td>
                            <td>
                                <#assign sc = movie.status.code/>
                                <a href="javascript:void(0);" class="movie-search" data-id="${movie.id?c}"
                                   title="Query resources with the title"><i class="layui-icon layui-icon-search"></i></a>
                                &nbsp;
                                <#if sc == 30>
                                    <a href="javascript:void(0);" class="movie-input" data-id="${movie.id?c}"
                                       title="Query resources with input key"><i class="layui-icon layui-icon-edit"></i> </a>
                                    &nbsp;
                                    <a href="/video/resource/index?dbId=${movie.dbId?c!''}&imdbId=${movie.imdbId!''}&chosen=true" target="_blank"
                                       title="Choose resources to download"><i class="layui-icon layui-icon-link"></i> </a>
                                <#elseif sc==40>
                                    <span class="click-tip" hidden="hidden"></span>
                                    <a href="javascript:void(0);" class="movie-archive link" data-id="${movie.id?c}"
                                       title="Archived downloaded resources"> Archive </a>
                                <#elseif sc==20>
                                    <a href="javascript:void(0);" class="movie-open" data-id="${movie.id?c}" title="Click to open the video"><i
                                                class="layui-icon layui-icon-video"></i> </a>
                                <#else>
                                    ${movie.status.text}
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
                        <th>Language</th>
                        <th>Resources</th>
                        <th>Season</th>
                        <th>Year</th>
                        <th>Episodes</th>
                        <th>Durations</th>
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
                                <a href="https://imdb.cn/title/${series.imdbId}/" target="_blank">${series.title}</a>
                            </td>
                            <td rowspan="${rows}">${(series.languages[0].title)!""}</td>
                            <td rowspan="${rows}">
                                <a href="javascript:void(0);" class="series-search" data-id="${series.id?c}">Query</a>
                                /
                                <a href="javascript:void(0);" class="series-input" data-id="${series.id?c}">Input</a>
                            </td>
                        </tr>
                        <#list seasons as season>
                            <tr>
                                <td>
                                    <a href="https://movie.douban.com/subject/${season.dbId?c}/" target="_blank">${season.title}</a>
                                </td>
                                <td>${season.year?c}</td>
                                <td>${season.episodesCount?c}</td>
                                <td>${season.durations}</td>
                                <td>
                                    <#assign sc = season.status.code/>
                                    <#if sc == 30>
                                        <a href="/video/resource/index?dbId=${season.dbId?c!''}&chosen=true" target="_blank" class="link"
                                           title="Choose resources to download">Download</a>
                                    <#elseif sc==40>
                                        <span class="click-tip" hidden="hidden"></span>
                                        <a href="javascript:void(0);" class="season-archive link" data-id="${season.id?c}"
                                           title="Archived downloaded resources"> Archive </a>
                                    <#else>
                                        ${season.status.text}
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