$(function () {
    $('.movie-archive').on('click', function () {
        archive($(this), '/video/movie/archive')
    });
    $('.movie-search').on('click', function () {
        searchMovie($(this).data('id'));
    });
    $('.movie-download').on('click', function () {
        download($(this), '/video/movie/download')
    })
    $('.season-archive').on('click', function () {
        archive($(this), '/video/season/archive')
    });
    $('.series-search').on('click', function () {
        searchSeries($(this).data('id'));
    });
    $('.season-download').on('click', function () {
        download($(this), '/video/season/download')
    })
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
            scrollbar: false
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
            scrollbar: false
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

/**
 * Check resources: link resources to specified identifiers.
 * @param checks
 * @param index
 */
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

/**
 * Archive a subject
 * @param ele current element
 * @param url url to post
 */
function archive(ele, url) {
    let tip = ele.prev('.click-tip');
    ele.attr('hidden', true);
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

    $.post(url, {id: ele.data('id')}, function (status) {
        clearInterval(timer);
        if (status.code === 20) {
            ele.remove();
            tip.text(status.text);
        } else {
            ele.text(status.text);
            tip.attr('hidden', true);
            ele.attr('hidden', false);
        }
    });
}

/**
 * Download the given subject.
 * @param ele
 * @param url
 */
function download(ele, url) {
    let id = ele.data('id');
    $.post(url, {id: id}, function (count) {
        if (count === 0) {
            layui.layer.alert("None added.");
        } else {
            layui.layer.alert(count + ' added to download.');
        }
    })
}