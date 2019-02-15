//服务层
app.service('contentCategoryService', function ($http) {

    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../contentCategory/findAll');
    };
    //分页
    this.findPage = function (page, rows) {
        return $http.get('../contentCategory/findPage?page=' + page + '&rows=' + rows);
    };
    //查询实体
    this.findOne = function (id) {
        return $http.get('../contentCategory/findOne?id=' + id);
    };
    //增加
    this.add = function (contentCategory) {
        return $http.post('../contentCategory/add', contentCategory);
    };
    //修改
    this.update = function (contentCategory) {
        return $http.post('../contentCategory/update', contentCategory);
    };
    //删除
    this.dele = function (ids) {
        return $http.get('../contentCategory/delete?ids=' + ids);
    };
    //搜索
    this.search = function (page, rows, searchEntity) {
        return $http.post('../contentCategory/search?page=' + page + "&rows=" + rows, searchEntity);
    };
});