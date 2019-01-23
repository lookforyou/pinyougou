//控制层 
app.controller('specificationController', function ($scope, $controller, specificationService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        specificationService.findAll().success(
            function (data) {
                $scope.specifications = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        specificationService.findPage(page, rows).success(
            function (data) {
                $scope.specifications = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体 
    $scope.findOne = function (id) {
        specificationService.findOne(id).success(
            function (data) {
                $scope.specificationPojo = data;
            }
        );
    };

    //保存 
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.specificationPojo.specification.id != null) {//如果有ID
            serviceObject = specificationService.update($scope.specificationPojo); //修改
        } else {
            serviceObject = specificationService.add($scope.specificationPojo);//增加
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
        specificationService.dele($scope.ids).success(
            function (data) {
                if (confirm("确定删除吗？")) {
                    if (data.success) {
                        $scope.reload();//刷新列表
                        $scope.ids = [];//重置数组
                    } else {
                        alert(data.msg);
                    }
                }
            }
        );
    };

    $scope.specificationSelect = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        specificationService.search(page, rows, $scope.specificationSelect).success(
            function (data) {
                $scope.specifications = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    // $scope.specificationPojo = {specification:{}, specificationOptions:[]};
    $scope.addTableRow = function () {
        $scope.specificationPojo.specificationOptions.push({});
    };

    $scope.deleteTableRow = function (index) {
        $scope.specificationPojo.specificationOptions.splice(index, 1);
    }
});	
