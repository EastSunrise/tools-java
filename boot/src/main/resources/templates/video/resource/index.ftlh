<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Resources</title>
    <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div>
    <div>
        <form class="layui-form" action="/video/resource/index" method="post">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label" for="key"> KEY </label>
                    <div class="layui-input-inline">
                        <input type="text" id="key" name="key" required placeholder="Please input a key"
                               value="${query.key!''}" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label" for="dbId" style="width: 100px"> ID of Douban </label>
                    <div class="layui-input-inline">
                        <input type="text" id="dbId" name="dbId" required placeholder="Please input an ID of Douban"
                               value="${query.dbId?c!''}" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label" for="imdbId"> ID of IMDb </label>
                    <div class="layui-input-inline">
                        <input type="text" id="imdbId" name="imdbId" required placeholder="Please input an ID of IMDb"
                               value="${query.imdbId!''}" autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div>
        <div class="layui-collapse">
            <#list links?keys as type>
                <div class="layui-colla-item">
                    <h2 class="layui-colla-title"> ${type} </h2>
                    <div class="layui-colla-content layui-show">
                        <ul>
                            <#list links[type]?sort_by("url") as link>
                                <li>
                                    ${link_index+1}. <a href="${link.url}">${link.title}</a>
                                    /
                                    <a href="${link.thunder}">Thunder</a>
                                </li>
                            </#list>
                        </ul>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</div>
</body>
</html>