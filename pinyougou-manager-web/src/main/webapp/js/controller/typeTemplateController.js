//控制层 
app.controller('typeTemplateController', function ($scope, $controller, typeTemplateService) {

    $controller('baseController', {$scope:$scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        typeTemplateService.findAll().success(
            function (data) {
                $scope.list = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        typeTemplateService.findPage(page, rows).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体 
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(
            function (data) {
                $scope.entity = data;
            }
        );
    };

    //保存 
    $scope.save = function () {
        var serviceObject;//服务层对象  				
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = typeTemplateService.update($scope.entity); //修改  
        } else {
            serviceObject = typeTemplateService.add($scope.entity);//增加 
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
    };


    //批量删除 
    $scope.dele = function () {
        //获取选中的复选框			
        typeTemplateService.dele($scope.selectIds).success(
            function (data) {
                if (data.success) {
                    $scope.reload();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.typeTemplate = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        typeTemplateService.search(page, rows, $scope.typeTemplate).success(
            function (data) {
                $scope.typeTemplates = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    $scope.brands = {
        data:[{id:1, text:"联想"}, {id:2, text:"华为"}, {id:3, text:"苹果"}]
    };
});	
