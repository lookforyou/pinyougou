//控制层 
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (data) {
                $scope.itemCats = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (data) {
                $scope.itemCats = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体 
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (data) {
                $scope.itemCat = data.tbItemCat;
                $scope.itemCat.type = data.tbTypeTemplate[0];
            }
        );
    };

    //保存 
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.itemCat.id != null) {//如果有ID
            $scope.itemCat.parentId = $scope.itemCatParentId;
            $scope.itemCat.typeId = $scope.itemCat.type.id;
            serviceObject = itemCatService.update($scope.itemCat); //修改
        } else {
            $scope.itemCat.parentId = $scope.itemCatParentId;
            $scope.itemCat.typeId = $scope.itemCat.type.id;
            serviceObject = itemCatService.add($scope.itemCat);//增加
        }
        serviceObject.success(
            function (data) {
                if (data.success) {
                    //重新查询 
                    $scope.findByParentId($scope.itemCatParentId);//重新加载
                } else {
                    alert(data.msg);
                }
            }
        );
    };


    //批量删除 
    $scope.dele = function () {
        //获取选中的复选框			
        itemCatService.dele($scope.ids).success(
            function (data) {
                if (confirm("确定删除吗？")) {
                    if (data.success) {
                        $scope.findByParentId($scope.itemCatParentId);//刷新列表
                        $scope.ids = [];
                    } else {
                        alert(data.msg);
                    }
                }
            }
        );
    };

    $scope.searchItemCats = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchItemCats).success(
            function (data) {
                $scope.itemCats = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    $scope.itemCatParentId = 0;
    $scope.findByParentId = function (parentId) {
        $scope.itemCatParentId = parentId;
        itemCatService.findByParentId(parentId).success(function (data) {
            $scope.itemCats = data;
            $scope.ids = [];
        })
    };

    $scope.grade = 1;

    $scope.updateGrade = function (value) {
        $scope.grade = value;
    };

    $scope.selectList = function (p_itemCat) {
        if ($scope.grade == 1) {
            $scope.itemCat_1 = null;
            $scope.itemCat_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.itemCat_1 = p_itemCat;
            $scope.itemCat_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.itemCat_2 = p_itemCat;
        }

        $scope.findByParentId(p_itemCat.id);
    };

    $scope.typeTemplate = {data: []};
    $scope.findAllTypeName = function () {
        typeTemplateService.findAllTypeName().success(function (data) {
            $scope.typeTemplate = {data: data};
        })
    }
});	
