app.controller("cartController", function ($scope, cartService) {
    $scope.findCarts = function () {
        cartService.findCarts().success(function (data) {
            $scope.carts = data;
            $scope.totalValue = cartService.sum($scope.carts);
        })
    };

    $scope.addGoodsToCarts = function (itemId, num) {
        cartService.addGoodsToCarts(itemId, num).success(function (data) {
            if (data.success) {
                $scope.findCarts();
            } else {
                alert(data.msg);
            }
        })
    };

    $scope.findAddressListByUserId = function () {
        cartService.findAddressListByUserId().success(function (data) {
            $scope.addressList = data;
            for (var i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault == '1') {
                    $scope.address = $scope.addressList[i];
                    break;
                }
            }
        })
    };

    $scope.selectedAddress = function (address) {
        $scope.address = address;
    };

    $scope.isSelected = function (address) {
        return $scope.address == address;
    };

    $scope.add = function () {
        cartService.add($scope.address).success(function (data) {
            if (data.success) {
                location.reload();
            } else {
                alert(data.msg);
            }
        })
    };

    $scope.changeAlias = function (alias) {
        $scope.address.alias = alias;
    };

    $scope.order = {paymentType: "1"};

    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    $scope.orderAdd = function () {
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        cartService.orderAdd($scope.order).success(function (data) {
            if (data.success) {
                if ($scope.order.paymentType == "1") {
                    location.href = "pay.html";
                } else {
                    location.href = "paysuccess.html";
                }
            } else {
                alert(data.msg);
            }
        })
    }
});