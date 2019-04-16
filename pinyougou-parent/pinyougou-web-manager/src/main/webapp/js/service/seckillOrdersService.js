//服务层
app.service('seckillOrdersService',function($http){

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../seckillOrders/findAll.do');
    }
    //分页
    this.findPage=function(page,rows){
        return $http.get('../seckillOrders/findPage.do?page='+page+'&rows='+rows);
    }
    //查询实体
    this.findOne=function(id){
        return $http.get('../seckillOrders/findOne.do?id='+id);
    }
    //增加
    this.add=function(entity){
        return  $http.post('../seckillOrders/add.do',entity );
    }
    //修改
    this.update=function(entity){
        return  $http.post('../seckillOrders/update.do',entity );
    }
    //删除
    this.dele=function(ids){
        return $http.get('../seckillOrders/delete.do?ids='+ids);
    }
    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../seckillOrders/search.do?page='+page+"&rows="+rows, searchEntity);
    }

    this.updateStatus = function(ids,status){
        return $http.get('../seckillOrders/updateStatus.do?ids='+ids+"&status="+status);
    }


});
