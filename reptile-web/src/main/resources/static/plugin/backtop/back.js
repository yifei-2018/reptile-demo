// 浏览器窗口滚动条
$(window).scroll(function () {
    //动态控制右侧边栏的隐藏显示
    if ($(this).scrollTop() > 10) {
        $("#go-top").css({
            "right": "2px"
        });
    } else {
        $("#go-top").css({
            "right": "-56px"
        });
    }
});

var BackTop = {
    /**
     * 添加回顶部
     */
    add: function () {
        var goTopHtml = '';
        goTopHtml += '<div id="go-top" class="go-top" title="回顶部">';
        goTopHtml += '	<a href="javascript:void(0)" onclick="BackTop.goTop(\'html\');"></a>';
        goTopHtml += '</div>';
        $("body").append(goTopHtml);
    },
    /**
     * 回顶部
     * @param {string} dom 选择器
     */
    goTop: function (dom) {
        $(dom).stop().animate({scrollTop: 0}, 500);
    }
};
