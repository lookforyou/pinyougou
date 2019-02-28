app.service("cartService", function ($http) {
    this.findCarts = function () {
        return $http.get("../cart/findCarts");
    };

    this.addGoodsToCarts = function (itemId, num) {
        return $http.get("../cart/addGoodsToCarts?itemId=" + itemId + "&num=" + num);
    };

    this.sum = function (carts) {
        var totalValue = {totalNum : 0, totalMoney : 0};
        for (var i = 0; i < carts.length; i++) {
            for (var j = 0; j < carts[i].orderItems.length; j++) {
                totalValue.totalNum += carts[i].orderItems[j].num;
                totalValue.totalMoney += carts[i].orderItems[j].totalFee;
            }
        }
        return totalValue;
    };

    this.findAddressListByUserId = function () {
        return $http.get("../address/findAddressListByUserId");
    };

    this.add = function (address) {
        return $http.post("../address/add", address);
    };

    this.orderAdd = function (order) {
        return $http.post("../order/add", order);
    };
});