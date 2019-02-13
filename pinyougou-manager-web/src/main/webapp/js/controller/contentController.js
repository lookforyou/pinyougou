//控制层
app.controller('contentController', function ($scope, $controller, contentService, uploadService, contentCategoryService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        contentService.findAll().success(
            function (data) {
                $scope.contents = data;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        contentService.findPage(page, rows).success(
            function (data) {
                $scope.contents = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        contentService.findOne(id).success(
            function (data) {
                $scope.content = data;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.content.id != null) {//如果有ID
            serviceObject = contentService.update($scope.content); //修改
        } else {
            serviceObject = contentService.add($scope.content);//增加
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
        contentService.dele($scope.ids).success(
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
        contentService.search(page, rows, $scope.searchEntity).success(
            function (data) {
                $scope.contents = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
            }
        );
    };

    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if (data.success) {
                $scope.content.pic = data.msg;
            } else {
                alert(data.msg);
            }
        }).error(function () {
            alert("上传错误");
        })
    };

    $scope.findAllContentCategory = function () {
        contentCategoryService.findAll().success(function (data) {
            $scope.contentCategories = data;
        })
    };

    $scope.status = ["无效", "有效"];
});	
