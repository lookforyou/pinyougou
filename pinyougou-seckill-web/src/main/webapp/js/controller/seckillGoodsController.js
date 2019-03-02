app.controller("seckillGoodsController", function ($scope, $location, $interval, seckillGoodsService) {
    $scope.findSeckillGoods = function () {
        seckillGoodsService.findSeckillGoods().success(function (data) {
            $scope.list = data;
        })
    };

    $scope.findSeckillGoodFormRedis = function () {
        var id = $location.search()["id"];
        seckillGoodsService.findSeckillGoodFormRedis(id).success(function (data) {
            $scope.seckillGood = data;
            var allSecond = Math.floor((new Date($scope.seckillGood.endTime).getTime() - new Date().getTime()) / 1000);
            var time = $interval(function () {
                allSecond = allSecond - 1;
                $scope.time = convertSecond(allSecond);
                if (allSecond <= 0) {
                    $interval.cancel(time);
                }
            }, 1000);
        })
    };

    /**
     * 秒转换
     * @param allSecond
     * @returns {string}
     */
    var convertSecond = function (allSecond) {
        var days = Math.floor(allSecond / (60 * 60 * 24));//计算天
        var hours = Math.floor((allSecond - days * 24 * 60 * 60) / (60 * 60));//计算小时
        var minutes = Math.floor((allSecond - days * 24 * 60 * 60 - hours * 60 * 60) / 60);//计算分钟
        var second = allSecond - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60;//计算秒
        var daysStr = "";
        if (days > 0) {
            daysStr = days + "天 ";
        }
        var hoursStr = hours + "";
        if (hours < 10) {
            hoursStr = "0" + hours;
        }
        var minutesStr = minutes + "";
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        }
        var secondStr = second + "";
        if (second < 10) {
            secondStr = "0" + second;
        }
        return daysStr + hoursStr + ":" + minutesStr + ":" + secondStr;
    };

    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.seckillGood.id).success(function (data) {
            if (data.success) {
                alert("下单成功，请在5分钟之内完成支付！");
                location.href = "pay.html";
            } else {
                alert(data.msg);
            }
        })
    }
});