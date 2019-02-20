//控制层
app.controller('itemController', function ($scope) {
    $scope.addNum = function (x) {
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    $scope.specificationItems = {};

    $scope.selectSpecificationItem = function (key, value) {
        $scope.specificationItems[key] = value;
        searchSku();
    };

    $scope.isSelected = function (key, value) {
        return $scope.specificationItems[key] == value;
    };

    $scope.sku = {};

    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify(skuList[0].spec));
    };

    var matchObject = function (map1, map2) {
        for (var key1 in map1) {
            if (map1[key1] != map2[key1]) {
                return false;
            }
        }
        for (var key2 in map2) {
            if (map2[key2] != map1[key2]) {
                return false;
            }
        }
        return true;
    };

    var searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject($scope.specificationItems, skuList[i].spec)) {
                $scope.sku = skuList[i];
                return;
            }
        }
    };

    $scope.addToCart = function () {
        alert("skuid" + $scope.sku.id);
    }
});
