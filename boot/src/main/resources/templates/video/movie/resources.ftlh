<div class="layui-fluid">
  <div>
    <table class="layui-table">
      <tr>
          <#if movie.dbId??>
            <td><a href="https://movie.douban.com/subject/${movie.dbId?c}/"
                   target="_blank">${movie.zhTitle}</a></td>
          </#if>
        <td>${movie.year?c}</td>
          <#if movie.imdbId??>
            <td><a href="https://imdb.cn/title/${movie.imdbId}/"
                   target="_blank">${(movie.enTitle)!"NO_TEXT"}</a></td>
          </#if>
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
            <th>Douban</th>
            <th>IMDb</th>
            <th>Check</th>
          </tr>
          </thead>
          <tbody>
          <#list items?sort_by('gmtModified')?reverse as item>
            <tr>
              <td>${item_index+1}</td>
              <td style="width: 400px;">
                  ${item.title}<#if item.year??>(${item.year?c})</#if>
                <a href="https://search.douban.com/movie/subject_search?search_text=${item.title}&cat=1002"
                   class="layui-icon layui-icon-search"
                   target="_blank"></a>
              </td>
              <td>
                  <#if item.dbId??>
                      <#if movie.dbId?? && item.dbId==movie.dbId>
                        <a href="https://movie.douban.com/subject/${movie.dbId?c}/" target="_blank">Matched</a>
                      <#else>
                        <a href="https://movie.douban.com/subject/${item.dbId?c}/" target="_blank"
                           class="conflict">Conflict</a>
                      </#if>
                  </#if>
              </td>
              <td>
                  <#if item.imdbId??>
                      <#if movie.imdbId?? && item.imdbId==movie.imdbId>
                        <a href="https://imdb.cn/title/${movie.imdbId}/" target="_blank">Matched</a>
                      <#else>
                        <a href="https://imdb.cn/title/${item.imdbId}/" target="_blank"
                           class="conflict">Conflict</a>
                      </#if>
                  </#if>
              </td>
              <td>
                  <#if !item.identified>
                    <label>
                      <input type="checkbox" class="resource-choose" data-id="${item.id?c}"
                             data-db-id="${(movie.dbId?c)!''}"
                             data-imdb-id="${movie.imdbId!''}">
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