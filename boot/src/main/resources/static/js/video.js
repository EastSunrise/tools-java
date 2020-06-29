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