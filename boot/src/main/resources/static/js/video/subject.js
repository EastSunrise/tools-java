$(function () {
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
    $('.movie-archive').on('click', function () {
        archive($(this), '/video/movie/archive');
    });
    $('.series-search').on('click', function () {
        search("/video/series/resources", $(this).data('id'));
    });
    $('.series-input').on('click', function () {
        let id = $(this).data('id');
        layui.layer.prompt(function (value, index) {
            layui.layer.close(index);
            search("/video/series/resources", id, value);
        })
    });
    $('.season-archive').on('click', function () {
        archive($(this), '/video/season/archive');
    });
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
                        id: $(this).data('id'),
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
                            id: $(this).data('id'),
                            imdbId: value
                        })
                    } else {
                        checks.push({
                            id: $(this).data('id'),
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

/**
 * Archive a subject
 * @param ele current element
 * @param url url to post
 */
function archive(ele, url) {
    if (!confirm('Are all files chosen?')) {
        return;
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
        },
        'success': function (status) {
            clearInterval(timer);
            ele.remove();
            tip.text(status.text);
            if (status.code !== ARCHIVED_CODE) {
                alert('Failed to archive the subject: ' + (status.text.toUpperCase()));
            }
        },
        error: function (xhr) {
            clearInterval(timer);
            tip.text("ERROR");
            layui.layer.alert(xhr.responseText);
        }
    });
}