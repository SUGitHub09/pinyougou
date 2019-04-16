//服务层
app.service('seckill_orderService',function($http){

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../SeckillOrder/findAll.do');
    }
    //分页
    this.findPage=function(page,rows){
        return $http.get('../SeckillOrder/findPage.do?page='+page+'&rows='+rows);
    }
    //查询实体
    this.findOne=function(id1){
        return $http.get('../SeckillOrder/findOne.do?id1='+id1);
    }

    //增加
    this.add=function(entity){
        return  $http.post('../SeckillOrder/add.do',entity );
    }
    //修改
    this.update=function(entity){
        return  $http.post('../SeckillOrder/update.do',entity );
    }
    //删除
    this.dele=function(ids){
        return $http.get('../SeckillOrder/delete.do?ids='+ids);
    }
    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../SeckillOrder/search.do?page='+page+"&rows="+rows, searchEntity);
    }
});
