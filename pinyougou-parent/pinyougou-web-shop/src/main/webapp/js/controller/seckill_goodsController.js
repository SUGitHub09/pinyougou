//控制层
app.controller('seckill_goodsController' ,function($scope,$controller,$location,seckill_goodsService) {

    $controller('baseController', {$scope: $scope});//继承
    //查询实体
    $scope.findOne=function(){
        var id = $location.search()['id'];
        if(null == id){
            return;
        }
        seckill_goodsService.findOne(id).success(
            function(response){
                $scope.entity=response;
            }
            );
    }
    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=seckill_goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=seckill_goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.flag){
                    //重新查询
                    alert(response.message);
                    location.href="seckill_goods.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //定义搜索对象
    $scope.searchEntity={};
    //搜索
    $scope.search=function(page,rows){
        seckill_goodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
       seckill_goodsService.dele( $scope.selectIds ).success(
            function(response){
                if(response.flag){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
});