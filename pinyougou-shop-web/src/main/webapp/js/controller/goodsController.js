//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

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
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return null;
        }
        goodsService.findOne(id).success(
            function (data) {
                $scope.good = data;
                editor.html($scope.good.goodsDesc.introduction);
                $scope.good.goodsDesc.itemImages = JSON.parse($scope.good.goodsDesc.itemImages);
                $scope.good.goodsDesc.customAttributeItems = JSON.parse($scope.good.goodsDesc.customAttributeItems);
                $scope.good.goodsDesc.specificationItems = JSON.parse($scope.good.goodsDesc.specificationItems);
                for (var i = 0; i < $scope.good.items.length; i++) {
                    $scope.good.items[i].spec = JSON.parse($scope.good.items[i].spec);
                }
                $scope.selectItemCat2();
                $scope.selectItemCat3();
                $scope.findTypeId();
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        $scope.good.goodsDesc.introduction = editor.html();
        if ($scope.good.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.good); //修改
        } else {
            serviceObject = goodsService.add($scope.good);//增加
        }
        serviceObject.success(
            function (data) {
                if (data.success) {
                    alert("添加成功");
                    location.href = "goods.html";
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

    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if (data.success) {
                $scope.upload_image.url = data.msg;
            } else {
                alert(data.msg);
            }
        })
    };

    $scope.good = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
    //展示图片列表
    $scope.addImageToGoods = function () {
        $scope.good.goodsDesc.itemImages.push($scope.upload_image);
    };

    //删除图片列表中的某一个
    $scope.deleteImage = function ($index) {
        $scope.good.goodsDesc.itemImages.splice($index, 1);
    };

    //商品分类一级下拉框
    $scope.selectItemCat1 = function () {
        itemCatService.findByParentId(0).success(function (data) {
            $scope.itemCats1 = data;
        });
    };

    $scope.selectItemCat2 = function () {
        itemCatService.findByParentId($scope.good.goods.category1Id).success(function (data) {
            $scope.itemCats2 = data;
        });
        //如果重新选择清空数据
        $scope.itemCats3 = {};
        $scope.brands = {};
        $scope.good.goods.typeTemplateId = null;
        // $scope.good.goodsDesc.customAttributeItems = {};
    };

    $scope.selectItemCat3 = function () {
        itemCatService.findByParentId($scope.good.goods.category2Id).success(function (data) {
            $scope.itemCats3 = data;
        })
    };

    $scope.findTypeId = function () {
        itemCatService.findOne($scope.good.goods.category3Id).success(function (data) {
            $scope.good.goods.typeTemplateId = data.tbItemCat.typeId;
            $scope.findBrandByTypeId();
            $scope.findSpecIds();
        });
    };

    $scope.findBrandByTypeId = function () {
        typeTemplateService.findOne($scope.good.goods.typeTemplateId).success(function (data) {
            $scope.brands = JSON.parse(data.brandIds);
            if ($location.search()['id'] == null) {
                $scope.good.goodsDesc.customAttributeItems = JSON.parse(data.customAttributeItems);
            }
        })
    };

    $scope.findSpecIds = function () {
        typeTemplateService.findSpecIds($scope.good.goods.typeTemplateId).success(function (data) {
            $scope.specIds = data;
        })
    };

    $scope.updateSpecificationItems = function (name, value, $event) {
        //查找是否有这个对象
        var object = $scope.searchObjectByKey($scope.good.goodsDesc.specificationItems, "attributeName", name);
        if (object != null) {
            //如果有判断他是否勾选
            if ($event.target.checked) {
                //如果勾选往里面attributeValue属性添加值
                object.attributeValue.push(value);
            } else {
                //如果没勾选，移除没有勾选的值
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                //如果attributeValue的长度为0
                if (object.attributeValue.length == 0) {
                    //那么就移除所有的值
                    $scope.good.goodsDesc.specificationItems.splice($scope.good.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            //如果没有就初始化对象并添加值
            $scope.good.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };

    $scope.createItems = function () {
        $scope.good.items = [{spec: {}, price: 0, num: 9999, status: "0", isDefault: "0"}];
        for (var i = 0; i < $scope.good.goodsDesc.specificationItems.length; i++) {
            $scope.good.items = addColumn($scope.good.items, $scope.good.goodsDesc.specificationItems[i].attributeName, $scope.good.goodsDesc.specificationItems[i].attributeValue);
        }
    };

    var addColumn = function (list, columnName, columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldElement = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newElement = JSON.parse(JSON.stringify(oldElement));
                newElement.spec[columnName] = columnValues[j];
                newList.push(newElement);
            }
        }
        return newList;
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

    $scope.checkAttributeValue = function (columnName, columnValues) {
        var object = $scope.searchObjectByKey($scope.good.goodsDesc.specificationItems, 'attributeName', columnName);
        if (object != null) {
            return object.attributeValue.indexOf(columnValues) >= 0;
        } else {
            return false;
        }
    };

    $scope.updateMarketTable = function (status) {
        if (!$scope.ids.length == 0) {
            goodsService.updateMarketTable(status, $scope.ids).success(function (data) {
                if (data.success) {
                    alert(data.msg);
                    $scope.ids = [];
                } else {
                    alert(data.msg);
                    $scope.ids = [];
                }
            })
        } else {
            if (status == '0') {
                alert("请选择要下架的商品");
            } else {
                alert("请选择要上架的商品");
            }
        }
    }
});
