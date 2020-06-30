(function () {
    // submit form
    $('.filter').on('change', function () {
        $('#form').submit();
    });

    // play archived subject
    $('.archived').on('click', function () {
        let this_ = $(this);
        ajaxAsync(this_, '正在启动', {
            url: '/video/play',
            type: 'post',
            data: {
                'id': this_.data('id')
            }
        });
    });
})();

function updateInfo(id) {
    ajaxResult({
        url: "/video/updateInfo",
        type: "post",
        data: {
            'id': id
        },
        callback: function () {
            window.location.reload();
        }
    });
}

function update(id) {
    let imdbId = prompt("请输入IMDb ID: ") || '';
    if ('' === imdbId) {
        return;
    }
    ajaxResult({
        url: "/video/update",
        type: "post",
        data: {
            'id': id,
            'imdbId': imdbId
        },
        callback: function () {
            window.location.reload();
        }
    });
}

function updateMyMovies(this_) {
    let userId = prompt("请输入用户ID: ") || "";
    if ("" !== userId) {
        ajaxAsync($(this_), "正在更新", {
            url: "/video/user/collect",
            type: 'post',
            data: {
                'userId': userId
            },
            callback: function (result) {
                alert("已更新：" + result["record"]);
            }
        });
    }
}