package cn.itcast.core.controller;

import cn.itcast.common.utils.POIUtils;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNo, Integer pageSize){
        return brandService.findPage(pageNo,pageSize);
    }
    @RequestMapping("/add")
    public Result save(@RequestBody Brand brand){
        try {
            brandService.save(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
       return brandService.findOne(id);
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
             brandService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"失败");
        }
    }
    @RequestMapping("/search")
    public PageResult findSearch(Integer pageNo, Integer pageSize,@RequestBody(required = false) Brand brand){

        return brandService.findsearch(pageNo,pageSize,brand);
    }


    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }



    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[]ids,String status){

        try {
            brandService.updateStatus(ids,status);
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

            brandService.uploadExcelForStore(list);

            return new Result(true, "导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "导入失败");

        }
    }

}

