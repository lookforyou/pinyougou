//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (data) {
                $scope.goods = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (data) {
                $scope.goods = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (data) {
                $scope.good = data;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (data) {
                if (data.success) {
                    //重新查询
                    $scope.reload();//重新加载
                } else {
                    alert(data.msg);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.ids).success(
            function (data) {
                if (data.success) {
                    $scope.reload();//刷新列表
                    $scope.ids = [];
                }
            }
        );
    };

    $scope.searchGoods = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchGoods).success(
            function (data) {
                $scope.goods = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];

    $scope.itemCats = [];
    $scope.findItemCats = function () {
        itemCatService.findAll().success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.itemCats [data[i].id] = data[i].name;
            }
        })
    };

    $scope.updateStatus = function (status) {
        if (!$scope.ids.length == 0) {
            goodsService.updateStatus(status, $scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reload();
                    $scope.ids = [];
                } else {
                    alert(data.msg);
                    $scope.ids = [];
                }
            })
        } else {
            alert("请选择要审核的商品");
        }
    }
});	
