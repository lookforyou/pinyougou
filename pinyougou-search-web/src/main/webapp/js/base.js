var app = angular.module("pinyougou", []);
//配置过滤器
app.filter("trustHtml", ["$sce", function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);