/**
 * Search resources of the movie of the given id
 * @param id
 */
function searchMovie(id) {
    $.get("/video/movie/" + id + "/resources", function (result) {
        layui.layer.confirm(result, {
            title: "Resources",
            area: '1200px',
        }, function (index) {
            let checks = [];
            $.each($('input.resource-choose'), function () {
                if (this.checked) {
                    let dbId = $(this).data('dbId') || "";
                    let imdbId = $(this).data('imdbId') || "";
                    checks.push({
                        url: $(this).data('url'),
                        dbId: dbId === "" ? null : dbId,
                        imdbId: imdbId === "" ? null : imdbId
                    });
                }
            })
            checkResources(checks, index);
        })
    })
}

/**
 * Search resources of the series of the given id
 * @param id
 */
function searchSeries(id) {
    $.get("/video/series/" + id + "/resources", function (result) {
        layui.layer.confirm(result, {
            title: "Resources",
            area: '1200px',
        }, function (index) {
            let checks = [];
            $.each($('select.resource-choose'), function () {
                let value = $(this).val() || "";
                if (value !== "") {
                    checks.push({
                        url: $(this).data('url'),
                        dbId: value
                    });
                }
            })
            checkResources(checks, index);
        })
    })
}

function checkResources(checks, index) {
    if (checks.length === 0) {
        layui.layer.alert("None checked!");
        layui.layer.close(index);
        return;
    }
    $.ajax('/video/resource/check', {
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify(checks),
        success: function (count) {
            layui.layer.alert("Checked: " + count);
            layui.layer.close(index);
        }
    })
}