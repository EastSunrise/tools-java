/**
 * Handle result of Ajax request.
 * @param options options of ajax, add 'callback' function to handle the result if necessary.
 */
function ajaxResult(options) {
    $.extend(options, {
        dataType: 'json',
        success: function (result) {
            if (!result['success']) {
                alert(result['message']);
            } else {
                let callback = options['callback'];
                if (callback !== undefined && callback !== null) {
                    callback(result);
                }
            }
        },
        error: function () {
            alert('无法连接到服务器！');
        }
    });
    $.ajax(options);
}

/**
 * Handle async request.
 * Require a 'span' tag of '.clickTip' to show tips when executing request
 * and an 'a' tag to click
 * @param _this the 'a' tag
 * @param tip tip to show when requesting, required
 * @param options arguments of ajax request, add 'callback' to handle the result if necessary.
 * @param confirmMsg confirm message before requesting, optional
 */
function ajaxAsync(_this, tip, options, confirmMsg) {
    $(_this).after("<span class='clickTip'></span>");
    let span = _this.next('.clickTip');
    if (!confirmMsg || confirm(confirmMsg)) {
        _this.attr('hidden', true);
        span.text(tip);
        let spotCount = 0;
        let timer = setInterval(function () {
            let text = tip + '.';
            for (let i = 0; i < spotCount; i++) {
                text += '.';
            }
            span.text(text);
            if (spotCount === 2) {
                spotCount = 0;
            } else {
                spotCount++;
            }
        }, 500);
        $.extend(options, {
            dataType: 'json',
            success: function (result) {
                if (result['success']) {
                    let callback = options['callback'];
                    if (callback !== undefined && callback !== null) {
                        callback(result);
                    }
                } else {
                    alert(result['message']);
                }
            },
            error: function () {
                alert('无法连接到服务器！');
            },
            complete: function () {
                clearInterval(timer);
                _this.attr('hidden', false);
                span.remove();
            }
        });
        $.ajax(options);
    }
}