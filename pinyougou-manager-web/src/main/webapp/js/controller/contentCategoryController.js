//控制层
app.controller('contentCategoryController', function ($scope, $controller, contentCategoryService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        contentCategoryService.findAll().success(
            function (data) {
                $scope.contentCategories = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        contentCategoryService.findPage(page, rows).success(
            function (data) {
                $scope.contentCategories = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        contentCategoryService.findOne(id).success(
            function (data) {
                $scope.contentCategory = data;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.contentCategory.id != null) {//如果有ID
            serviceObject = contentCategoryService.update($scope.contentCategory); //修改
        } else {
            serviceObject = contentCategoryService.add($scope.contentCategory);//增加
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
        contentCategoryService.dele($scope.ids).success(
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
        contentCategoryService.search(page, rows, $scope.searchEntity).success(
            function (data) {
                $scope.contentCategories = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

});	
