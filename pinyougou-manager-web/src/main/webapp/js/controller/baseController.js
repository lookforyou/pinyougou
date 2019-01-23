app.controller("baseController", function ($scope) {
    //分页条插件
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reload();
        }
    };

    //刷新页面
    $scope.reload = function() {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //删除功能
    $scope.ids = [];
    $scope.selectIds = function (id, $event) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            $scope.ids.splice($scope.ids.indexOf(id), 1);
        }
    };

    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i++) {
            value += json[i][key];
            if (i != json.length - 1) {
                value += "，";
            }
        }
        return value;
    };
});