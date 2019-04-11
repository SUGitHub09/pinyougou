package cn.itcast.core.controller;

import cn.itcast.core.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping("/search")
    public Map<String,Object> itemSearch(@RequestBody Map<String,String> searchMap){
        return itemSearchService.itemSearch(searchMap);
    }
}
