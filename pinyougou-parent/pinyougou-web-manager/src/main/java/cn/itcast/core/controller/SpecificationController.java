package cn.itcast.core.controller;

import cn.itcast.common.utils.POIUtils;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;

import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specificationService;
    //page='+page+"&rows="+rows, searchEntity
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) Specification specification){
        return specificationService.search(page,rows,specification);
    }
    @RequestMapping("/add")
    public Result search(@RequestBody SpecificationVo specificationvo){
        try {
            specificationService.add(specificationvo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    @RequestMapping("findOne")
    public SpecificationVo findOne(Long id){
        return specificationService.findOne(id);
    }
    @RequestMapping("update")
    public Result update(@RequestBody SpecificationVo specificationvo){
        try {
            specificationService.update(specificationvo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }



    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[]ids,String status){

        try {
            specificationService.updateStatus(ids,status);
            return new Result(true, "审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }

    @RequestMapping("/uploadExcelForStore")
    public Result uploadExcelForStore(MultipartFile file) {

        try {

            POIUtils poiUtils = new POIUtils();

            List<String[]> list = poiUtils.ParseExcel(file);

            specificationService.uploadExcelForStore(list);

            return new Result(true, "导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "导入失败");

        }
    }


}
