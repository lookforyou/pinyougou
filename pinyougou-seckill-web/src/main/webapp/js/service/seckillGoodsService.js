app.service("seckillGoodsService", function ($http) {
    this.findSeckillGoods = function () {
        return $http.get("../seckillGoods/findSeckillGoods");
    };

    this.findSeckillGoodFormRedis = function (id) {
        return $http.get("../seckillGoods/findSeckillGoodFromRedis?id=" + id);
    };

    this.submitOrder = function (seckillGoodId) {
        return $http.get("../seckillOrder/submitOrder?seckillGoodId=" + seckillGoodId);
    }
});