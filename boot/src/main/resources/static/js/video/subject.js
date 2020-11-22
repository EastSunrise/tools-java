$(function () {
    $('.movie-archive').on('click', function () {
        let a = $(this);
        let tip = a.prev('.click-tip');
        a.attr('hidden', true);
        tip.attr('hidden', false);

        let spotCount = 0;
        let timer = setInterval(function () {
            let text = 'Archiving.';
            for (let i = 0; i < spotCount; i++) {
                text += '.';
            }
            tip.text(text);
            if (spotCount === 2) {
                spotCount = 0;
            } else {
                spotCount++;
            }
        }, 500);

        let id = a.data('id');
        $.post('/video/movie/archive', {
            id: id
        }, function (status) {
            clearInterval(timer);
            if (status.code === 20) {
                a.remove();
                tip.text(status.text);
            } else {
                a.text(status.text);
                tip.attr('hidden', true);
                a.attr('hidden', false);
            }
        });
    });
});

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