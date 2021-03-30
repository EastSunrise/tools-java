<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Preview</title>
  <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
  <script src="/webjars/layui/2.5.6/layui.all.js"></script>
  <script src="/static/js/video/subject.js"></script>
  <link rel="stylesheet" href="/static/css/video.css">
  <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div id="lazy-images">
    <#list views as view>
      <h3 style="alignment: center;">${view.id}</h3>
        <#list view.images as image>
          <img style="alignment: center;" lay-src="${image}" alt="${view.id}"/>
        </#list>
    </#list>
</div>
<script>
  layui.use('flow', function () {
    let flow = layui.flow;
    flow.lazyimg({
      elem: '#lazy-images img'
    });
  });
</script>
</body>