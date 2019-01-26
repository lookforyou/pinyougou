//控制层
app.controller('goodsController', function ($scope, $controller, goodsService) {

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

    $scope.add = function () {
        $scope.good.goodsDesc.introduction = editor.html();
        goodsService.add($scope.good).success(
            function (data) {
                if (data.success) {
                    alert("添加成功");
                    $scope.good = {};
                    editor.html("");
                } else {
                    alert(data.msg);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (data) {
                if (data.success) {
                    $scope.reload();//刷新列表
                    $scope.ids = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (data) {
                $scope.goods = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };
});	
