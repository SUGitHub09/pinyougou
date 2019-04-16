package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@SuppressWarnings("all")
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public void add(Seller seller) {
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        PageHelper.startPage(page, rows);
        SellerQuery sellerQuery=new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();
        if (seller!=null){
            criteria.andStatusEqualTo("1");
            if (seller.getName()!=null&& !"".equals(seller.getName().trim())){
                criteria.andNameLike("%"+seller.getName().trim()+"%");
            }
            if (seller.getNickName()!=null&& !seller.getNickName().trim().equals("")){
                criteria.andNickNameLike("%"+seller.getNickName().trim()+"%");
            }
        }
        Page<Seller> sellers = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(sellers.getTotal(),sellers.getResult());
    }

    @Override
    public Seller findOne(String id) {
        return  sellerDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public Seller findBySellerId(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }

    @Override
    public void findOneForOrder(String id) {
        //1.创建一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();//作用于excel2003 版本
//2.创建工作表
        Sheet sheet = wb.createSheet(id+"订单数据");
//3.创建行对象
        Row row = sheet.createRow(0);
//4.创建单元格
        Cell cell = row.createCell(0);
        Cell cell1 = row.createCell(2);
        Cell cell2= row.createCell(4);
        Cell cell3 = row.createCell(6);
        Cell cell4 = row.createCell(8);
        Cell cell5= row.createCell(10);
        Cell cell6= row.createCell(12);
        Cell cell7= row.createCell(14);
        Cell cell8= row.createCell(18);






//5.设置单元格的内容

        cell.setCellValue("订单id");
        cell1.setCellValue("下单用户");
        cell2.setCellValue("收货人");
        cell3.setCellValue("收货地址");
        cell4.setCellValue("收货人电话");
        cell5.setCellValue("总支付金额");
        cell6.setCellValue("支付类型");
        cell7.setCellValue("订单商品");
        cell8.setCellValue("下单时间");



        HSSFPalette palette = wb.getCustomPalette();
        palette.setColorAtIndex((short)9, (byte) (0xff & 251), (byte) (0xff & 161), (byte) (0xff & 161));


//6.设置单元格的样式
        CellStyle cellStyle = wb.createCellStyle();


        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillBackgroundColor((short) 13);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short)13);//字体大小
        font.setFontName("宋体");//字体名称


        cellStyle.setFont(font);//设置单元格样式的字体

        cell.setCellStyle(cellStyle);//将单元格样式作用于单元格
        cell1.setCellStyle(cellStyle);
        cell2.setCellStyle(cellStyle);
        cell3.setCellStyle(cellStyle);
        cell4.setCellStyle(cellStyle);
        cell5.setCellStyle(cellStyle);
        cell6.setCellStyle(cellStyle);
        cell7.setCellStyle(cellStyle);
        cell8.setCellStyle(cellStyle);

        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        criteria.andSellerIdEqualTo(id);


        List<Order> orders = orderDao.selectByExample(orderQuery);

        for (int i = 0; i < orders.size(); i++) {

            OrderItemQuery orderItemQuery = new OrderItemQuery();

            OrderItemQuery.Criteria criteria1 = orderItemQuery.createCriteria();
            criteria1.andOrderIdEqualTo(orders.get(i).getOrderId());

            List<OrderItem> orderItems = orderItemDao.selectByExample(orderItemQuery);


            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell1 =row.createCell(2);
            cell2= row.createCell(4);
            cell3 =row.createCell(6);
            cell4 =row.createCell(8);
            cell5=row.createCell(10);
            cell6=row.createCell(12);
            cell7=row.createCell(14);
            cell8=row.createCell(18);

            cell.setCellValue(orders.get(i).getOrderId());
            cell1.setCellValue(orders.get(i).getUserId());
            cell2.setCellValue(orders.get(i).getReceiver());
            cell3.setCellValue(orders.get(i).getReceiverAreaName());
            cell4.setCellValue(orders.get(i).getReceiverMobile());
            cell5.setCellValue(orders.get(i).getPayment()+"元");
            if (orders.get(i).getPaymentType() == null) {

                cell6.setCellValue("付款异常");
            }else {

                cell6.setCellValue(orders.get(i).getPaymentType().equals("1")?"在线支付":"货到付款");
            }
            String name = null;
            for (OrderItem orderItem : orderItems) {
                name += orderItem.getTitle() + "--";
            }
            cell7.setCellValue(name);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

            cell8.setCellValue(sdf.format(orders.get(i).getCreateTime()));




            CellStyle scellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle.setFillBackgroundColor((short) 13);

            Font sfont = wb.createFont();
            sfont.setFontHeightInPoints((short)13);//字体大小
            sfont.setFontName("宋体");//字体名称


            cellStyle.setFont(sfont);//设置单元格样式的字体

            cell.setCellStyle(scellStyle);//将单元格样式作用于单元格
            cell1.setCellStyle(scellStyle);
            cell2.setCellStyle(scellStyle);
            cell3.setCellStyle(scellStyle);
            cell4.setCellStyle(scellStyle);
            cell5.setCellStyle(scellStyle);
            cell5.setCellStyle(scellStyle);
            cell6.setCellStyle(scellStyle);
            cell7.setCellStyle(scellStyle);
            cell8.setCellStyle(scellStyle);
        }

