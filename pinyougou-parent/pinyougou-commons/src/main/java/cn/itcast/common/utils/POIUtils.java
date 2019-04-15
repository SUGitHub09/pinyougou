package cn.itcast.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class POIUtils {

    public Workbook getWorkbook(MultipartFile file) {

        String filename = file.getOriginalFilename();

        Workbook workbook = null;

        try {
            InputStream inputStream = file.getInputStream();

            if (filename.endsWith("xls")) {

                workbook = new HSSFWorkbook(inputStream);

            } else if (filename.endsWith("xlsx")) {

                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;

    }

    public List<String[]> ParseExcel(MultipartFile file) {
        Workbook workbook = getWorkbook(file);

        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                if (sheet == null) {
                    continue;
                }
                int firstRowNum = sheet.getFirstRowNum();

                int lastRowNum = sheet.getLastRowNum();

                for (int rowNum = firstRowNum + 1; rowNum < lastRowNum + 1; rowNum++) {

                    Row row = sheet.getRow(rowNum);

                    if (row == null) {
                       continue;
                    }

                    int firstCellNum = row.getFirstCellNum();
                    int lastCellNum = row.getLastCellNum();

                    String[] cells = new String[row.getPhysicalNumberOfCells()];

                    for (int cellNum =firstCellNum; cellNum <lastCellNum; cellNum++) {

                        Cell cell = row.getCell(cellNum);

                          cell.setCellType(Cell.CELL_TYPE_STRING);

                         cells[cellNum] = cell.getStringCellValue();



                    }

                    list.add(cells);


                }


            }

        }
        return list;
    }




}
