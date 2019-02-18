app.controller("searchController", function ($scope, $location, searchService) {
    $scope.searchMap = {keywords : "", category : "", brand : "", spec : {}, price: "", pageNo : 1, pageSize : 40, sort: "", sortField : ""};
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (data) {
            $scope.resultMap = data;
            buildPageLabel();
        })
    };
    $scope.addSearchItem = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    };

    $scope.deleteSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    };

    var buildPageLabel = function() {
        $scope.pageLabel = [];
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;
        $scope.firstDotted = true;
        $scope.lastDotted = true;
        if (lastPage > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                $scope.firstDotted = false;
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                firstPage = lastPage - 4;
                $scope.lastDotted = false;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDotted = false;
            $scope.lastDotted = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    $scope.queryForPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    $scope.searchSort = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    };
    
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brands.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brands[i].text) >= 0) {
                return true;
            }
        }
        return false;
    };

    $scope.keywordsIsCategory = function () {
        for (var i = 0; i < $scope.resultMap.categories.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.categories[i]) >= 0) {
                return true;
            }
        }
        return false;
    };

    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
});