//        HSSFPalette palette = wb.getCustomPalette();



//7.保存，关闭流
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            OutputStream os = new FileOutputStream("E:\\"+id+"order"+sdf.format(new Date())+".xls");
            wb.write(os);
            os.close();
        } catch (IOException e) {

            e.printStackTrace();

        throw  new RuntimeException(e);
        }
//8.下载（在项目中采用工具类的方式）




    }

    @Override
    public void  findOneForExcel(String id) {

        //1.创建一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();//作用于excel2003 版本
//2.创建工作表
        Sheet sheet = wb.createSheet(id + "商品数据");



//3.创建行对象
        Row row = sheet.createRow(0);
//4.创建单元格
        Cell cell = row.createCell(0);
        Cell cell1 = row.createCell(2);
        Cell cell2= row.createCell(4);
        Cell cell3 = row.createCell(8);
        Cell cell4 = row.createCell(10);
        Cell cell5= row.createCell(12);




//5.设置单元格的内容
        cell.setCellValue("商品id");
        cell1.setCellValue("商品名称");
        cell2.setCellValue("商品分类");
        cell3.setCellValue("商品价格");
        cell4.setCellValue("商品审核状态");
        cell5.setCellValue("商品上架状态");
        HSSFPalette palette = wb.getCustomPalette();
        palette.setColorAtIndex((short)9, (byte) (0xff & 251), (byte) (0xff & 161), (byte) (0xff & 161));


//6.设置单元格的样式
        CellStyle cellStyle = wb.createCellStyle();


        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillBackgroundColor((short) 13);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short)13);//字体大小
        font.setFontName("宋体");//字体名称


        cellStyle.setFont(font);//设置单元格样式的字体

        cell.setCellStyle(cellStyle);//将单元格样式作用于单元格
        cell1.setCellStyle(cellStyle);
        cell2.setCellStyle(cellStyle);
        cell3.setCellStyle(cellStyle);
        cell4.setCellStyle(cellStyle);
        cell5.setCellStyle(cellStyle);

        GoodsQuery goodsQuery = new GoodsQuery();

        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andSellerIdEqualTo(id);


        List<Goods> goods = goodsDao.selectByExample(goodsQuery);
        for (int i = 0; i < goods.size(); i++) {

            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.get(i).getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.get(i).getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.get(i).getCategory3Id());


            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell1 =row.createCell(2);
            cell2= row.createCell(4);
            cell3 =row.createCell(8);
            cell4 =row.createCell(10);
            cell5=row.createCell(12);


            cell.setCellValue(goods.get(i).getId());
            cell1.setCellValue(goods.get(i).getGoodsName());
            cell2.setCellValue(itemCat1.getName()+"-"+itemCat2.getName()+"-"+itemCat3.getName());
            cell3.setCellValue(goods.get(i).getPrice()+"元");
            if (goods.get(i).getAuditStatus() == null) {

                cell4.setCellValue("审核异常");
            }else {

                cell4.setCellValue(goods.get(i).getAuditStatus().equals("1")?"已审核通过":"未审核通过");
            }

            if (goods.get(i).getIsMarketable()== null) {

                cell5.setCellValue("状态异常");
            }else {

                cell5.setCellValue(goods.get(i).getIsMarketable()=="1"?"已上架":"未上架");

            }





            CellStyle scellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle.setFillBackgroundColor((short) 13);

            Font sfont = wb.createFont();
            sfont.setFontHeightInPoints((short)13);//字体大小
            sfont.setFontName("宋体");//字体名称


            cellStyle.setFont(sfont);//设置单元格样式的字体

            cell.setCellStyle(scellStyle);//将单元格样式作用于单元格
            cell1.setCellStyle(scellStyle);
            cell2.setCellStyle(scellStyle);
            cell3.setCellStyle(scellStyle);
            cell4.setCellStyle(scellStyle);
            cell5.setCellStyle(scellStyle);
        }

//        HSSFPalette palette = wb.getCustomPalette();



//7.保存，关闭流
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            OutputStream os = new FileOutputStream("E:\\"+id+sdf.format(new Date())+".xls");
            wb.write(os);
            os.close();
        } catch (IOException e) {

            e.printStackTrace();

            throw  new RuntimeException(e);
        }
//8.下载（在项目中采用工具类的方式）


    }
}
