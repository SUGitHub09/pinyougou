package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.xml.ws.handler.MessageContext;
import java.util.List;

public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm=(ActiveMQTextMessage)message;
        try {
            String id=atm.getText();
            System.out.println("搜索项目接收到的id"+id);
            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andIsDefaultEqualTo("1");
            List<Item> items = itemDao.selectByExample(itemQuery);
            solrTemplate.saveBeans(items,1000);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
