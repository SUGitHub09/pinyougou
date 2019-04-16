//服务层
app.service('orderStatisticsService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../orderStatistic/findAll.do');
	}

    //搜索
    this.search=function(page,rows,timeStatus){
        return $http.post('../orderStatistic/search.do?page='+page+"&rows="+rows+"&timeStatus="+timeStatus);
    }
});
