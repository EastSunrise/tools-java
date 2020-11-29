<div class="layui-fluid">
    <div>
        <table class="layui-table">
            <tr>
                <td>${series.title}</td>
                <#if series.imdbId??>
                    <td>
                        <a href="https://imdb.cn/title/${series.imdbId}/" target="_blank">${series.text}</a>
                    </td>
                </#if>
            </tr>
        </table>
        <table class="layui-table">
            <tr>
                <th>Season</th>
                <#list seasons as season>
                    <td>${season.currentSeason}</td>
                </#list>
            </tr>
            <tr>
                <th>Douban</th>
                <#list seasons as season>
                    <td>
                        <a href="https://movie.douban.com/subject/${season.dbId?c}/" target="_blank">${season.title}</a>
                    </td>
                </#list>
            </tr>
            <tr>
                <th>Year</th>
                <#list seasons as season>
                    <td>${season.year}</td>
                </#list>
            </tr>
        </table>
    </div>
    <div>
        <#if items?size gt 0>
            <table class="layui-table">
                <thead>
                <tr>
                    <th>No.</th>
                    <th>Title</th>
                    <th>Year</th>
                    <th>Type</th>
                    <th lay-data="{type:'checkbox',width:50}">Check</th>
                </tr>
                </thead>
                <tbody>
                <#list items?sort_by('gmtModified')?reverse as item>
                    <tr>
                        <td>${item_index+1}</td>
                        <td>
                            <a href="${item.url}" target="_blank">${item.title}</a>
                        </td>
                        <td>
                            <#if item.year??>${item.year?c}</#if>
                        </td>
                        <td>${item.videoType}</td>
                        <td>
                            <#if !item.identified>
                                <label>
                                    <#if seasons?size==1>
                                        <input type="checkbox" class="resource-choose" data-url="${item.url}"
                                               data-db-id="${seasons?first.dbId?c}">
                                    <#else>
                                        <select class="resource-choose" data-url="${item.url}" style="width: 100px">
                                            <option value="">--Choose--</option>
                                            <option value="${series.imdbId}">mixed</option>
                                            <#list seasons as season>
                                                <option value="${season.dbId?c}">${season.currentSeason}</option>
                                            </#list>
                                        </select>
                                    </#if>
                                </label>
                            <#else>
                                <span class="checked">CHECKED</span>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        <#else>
            <h3 style="text-align: center">None Resources!</h3>
        </#if>
    </div>
</div>