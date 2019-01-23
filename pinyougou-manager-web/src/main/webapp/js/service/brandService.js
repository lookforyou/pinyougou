//服务
app.service("brandService", function ($http) {
    this.findByPage = function (pageNum, pageSize) {
        return $http.get("../brand/findByPage?pageNum=" + pageNum + "&pageSize=" + pageSize);
    };

    this.add = function (brand) {
        return $http.post("../brand/add", brand);
    };

    this.update = function (brand) {
        return $http.post("../brand/update", brand);
    };

    this.findById = function (id) {
        return $http.get("../brand/findById?id=" + id);
    };

    this.deleteSelectBrands = function (ids) {
        return $http.get("../brand/deleteBrands?ids=" + ids);
    };

    this.search = function (pageNum, pageSize, brandSelect) {
        return $http.post("../brand/search?pageNum=" + pageNum + "&pageSize=" + pageSize, brandSelect);
    };

    this.findIdAndNameByBrand = function () {
        return $http.get("../brand/findIdAndName");
    }
});