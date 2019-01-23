//控制器
app.controller("brandController", function ($scope, brandService, $controller) {

    //伪继承
    $controller("baseController", {$scope:$scope});

    $scope.findByPage = function (pageNum, pageSize) {
        brandService.findByPage(pageNum, pageSize).success(function (data) {
            $scope.brands = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //添加和修改功能
    $scope.save = function () {
        var obj = null;
        if ($scope.brand.id != null)  {
            obj = brandService.update($scope.brand);
        } else {
            obj = brandService.add($scope.brand);
        }
        obj.success(function (data) {
            if (data.success) {
                $scope.reload();
            } else {
                alert(data.msg);
            }
        })
    };

    //修改数据回显
    $scope.findById = function (id) {
        brandService.findById(id).success(function (data) {
            $scope.brand = data;
        })
    };


    $scope.deleteSelectBrands = function () {
        brandService.deleteSelectBrands($scope.ids).success(function (data) {
            if (confirm("确定删除吗？")) {
                if (data.success) {
                    $scope.reload();
                    $scope.ids = [];
                } else {
                    alert(data.msg);
                }
            }
        })
    };

    //模糊查询功能
    $scope.brandSelect= {};
    $scope.search = function (pageNum, pageSize) {
        brandService.search(pageNum, pageSize, $scope.brandSelect).success(function (data) {
            $scope.brands = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

});