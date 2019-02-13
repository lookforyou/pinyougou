app.service("uploadService", function ($http) {
    this.uploadFile = function () {
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http({
            url:"../upload",
            method:"post",
            data:formData,
            headers:{"Content-Type":undefined},
            transformRequest:angular.identity
        });
    }
});