/**
 * StringUitls工具类
 * @type {{isEmpty: StringUitls.isEmpty, defaultString: StringUitls.defaultString}}
 */
var StringUtils = {
    isEmpty: function (param) {
        if (param === undefined || param === null || param === "") {
            return true;
        }
        return false;
    },
    isNonNegFloatingNum: function (param) {
        // 非负浮点数
        var regPos = /^\d+(\.\d+)?$/;
        return regPos.test(param);
    },
    defaultString: function (param) {
        if (param === undefined || param === null || param.trim() === "") {
            return "";
        }
        return param + "";
    }
}