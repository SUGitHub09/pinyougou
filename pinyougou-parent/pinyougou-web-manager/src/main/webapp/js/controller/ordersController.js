//控制层
app.controller('ordersController' ,function($scope,$controller,ordersService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        ordersService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //分页
    $scope.findPage=function(page,rows){
        ordersService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne=function(id){
        ordersService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }

    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=ordersService.update( $scope.entity ); //修改
        }else{
            serviceObject=ordersService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.flag){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        ordersService.dele( $scope.selectIds ).success(
            function(response){
                if(response.flag){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        ordersService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }



    $scope.itemCatList = [];
    // 显示分类:
    $scope.findItemCatList = function(){

        itemCatService.findAll().success(function(response){
            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        });
    }

    // 显示状态
    $scope.status = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
    /**
     * 订单来源：
     */
    $scope.sourceType = ["","app端","pc端","M端","微信端","手机qq端"];
    // 审核的方法:
    $scope.updateStatus = function(status){
        ordersService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }
});
