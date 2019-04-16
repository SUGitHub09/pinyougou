 //控制层 
app.controller('orderStatisticsController',function($scope, $controller, orderStatisticsService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        orderStatisticsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

    $scope.searchEntity={};//定义搜索对象
    //搜索
    $scope.search=function(page,rows){
        orderStatisticsService.search(page,rows,$scope.timeStatus).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
});	
