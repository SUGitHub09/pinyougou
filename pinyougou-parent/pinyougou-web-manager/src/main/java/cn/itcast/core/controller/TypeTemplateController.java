package cn.itcast.core.controller;

import cn.itcast.common.utils.POIUtils;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"chenggong");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"shibai");
        }
    }
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate ){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"chenggong");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"shibai");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"chenggong");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"shibai");
        }
    }


    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[]ids,String status){

        try {
            typeTemplateService.updateStatus(ids,status);
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

            typeTemplateService.uploadExcelForStore(list);

            return new Result(true, "导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "导入失败");


        }
    }
}
