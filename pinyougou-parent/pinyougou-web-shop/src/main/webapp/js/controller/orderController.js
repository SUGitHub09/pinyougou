 //控制层 
app.controller('orderController',function($scope, $controller, orderService){

	$controller('baseController',{$scope:$scope});//继承

    //  订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
    $scope.sourceType = ["","app端","pc端","M端","微信端","手机qq端"];
    // 显示状态：1、未付款，2、已付款,未发货，3、已付款,已发货，4、交易成功，5、交易关闭  6、待评价
    $scope.status = ["","未付款","已付款,未发货","已付款,已发货","交易成功","交易关闭","待评价"];

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

    $scope.searchEntity={};//定义搜索对象
    //搜索
    $scope.search=function(page,rows){
        orderService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //提交发货
    $scope.updateStatus=function(){
        //获取选中的复选框
        orderService.updateStatus( $scope.selectIds ).success(
            function(response){
                if(response.flag){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }else {
                    alert(response.message);
                }
            }
        );
    }
});	
