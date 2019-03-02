app.controller("payController", function ($scope, $location, payService) {
    $scope.createNative = function () {
        payService.createNative().success(function (data) {
            $scope.money = (data.total_fee / 100).toFixed(2);
            $scope.out_trade_no = data.out_trade_no;
            var qri= new QRious({
                element: document.getElementById("qrious"),
                size: 250,
                value: data.code_url,
                level: "H"
            });
            queryStatus();
        });
    };
    var queryStatus = function () {
        payService.queryStatus($scope.out_trade_no).success(function (data) {
            if (data.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {
                if (data.msg == "二维码过期") {
                    $scope.createNative();
                } else {
                    location.href = "payfail.html";
                }
            }
        })
    };

    $scope.getMoney = function () {
        return $location.search()["money"];
    }
});