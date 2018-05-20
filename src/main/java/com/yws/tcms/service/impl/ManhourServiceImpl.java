package com.yws.tcms.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yws.tcms.domain.Manhour;
import com.yws.tcms.domain.ManhourRepository;
import com.yws.tcms.service.ManhourService;
import com.yws.tcms.web.vo.ResultJson;

@Service
public class ManhourServiceImpl implements ManhourService {

	@Autowired
	ManhourRepository manhourRepository;

	@Autowired
    ObjectMapper mapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(ManhourServiceImpl.class);
	
	public Page<Manhour> findAll(int page, int size) {
		Sort sort = new Sort(Direction.ASC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		
		return manhourRepository.findAll(pageable);
	}

	public void batchImport(String rootpath, MultipartFile mFile) {
		if (!mFile.isEmpty()) {
			// 取文件后缀名
			String type = mFile.getOriginalFilename().substring(mFile.getOriginalFilename().indexOf("."));
			String fileName = System.currentTimeMillis() + type; // 取当前时间戳作为文件名
			String path = rootpath + "upload/" + fileName;

			// 创建一个新的的文件，用于存放上传的文件内容。
			File destFile = new File(path);
			try {
				// 将上传的文件内容写入到新创建的文件中。
				mFile.transferTo(destFile);
			} catch (IllegalStateException e) {
				LOGGER.error("当前对客户端的响应已经结束，不能在响应已经结束（或说消亡）后再向客户端（实际上是缓冲区）输出任何内容。" + e);
			} catch (IOException e) {
				LOGGER.error("IOException: " + e);
			}

			List<Manhour> manhours = readExcelValue(destFile);
			// 使用集合或数组之前先判断是否为空。
			if (!CollectionUtils.isEmpty(manhours)) {
				for (Manhour manhour : manhours) {
					manhourRepository.save(manhour);
				}
			}
		}
	}

	/**
	 * 将Excel文件中的数据行转换为Emp对象。
	 * 
	 * @param file
	 *            Excel文件，后缀名为xlsx
	 * @return
	 */
	private List<Manhour> readExcelValue(File file) {
		List<Manhour> manhourList = new ArrayList<Manhour>();
		InputStream is = null;
		// excel2007
		XSSFWorkbook xWorkbook = null;
		try {
			is = new FileInputStream(file);
			xWorkbook = new XSSFWorkbook(is);
			// 得到第一个Sheet
			XSSFSheet xSheet = xWorkbook.getSheetAt(0);
			if (null != xSheet) {
				// 得到Excel的行数
				int totalRows = xSheet.getPhysicalNumberOfRows();
				String[] titles = {"name","empNo","date","pdu","project","buzhu","hours"};
				// 总列数
				int totalCells = titles.length;
				
				// 数据从第二行开始
				for (int i = 1; i < totalRows; i++) {
					Manhour manhour = new Manhour();
					Row xRow = xSheet.getRow(i);
					Map<String,Object> map = new HashMap<String,Object>();
					for(int j=0;j<totalCells;j++){
						Cell cell = xRow.getCell(j);
						if(null != cell) {
				            switch (cell.getCellType()) {
				                case XSSFCell.CELL_TYPE_NUMERIC:
				                    double cellValue = cell.getNumericCellValue();
				                    if(DateUtil.isCellDateFormatted(cell)) { // 日期格式
				                        Date date = HSSFDateUtil.getJavaDate(cellValue);
				                        map.put(titles[j], date);
				                    }else{
				                    	map.put(titles[j], cellValue);
				                    }
				                    break;

				                case XSSFCell.CELL_TYPE_STRING:
				                    map.put(titles[j], cell.getStringCellValue());
				                    break;
				                default:
				                    break;
				            }
				        }
					}
					String result = mapper.writeValueAsString(map);
					System.out.println("result=" + result);
					manhour = mapper.readValue(result,Manhour.class);
					manhourList.add(manhour);
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException: " + e);
		} catch (IOException e) {
			LOGGER.error("IOException: " + e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
					LOGGER.error("关闭InputStream异常: " + e);
				}
			}

			if (null != xWorkbook) {
				try {
					xWorkbook.close();
				} catch (Exception e) {
					LOGGER.error("关闭文件异常: " + e);
				}
			}
			
			// 删除上传的文件 
			if(file.exists()){
				file.delete();
			}
		}

		return manhourList;
	}

	public ResultJson deleteById(Integer id) {
		ResultJson json = new ResultJson();
		Manhour manhour = manhourRepository.findById(id).orElse(null);
		if(manhour != null){
			manhourRepository.delete(manhour);
				json.setSuccess(true);
		}else {
			json.setSuccess(false);
			json.setMsg("要删除的数据不存在");
		}
		
		return json;
	}
	
	public List<String> pduList(){
		return manhourRepository.pduList();
	}
	
	public List<String> projectList() {
		return manhourRepository.projectList();
	}
	
	public List<Map<String,String>> personList() {
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		List<Object[]> list = manhourRepository.empNoList();
		if(list != null && list.size()>0){
			for(Object[] arr:list) {
				Map<String,String> map = new HashMap<String, String>();
				map.put("empNo", arr[0].toString());
				map.put("name", arr[1].toString());
				mapList.add(map);
			}
		}
		
		return mapList;
	}

	public Map<String,Object> byPdu(String pdu) {
		Map<String,Object> map = new HashMap<String,Object>();
		// 月度数据
		List<Object[]> monthList = manhourRepository.byPduMonth(pdu);
		if(monthList != null && monthList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:monthList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("name", pdu);
			map.put("mxAxis", xAxis);
			map.put("mseries1", series1);
			map.put("mseries2", series2);
		}
		
		List<Object[]> quarterList = manhourRepository.byPduQuarter(pdu);
		if(quarterList != null && quarterList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:quarterList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("qxAxis", xAxis);
			map.put("qseries1", series1);
			map.put("qseries2", series2);
		}
		
		List<Object[]> yearList = manhourRepository.byPduYear(pdu);
		if(yearList != null && yearList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:yearList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("yxAxis", xAxis);
			map.put("yseries1", series1);
			map.put("yseries2", series2);
		}
		
		return map;
	}
	
	public Map<String,Object> byProject(String project) {
		Map<String,Object> map = new HashMap<String,Object>();
		// 月度数据
		List<Object[]> monthList = manhourRepository.byProjectMonth(project);
		if(monthList != null && monthList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:monthList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("name", project);
			map.put("mxAxis", xAxis);
			map.put("mseries1", series1);
			map.put("mseries2", series2);
		}
		
		List<Object[]> quarterList = manhourRepository.byProjectQuarter(project);
		if(quarterList != null && quarterList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:quarterList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("qxAxis", xAxis);
			map.put("qseries1", series1);
			map.put("qseries2", series2);
		}
		
		List<Object[]> yearList = manhourRepository.byProjectYear(project);
		if(yearList != null && yearList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:yearList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("yxAxis", xAxis);
			map.put("yseries1", series1);
			map.put("yseries2", series2);
		}
		
		return map;
	}
	
	public Map<String,Object> byPerson(String empNo) {
		Map<String,Object> map = new HashMap<String,Object>();
		// 月度数据
		List<Object[]> monthList = manhourRepository.byPersonMonth(empNo);
		if(monthList != null && monthList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:monthList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("name", empNo);
			map.put("mxAxis", xAxis);
			map.put("mseries1", series1);
			map.put("mseries2", series2);
		}
		
		List<Object[]> quarterList = manhourRepository.byPersonQuarter(empNo);
		if(quarterList != null && quarterList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:quarterList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("qxAxis", xAxis);
			map.put("qseries1", series1);
			map.put("qseries2", series2);
		}
		
		List<Object[]> yearList = manhourRepository.byPersonYear(empNo);
		if(yearList != null && yearList.size()>0){
			List<String> xAxis = new ArrayList<String>();
			List<Double> series1 = new ArrayList<Double>();
			List<Double> series2 = new ArrayList<Double>();
			for(Object[] arr:yearList){
				xAxis.add(arr[0].toString());
				series1.add(Double.parseDouble(arr[1].toString()));
				series2.add(Double.parseDouble(arr[2].toString()));
			}
			
			map.put("yxAxis", xAxis);
			map.put("yseries1", series1);
			map.put("yseries2", series2);
		}
		
		return map;
	}
	
}
