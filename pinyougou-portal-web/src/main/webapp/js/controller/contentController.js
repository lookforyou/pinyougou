app.controller("contentController", function ($scope, contentService) {
    $scope.contents = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (data) {
            $scope.contents[categoryId] = data;
        })
    };

    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
});