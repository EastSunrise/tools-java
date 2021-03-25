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
<div>
  <table id="series" class="layui-table">
    <thead>
    <tr>
      <th>No.</th>
      <th>Title</th>
      <th>Season</th>
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
        <td hidden="hidden" id="${series.id?c}"></td>
          <#assign rows=(seasons?size)+1>
        <td rowspan="${rows}">${tv_index+1}</td>
        <td rowspan="${rows}">
          <a href="https://imdb.cn/title/${series.imdbId}/"
             target="_blank">${series.zhTitle}</a>
          &nbsp;
          <a class="layui-icon layui-icon-search"
             href="https://www.baidu.com/s?wd=${series.zhTitle}" target="_blank"
             title="Search by Baidu"></a>
          <span class="link series-search layui-icon layui-icon-search"
                title="Query resources with the title"></span>
          <span class="link series-input layui-icon layui-icon-edit"
                title="Query resources with input key"></span>
        </td>
      </tr>
        <#list seasons as season>
          <tr>
            <td hidden="hidden" id="${season.id?c}"></td>
            <td>
              <a href="https://movie.douban.com/subject/${season.dbId?c}/"
                 target="_blank">${season.zhTitle} (${season.year?c})</a>
            </td>
            <td>${season.episodesCount?c}</td>
            <td>${season.durations}</td>
            <td>
              <input type="hidden" name="id" value="${season.id?c}">
                <#assign sc = season.status.code/>
                <#if sc == 30>
                  <a href="/video/resource/index?dbId=${season.dbId?c!''}&chosen=true"
                     target="_blank"
                     class="link layui-icon layui-icon-link"
                     title="Choose resources to download"></a>
                <#elseif sc==40>
                  <span class="click-tip" hidden="hidden"></span>
                  <i class="link video-archive" title="Archived downloaded resources">
                    Archive </i>
                <#elseif sc==20>
                  <span class="link video-open layui-icon layui-icon-video"
                        title="Open the video"></span>
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
</body>
</html>