/**
 * Handle result of Ajax request.
 */
function ajaxResult(options) {
    options["dataType"] = 'json';
    options["success"] = function (result) {
        if (!result['success']) {
            alert(result['message']);
        } else {
            options['success'](result);
        }
    };
    options['error'] = function () {
        alert('无法连接到服务器！');
        options['error']();
    };
    $.ajax(options);
}

/**
 * Handle async request.
 * Require a 'span' tag of '.clickTip' to show tips when executing request
 * and an 'a' tag to click
 * @param _this the 'a' tag
 * @param tip tip to show when requesting, required
 * @param options arguments of ajax request
 * @param confirmMsg confirm message before requesting, optional
 */
function ajaxAsync(_this, tip, options, confirmMsg) {
    let span = _this.prev('.clickTip');
    _this.off('click').on('click', function () {
        if (!confirmMsg || confirm(confirmMsg)) {
            _this.attr('hidden', true);
            span.text(tip);
            span.attr('hidden', false);
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
            options['dateType'] = 'json';
            options['success'] = function (result) {
                if (result['success']) {
                    options['success'](result);
                } else {
                    alert(result['message']);
                    _this.attr('hidden', false);
                    span.attr('hidden', true);
                }
            };
            options["error"] = function () {
                span.text('无法连接到服务器！');
                options['error']();
            };
            options['complete'] = function () {
                clearInterval(timer);
                options['complete']();
            };
            $.ajax(options);
        }
    });
}