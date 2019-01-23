//控制层 
app.controller('itemController', function ($scope, $controller, itemService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemService.findAll().success(
            function (data) {
                $scope.list = data;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemService.findPage(page, rows).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    }

    //查询实体 
    $scope.findOne = function (id) {
        itemService.findOne(id).success(
            function (data) {
                $scope.entity = data;
            }
        );
    }

    //保存 
    $scope.save = function () {
        var serviceObject;//服务层对象  				
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemService.update($scope.entity); //修改  
        } else {
            serviceObject = itemService.add($scope.entity);//增加 
        }
        serviceObject.success(
            function (data) {
                if (data.success) {
                    //重新查询 
                    $scope.reload();//重新加载
                } else {
                    alert(data.message);
                }
            }
        );
    }


    //批量删除 
    $scope.dele = function () {
        //获取选中的复选框			
        itemService.dele($scope.selectIds).success(
            function (data) {
                if (data.success) {
                    $scope.reload();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象 

    //搜索
    $scope.search = function (page, rows) {
        itemService.search(page, rows, $scope.searchEntity).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    }

});	
