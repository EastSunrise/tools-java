<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Resources</title>
    <link rel="stylesheet" href="/webjars/layui/2.5.6/css/layui.css">
</head>
<body>
<div class="layui-header">
    <form class="layui-form" action="/video/resource/index" method="get">
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label" for="key"> KEY </label>
                <div class="layui-input-inline">
                    <input type="text" id="key" name="key" placeholder="Please input a key" value="${query.key!''}" autocomplete="off"
                           class="layui-input">
                </div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label" for="dbId" style="width: 100px"> ID of Douban </label>
                <div class="layui-input-inline">
                    <input type="text" id="dbId" name="dbId" placeholder="Please input an ID of Douban" value="${(query.dbId?c)!''}"
                           autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label" for="imdbId"> ID of IMDb </label>
                <div class="layui-input-inline">
                    <input type="text" id="imdbId" name="imdbId" placeholder="Please input an ID of IMDb" value="${(query.imdbId)!''}"
                           autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-inline">
                <div class="layui-input-inline">
                    <button class="layui-btn" lay-submit lay-filter="resourceForm">Submit</button>
                </div>
            </div>
        </div>
    </form>
    <script>
        layui.use('form', function () {
            let form = layui.form;
            form.on('submit(resourceForm)', function (data) {
                layer.msg(JSON.stringify(data.field));
                return false;
            });
        });
    </script>
</div>
<div>
    <div class="layui-collapse">
        <#assign linkCount=0/>
        <#list resources as resource>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">
                    ${resource_index+1}. ${resource.title}
                    <a href="https://search.douban.com/movie/subject_search?search_text=${resource.title}&cat=1002" class="layui-icon layui-icon-search"
                       target="_blank"></a>
                </h2>
                <#if resource.links??>
                    <div class="layui-colla-content layui-show">
                        <table class="layui-table" style="width: 100%">
                            <#list resource.links as link>
                                <tr>
                                    <#assign linkCount=linkCount+1/>
                                    <td>${linkCount}</td>
                                    <td style="width: 600px"><a href="${link.url}">${(link.filename)!""}</td>
                                    <td>
                                        <#if link.length??>${(link.length/1024)?round} KB</#if>
                                    </td>
                                    <td><a href="${link.thunder}">Thunder</a></td>
                                </tr>
                            </#list>
                        </table>
                    </div>
                </#if>
            </div>
        </#list>
    </div>
</div>
</body>
</html>