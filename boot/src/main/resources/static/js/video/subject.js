$(function () {
    $('.movie-search').on('click', function () {
        search("/video/resource/movie", getId($(this)));
    });
    $('.movie-input').on('click', function () {
        let id = getId($(this));
        layui.layer.prompt(function (value, index) {
            layui.layer.close(index);
            search("/video/resource/movie", id, value);
        })
    });
    $('.series-search').on('click', function () {
        search("/video/resource/series", getId($(this)));
    });
    $('.series-input').on('click', function () {
        let id = getId($(this));
        layui.layer.prompt(function (value, index) {
            layui.layer.close(index);
            search("/video/resource/series", id, value);
        })
    });

    $('.video-archive').on('click', function () {
        archive($(this), getId($(this)));
    });

    $('.video-open').on('click', function () {
        $.post('/video/open', {
            id: getId($(this))
        });
    })
});

function getId(_this) {
    return _this.parentsUntil("tbody", "tr").children(":first").attr("id");
}

/**
 * Search resources of the given key
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

const ARCHIVED = 'Archived';

/**
 * Archive a subject
 * @param ele current element
 * @param id
 */
function archive(ele, id) {
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

    $.ajax('/video/subject/archive', {
        type: 'POST',
        data: {
            id: id,
        },
        'success': function (status) {
            clearInterval(timer);
            ele.remove();
            tip.text(status);
            if (status !== ARCHIVED) {
                alert('Failed to archive the subject: ' + (status.toUpperCase()));
            }
        },
        error: function (xhr) {
            clearInterval(timer);
            tip.text("ERROR");
            layui.layer.alert(xhr.responseText);
        }
    });
}