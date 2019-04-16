//服务层
app.service('seckill_goodsService',function($http){

    //查询实体
    this.findOne=function(id){
        return $http.get('../SeckillGoods/findOne.do?id='+id);
    }
    //增加
    this.add=function(entity){
        return  $http.post('../SeckillGoods/add.do',entity );
    }
    //修改
    this.update=function(entity){
        return  $http.post('../SeckillGoods/update.do',entity );
    }
    //删除
    this.dele=function(ids){
        return $http.get('../SeckillGoods/delete.do?ids='+ids);
    }
    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../SeckillGoods/search.do?page='+page+"&rows="+rows, searchEntity);
    }
});
