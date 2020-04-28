var Loading = {
    /**
     * 显示loading
     * @param {string} boxId 显示loading区域的id
     * @return {string} 加载框id
     */
    show: function (boxId) {
        var loadingId = 'loadingId_' + (new Date()).valueOf();
        var loadingLayerHtml = "";
        loadingLayerHtml += '<div id="' + loadingId + '" class="loading-layer">';
        loadingLayerHtml += '	<div id="loading-container" class="loading-container">';
        loadingLayerHtml += '		<div class="loading-box">';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '			<div class="loading-ball"></div>';
        loadingLayerHtml += '		</div>';
        loadingLayerHtml += '	</div>';
        loadingLayerHtml += '</div>';

        if (boxId === undefined) {
            $("body").append(loadingLayerHtml);
            return loadingId;
        }

        var loadingBox = $("#" + boxId);
        if (loadingBox.length === 0) {
            $("body").append(loadingLayerHtml);
        } else {
            loadingBox.append(loadingLayerHtml);
        }
        return loadingId;
    },
    /**
     * 关闭loading
     * @param {strings} loadingId 加载框id
     */
    close: function (loadingId) {
        $("#" + loadingId).hide().remove();
    }
};
