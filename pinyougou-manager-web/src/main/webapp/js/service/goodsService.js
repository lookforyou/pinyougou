//服务层
app.service('goodsService', function ($http) {

    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../goods/findAll');
    };
    //分页
    this.findPage = function (page, rows) {
        return $http.get('../goods/findByPage?page=' + page + '&rows=' + rows);
    };
    //查询实体
    this.findOne = function (id) {
        return $http.get('../goods/findById?id=' + id);
    };
    //增加
    this.add = function (good) {
        return $http.post('../goods/add', good);
    };
    //修改
    this.update = function (good) {
        return $http.post('../goods/update', good);
    };
    //删除
    this.dele = function (ids) {
        return $http.get('../goods/delete?ids=' + ids);
    };
    //搜索
    this.search = function (page, rows, searchGoods) {
        return $http.post('../goods/search?page=' + page + '&rows=' + rows, searchGoods);
    };

    this.updateStatus = function (status, ids) {
        return $http.get('../goods/updateStatus?status=' + status + '&ids=' + ids);
    }
});
