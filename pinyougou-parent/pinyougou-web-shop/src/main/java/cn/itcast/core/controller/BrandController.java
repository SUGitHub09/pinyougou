package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController
@SuppressWarnings("all")
public class BrandController {
   /* //增加
    this.add=function(entity){
        return  $http.post('../brand/add.do',entity );
    }*/
     @Reference
     private BrandService brandService;
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.save(brand);
            return new Result(true,"添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //分页查询,以id排序倒序
    @RequestMapping("/search")
    public PageResult search(Integer pageNo,Integer pageSize,@RequestBody Brand searchEntity){
        PageResult pageResult = brandService.findsearch(pageNo,pageSize, searchEntity);
        return pageResult ;
    }
    //为更新做准备
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        Brand brand = brandService.findOne(id);
        return brand;
    }
    //修改一个更新
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"更新成功");
        }catch (RuntimeException e){
            //如果更改状态 返回 更新失败;
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }

    }
    //删除
    @RequestMapping("/delete")
    public Result delete(Long[]ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){

        return brandService.selectOptionList();
    }

}
