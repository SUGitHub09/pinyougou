//服务层
app.service('orderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../order/findAll.do');
	}

    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../order/search.do?page='+page+"&rows="+rows, searchEntity);
    }
    //修改发货状态
    this.updateStatus = function(ids){
        return $http.get('../order/updateStatus.do?ids='+ids);
    }
});
