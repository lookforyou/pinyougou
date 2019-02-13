app.controller("contentController", function ($scope, contentService) {
    $scope.contents = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (data) {
            $scope.contents[categoryId] = data;
        })
    }
});