package cn.itcast.core.service;

import java.util.Map;

public interface ItemSearchService {
    Map<String,Object> itemSearch(Map<String, String> searchMap);
}
