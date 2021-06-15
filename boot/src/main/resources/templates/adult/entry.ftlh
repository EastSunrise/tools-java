<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,
        user-scalable=no"/>
  <title>
      <#if current.serialNum??>${current.serialNum} - Entry<#else>Entry</#if>
  </title>
  <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
  <script src="/webjars/layui/2.5.6/layui.all.js"></script>
  <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div style="float: left; position: fixed; left: 0;">
    <#if previous??>
      <a href="/adult/entry?id=${previous.id?c}" title="Previous">
        <i class="layui-icon layui-icon-prev"></i>
      </a>
    </#if>
</div>
<div style="float: right; position: fixed; right: 0;">
    <#if next??>
      <a href="/adult/entry?id=${next.id?c}" title="Next" onkeypress="function f(e) {
        e.key
      }">
        <i class="layui-icon layui-icon-next"></i>
      </a>
    </#if>
</div>
<div style="text-align: center;">
    <#if current.cover??>
      <div>
        <img src="${current.cover}" alt="${current.serialNum!""}">
      </div>
    </#if>
  <div>
    <ul>
      <li>Serial Number: ${current.serialNum!"Unknown"}</li>
      <li>Title: ${current.title!"Unknown"}</li>
      <li>Mosaic: ${(current.hasMosaic()!"Unknown")?string("yes","no")}</li>
      <li>Duration: ${current.duration!"Unknown"}</li>
    </ul>
  </div>
  <div>
    <ul>
        <#list current.images as image>
          <li><img src="${image}" alt="${current.serialNum!""}"></li>
        </#list>
    </ul>
  </div>
</div>
</body>