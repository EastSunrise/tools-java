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
                                <a href="https://movie.douban.com/subject/${movie.dbId?c}" target="_blank">${movie.title}
                                    <#if movie.originalTitle??>${movie.originalTitle}</#if> (${movie.year?c})</a>
                                <a class="layui-icon layui-icon-search" href="https://www.baidu.com/s?wd=${movie.title}" target="_blank"></a>
                            </td>
                            <td>${(movie.languages[0].title)!""}</td>
                            <td>${movie.durations}</td>
                            <td>
                                <input type="hidden" name="id" value="${movie.id?c}">
                                <#assign sc = movie.status.code/>
                                <i class="link movie-search layui-icon layui-icon-search" title="Query resources with the title"></i>
                                <i class="link movie-input layui-icon layui-icon-edit" title="Query resources with input key"></i>
                                &nbsp;
                                <#if sc == 30>
                                    <a href="/video/resource/index?dbId=${movie.dbId?c!''}&imdbId=${movie.imdbId!''}&chosen=true" target="_blank"
                                       class="link layui-icon layui-icon-link" title="Choose resources to download"></a>
                                <#elseif sc==40>
                                    <span class="click-tip" hidden="hidden"></span>
                                    <i class="link video-archive" title="Archived downloaded resources"> Archive </i>
                                <#elseif sc==20>
                                    <i class="video-open layui-icon layui-icon-video" title="Open the video"></i>
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
                                <input type="hidden" name="id" value="${series.id?c}">
                                <i class="link series-search layui-icon layui-icon-search" title="Query resources with the title"></i>
                                <i class="link series-input layui-icon layui-icon-edit" title="Query resources with input key"></i>
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
                                    <input type="hidden" name="id" value="${season.id?c}">
                                    <#assign sc = season.status.code/>
                                    <#if sc == 30>
                                        <a href="/video/resource/index?dbId=${season.dbId?c!''}&chosen=true" target="_blank"
                                           class="link layui-icon layui-icon-link" title="Choose resources to download"></a>
                                    <#elseif sc==40>
                                        <span class="click-tip" hidden="hidden"></span>
                                        <i class="link video-archive" title="Archived downloaded resources"> Archive </i>
                                    <#elseif sc==20>
                                        <i class="video-open layui-icon layui-icon-video" title="Open the video"></i>
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