app.controller("indexController", function ($scope, loginService) {
    $scope.getUsername = function () {
        loginService.getUsername().success(function (data) {
            $scope.username = data.username;
        })
    }
});