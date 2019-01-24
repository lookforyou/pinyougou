//控制层
app.controller('sellerController', function ($scope, $controller, sellerService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        sellerService.findAll().success(
            function (data) {
                $scope.sellers = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        sellerService.findPage(page, rows).success(
            function (data) {
                $scope.sellers = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        sellerService.findOne(id).success(
            function (data) {
                $scope.seller = data;
            }
        );
    };

    //保存
    $scope.add = function () {
        sellerService.add($scope.seller).success(
            function (data) {
                if (data.success) {
                    location.href = "shoplogin.html";
                } else {
                    alert(data.msg);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        sellerService.dele($scope.selectIds).success(
            function (data) {
                if (data.success) {
                    $scope.reload();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchSeller = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        sellerService.search(page, rows, $scope.searchSeller).success(
            function (data) {
                $scope.sellers = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    }

});	
