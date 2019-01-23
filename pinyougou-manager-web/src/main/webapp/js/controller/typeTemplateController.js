//控制层 
app.controller('typeTemplateController', function ($scope, $controller, typeTemplateService, brandService, specificationService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        typeTemplateService.findAll().success(
            function (data) {
                $scope.typeTemplates = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        typeTemplateService.findPage(page, rows).success(
            function (data) {
                $scope.typeTemplates = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体 
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(
            function (data) {
                $scope.typeTemplate = data;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                $scope.typeTemplate.specIds = JSON.parse($scope.typeTemplate.specIds);
                $scope.typeTemplate.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        );
    };

    //保存 
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.typeTemplate.id != null) {//如果有ID
            serviceObject = typeTemplateService.update($scope.typeTemplate); //修改
        } else {
            serviceObject = typeTemplateService.add($scope.typeTemplate);//增加
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
        if (confirm("确定删除吗？")) {
            typeTemplateService.dele($scope.ids).success(
                function (data) {
                    if (data.success) {
                        $scope.reload();//刷新列表
                        $scope.ids = [];
                    }
                }
            );
        }
    };

    $scope.typeTemplateSelect = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        typeTemplateService.search(page, rows, $scope.typeTemplateSelect).success(
            function (data) {
                $scope.typeTemplates = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    $scope.brandsWithIdAndName = {data: []};
    $scope.findIdAndNameByBrand = function () {
        brandService.findIdAndNameByBrand().success(function (data) {
            $scope.brandsWithIdAndName = {data: data};
        })
    };

    $scope.specificationsWithIdAndName = {data: []};
    $scope.findIdAndNameBySpecification = function () {
        specificationService.findIdAndNameBySpecification().success(function (data) {
            $scope.specificationsWithIdAndName = {data: data};
        })
    };

    //增加扩展属性行
    $scope.addTableRow = function () {
        $scope.typeTemplate.customAttributeItems.push({});
    };

    //删除扩展属性行
    $scope.deleteTableRow = function (index) {
        $scope.typeTemplate.customAttributeItems.splice(index, 1);
    }
});	
