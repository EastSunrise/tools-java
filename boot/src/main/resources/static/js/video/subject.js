$(function () {
    $('.movie-archive').on('click', function () {
        archive($(this), '/video/movie/archive');
    });
    $('.movie-search').on('click', function () {
        search("/video/movie/resources", $(this).data('id'));
    });
    $('.movie-input').on('click', function () {
        let id = $(this).data('id');
        layui.layer.prompt(function (value, index) {
            layui.layer.close(index);
            search("/video/movie/resources", id, value);
        })
    });
    $('.movie-download').on('click', function () {
        download($(this), '/video/movie/download')
    })
    $('.season-archive').on('click', function () {
        archive($(this), '/video/season/archive');
    });
    $('.series-search').on('click', function () {
        search("/video/series/resources", $(this).data('id'));
    });
    $('.series-download').on('click', function () {
        download($(this), '/video/series/download')
    })
    $('.series-input').on('click', function () {
        let id = $(this).data('id');
        layui.layer.prompt(function (value, index) {
            layui.layer.close(index);
            search("/video/series/resources", id, value);
        })
    });
    $('.season-download').on('click', function () {
        download($(this), '/video/season/download')
    })
});

/**
 * Search resources of the series of the given id
 * @param url
 * @param id
 * @param key
 */
function search(url, id, key) {
    $.post(url, {
        id: id,
        key: key
    }, function (result) {
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
            $.each($('select.resource-choose'), function () {
                let value = $(this).val() || "";
                if (value !== "") {
                    if (value.startsWith("tt")) {
                        checks.push({
                            url: $(this).data('url'),
                            imdbId: value
                        })
                    } else {
                        checks.push({
                            url: $(this).data('url'),
                            dbId: value
                        });
                    }
                }
            })
            if (checks.length === 0) {
                layui.layer.close(index);
                return;
            }
            $.ajax('/video/resource/check', {
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(checks),
                'success': function (count) {
                    layui.layer.alert("Checked: " + count);
                    layui.layer.close(index);
                }
            })
        })
    })
}

const ARCHIVED_CODE = 20;
const TO_ARCHIVE_CODE = 32;

/**
 * Archive a subject
 * @param ele current element
 * @param url url to post
 */
function archive(ele, url) {
    let chosen = false;
    if (ele.data('toArchive')) {
        if (!confirm('Are files chosen?')) {
            return;
        } else {
            chosen = true;
        }
    }

    let tip = ele.prev('.click-tip');
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
    ele.attr('hidden', true);
    tip.attr('hidden', false);

    $.ajax(url, {
        type: 'POST',
        data: {
            id: ele.data('id'),
            chosen: chosen
        },
        'success': function (status) {
            clearInterval(timer);
            if (status.code === ARCHIVED_CODE) {
                ele.remove();
                tip.text(status.text);
            } else {
                if (status.code === TO_ARCHIVE_CODE) {
                    ele.data('toArchive', true);
                }
                ele.text(status.text);
                tip.attr('hidden', true);
                ele.attr('hidden', false);
            }
        },
        error: function (xhr, status, msg) {
            layui.layer.alert(msg);
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