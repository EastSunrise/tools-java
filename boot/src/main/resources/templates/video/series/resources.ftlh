<div class="layui-fluid">
  <div>
    <table class="layui-table">
      <tr>
        <td>${series.zhTitle}</td>
        <td><a href="https://imdb.cn/title/${series.imdbId}/" target="_blank">${series.enTitle}</a>
        </td>
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
        <th>Title</th>
          <#list seasons as season>
            <td><a href="https://movie.douban.com/subject/${season.dbId?c}/"
                   target="_blank">${season.zhTitle}</a></td>
          </#list>
      </tr>
      <tr>
        <th>Year</th>
          <#list seasons as season>
            <td>${season.year?c}</td>
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
            <th lay-data="{type:'checkbox',width:50}">Check</th>
          </tr>
          </thead>
          <tbody>
          <#list items?sort_by('gmtModified')?reverse as item>
            <tr>
              <td>${item_index+1}</td>
              <td>
                <span>${item.title}</span>
                <a href="https://search.douban.com/movie/subject_search?search_text=${item.title}&cat=1002"
                   class="layui-icon layui-icon-search"
                   target="_blank"></a>
              </td>
              <td>
                  <#if item.year??>${item.year?c}</#if>
              </td>
              <td>
                  <#if !item.identified>
                    <label>
                        <#if seasons?size==1>
                          <input type="checkbox" class="resource-choose" data-id="${item.id?c}"
                                 data-db-id="${seasons?first.dbId?c}">
                        <#else>
                          <select class="resource-choose" data-id="${item.id?c}"
                                  style="width: 100px;">
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
        <h3 style="text-align: center;">None Resources!</h3>
      </#if>
  </div>
</div>