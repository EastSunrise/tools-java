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
      <th>Language</th>
      <th>Durations</th>
      <th>Resources</th>
    </tr>
    </thead>
    <tbody>
    <#list movies as movie>
      <tr>
        <td hidden="hidden" id="${movie.id?c}" title="${movie.zhTitle}"></td>
        <td>${movie_index+1}</td>
        <td>
          <a href="https://movie.douban.com/subject/${movie.dbId?c}"
             target="_blank">${movie.zhTitle}
              <#if movie.originalTitle??>${movie.originalTitle}</#if> (${movie.year?c})</a>
          &nbsp;
          <a class="layui-icon layui-icon-search"
             href="https://www.baidu.com/s?wd=${movie.zhTitle}" target="_blank"
             title="Search by Baidu"></a>
          <span class="link movie-search layui-icon layui-icon-search"
                title="Query resources with the title"></span>
          <span class="link movie-input layui-icon layui-icon-edit"
                title="Query resources with input key"></span>
        </td>
        <td>${(movie.languages[0].title)!""}</td>
        <td>${movie.durations}</td>
        <td>
            <#assign sc = movie.status.code/>
            <#if sc == 30>
              <a href="/video/resource/index?dbId=${movie.dbId?c!''}&imdbId=${movie.imdbId!''}&chosen=true"
                 target="_blank"
                 class="link layui-icon layui-icon-link"
                 title="Choose resources to download"></a>
            <#elseif sc==40>
              <span class="click-tip" hidden="hidden"></span>
              <i class="link video-archive" title="Archived downloaded resources">
                Archive </i>
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
</body>
</html>