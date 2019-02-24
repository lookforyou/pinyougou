//控制层
app.controller('userController', function ($scope, userService) {
    $scope.user = {phone:""};

    $scope.register = function () {
        if ($scope.user.password != $scope.password) {
            alert("两次输入密码不正确，请重新输入");
            $scope.user.password = "";
            $scope.password = "";
            return;
        }
        userService.add($scope.user, $scope.smsCode).success(function (data) {
            alert(data.msg);
        })
    };
    var sec = 60; //倒计时秒
    var flag = true;
    $scope.sendCode = function () {
        if ($scope.user.phone == null || $scope.user.phone == "") {
            alert("请填写手机号");
            return;
        }
        if (!flag) {
            return;
        }
        userService.sendCode($scope.user.phone).success(function (data) {
            alert(data.msg);
        });
        var clock = setInterval(function () {
            flag = false;
            if (sec > 0) {
                $scope.smsMsg = sec + "秒后重发";
                $scope.$digest();
                sec--;
            } else {
                $scope.smsMsg = "重发验证码";
                flag = true;
                sec = 60;
                $scope.$digest();//强制刷新视图
                clearInterval(clock);
            }
        }, 1000)
    }
});	
