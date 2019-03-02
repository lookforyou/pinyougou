app.service("payService", function ($http) {
    this.createNative = function () {
        return $http.get("../pay/createNative");
    };

    this.queryStatus = function (out_trade_no) {
        return $http.get("../pay/queryPayStatus?out_trade_no=" + out_trade_no);
    }
});