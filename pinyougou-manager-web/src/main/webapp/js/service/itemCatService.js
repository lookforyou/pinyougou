//服务层
app.service('itemCatService', function ($http) {

    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../itemCat/findAll');
    };
    //分页
    this.findPage = function (page, rows) {
        return $http.get('../itemCat/findPage?page=' + page + '&rows=' + rows);
    };
    //查询实体
    this.findOne = function (id) {
        return $http.get('../itemCat/findOne?id=' + id);
    };
    //增加
    this.add = function (itemCat) {
        return $http.post('../itemCat/add', itemCat);
    };
    //修改
    this.update = function (itemCat) {
        return $http.post('../itemCat/update', itemCat);
    };
    //删除
    this.dele = function (ids) {
        return $http.get('../itemCat/delete?ids=' + ids);
    };
    //搜索
    this.search = function (page, rows, searchItemCats) {
        return $http.post('../itemCat/search?page=' + page + "&rows=" + rows, searchItemCats);
    };

    this.findByParentId = function (parentId) {
        return $http.get("../itemCat/findByParentId?parentId=" + parentId);
    }
});
