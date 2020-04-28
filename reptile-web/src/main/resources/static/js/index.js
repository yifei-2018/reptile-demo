$(function () {
    BackTop.add();
});

$('#qryStr').bind('keydown', function (event) {
    if (event.keyCode == "13") {
        // 回车执行查询
        $('#searchBtn').click();
    }
});

/**
 * 查询
 */
function query() {
    // 校验
    if (!checkBeforeQry()) {
        return;
    }

    var qryParam = new QryParam();
    qryParam.searchStr = $('#qryStr').val();

    // 获取信息
    var loadingId = Loading.show();
    getHospitalInfoList(qryParam, function (data) {
        // 处理数据
        handleQryResult(data);
        Loading.close(loadingId);
    }, function (jqXHR) {
        $('#contentTableContainer').show();
        $('#contentTable tbody').html('<tr><td colspan="6">查询异常，请确认输入的检测词是否正确</td></tr>');
        $('#resultCount').html(0);
        Loading.close(loadingId);
        alert("检索异常，请重新检索！");
    })
}

/**
 * 下载
 */
function downloadFile() {
    var downloadUrl = ctxPath + '/index/downloadReptileFile/' + $('#qryStr').val();
    var downloadFormElem = $('#downloadForm');
    if (downloadFormElem.length < 1) {
        var downloadFormHtml = '<form id="downloadForm" method="get" action="' + downloadUrl + '"></form>';
        $('body').append(downloadFormHtml);
    }
    $('#downloadForm').submit();
}

/**
 * 查询前校验
 * @return {boolean}
 */
function checkBeforeQry() {
    if (StringUtils.isEmpty($('#qryStr').val())) {
        alert("检索词不能为空！");
        $('#qryStr').focus();
        return false;
    }
    return true;
}

/**
 * 获取医院信息
 * @param {QryParam} qryParam 查询参数
 * @param {function} successCallback 成功回调
 * @param {function} errorCallback 异常回调
 */
function getHospitalInfoList(qryParam, successCallback, errorCallback) {
    $.ajax({
        type: "GET",
        url: ctxPath + "/index/getHospitalInfoList",
        data: qryParam,
        dataType: "json",
        success: function (data) {
            if (successCallback) {
                successCallback(data);
            }
        },
        error: function (jqXHR) {
            console.error("发生错误：" + jqXHR.status);
            if (errorCallback) {
                errorCallback(jqXHR);
            }
        }
    });
}

/**
 * 处理查询结果
 * @param {array} dataList 数据集
 */
function handleQryResult(dataList) {
    var appendTrHtml = '';
    var appendTdHtml = '';
    var dataObj;
    var listLength = dataList.length;
    for (var i = 0; i < listLength; i++) {
        dataObj = dataList[i];
        appendTdHtml = '';
        appendTdHtml += '<td>' + (i + 1) + '</td>';
        appendTdHtml += '<td><a href="' + StringUtils.defaultString(dataObj.detailUrl) + '" target="_blank">' + StringUtils.defaultString(dataObj.name) + '</a></td>';
        appendTdHtml += '<td>' + StringUtils.defaultString(dataObj.provinceName) + '</td>';
        appendTdHtml += '<td>' + StringUtils.defaultString(dataObj.cityName) + '</td>';
        appendTdHtml += '<td>' + StringUtils.defaultString(dataObj.districtName) + '</td>';
        appendTdHtml += '<td>' + StringUtils.defaultString(dataObj.streetName) + '</td>';
        appendTrHtml += ('<tr>' + appendTdHtml + '</tr>');
    }
    $('#contentTable tbody').html(appendTrHtml);
    $('#resultCount').html(listLength);
    $('#contentTableContainer').show();
}

/**
 * 查询参数类
 */
var QryParam = function () {
    // 检索词
    searchStr: ""
};
