package com.ccwme.bugmanager.Controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccwme.bugmanager.Bean.Employees;
import com.ccwme.bugmanager.Bean.EmployeesCompany;
import com.ccwme.bugmanager.Bean.EmployeesEdu;
import com.ccwme.bugmanager.Service.Employees.EmployeesService;
import com.ccwme.bugmanager.Service.EmployeesCompany.EmployeesCompanyService;
import com.ccwme.bugmanager.Service.EmployeesEdu.EmployeesEduService;
import com.ccwme.bugmanager.Util.Message;
import lombok.extern.log4j.Log4j2;

import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;


@Controller
@Log4j2
public class EmployeesController {
    @Value("${filespath}")
    String filepath;
    @Autowired
    EmployeesService employeesService;
    @Autowired
    EmployeesEduService employeesEduService;
    @Autowired
    EmployeesCompanyService employeesCompanyService;
    @ResponseBody
    @PostMapping("/CandidateList")
    public Message candidateList(@RequestBody Map reqbody){
        Integer page = (Integer) reqbody.get("page");
        Integer pageSize = (Integer) reqbody.get("pageSize");
        String filter = (String) reqbody.get("filter");
        String search = (String) reqbody.get("search");
        JSONObject filterObject = JSON.parseObject(filter);
        JSONObject searchObject = JSON.parseObject(search);
        QueryWrapper<Employees> employeesQueryWrapper = new QueryWrapper();

        Iterator<String> searchiterator = searchObject.keySet().iterator();
        ArrayList searchkeys = new ArrayList();
        while (searchiterator.hasNext()) {
            searchkeys.add(searchiterator.next());
        }
        for (Object searchkey : searchkeys) {
            String searchkeystr = (String) searchkey;
            String valsstring = searchObject.getString(searchkeystr);
            if (valsstring != null) {
                employeesQueryWrapper.like(searchkeystr, valsstring);
            }
        }
        Iterator<String> filteriterator = filterObject.keySet().iterator();
        ArrayList filterkeys = new ArrayList();
        while (filteriterator.hasNext()) {
            filterkeys.add(filteriterator.next());
        }
        for (Object filterkey : filterkeys) {
            String filterkeystr = (String) filterkey;
            String valsstring = filterObject.getString(filterkeystr);
            if (valsstring != null) {
                JSONArray filtervals = JSON.parseArray(valsstring);
                if (filtervals.size() > 0) {
                    employeesQueryWrapper.in((String) filterkey, filtervals);
                }
            }
        }
        employeesQueryWrapper.orderByDesc("id");
        long count = employeesService.count(employeesQueryWrapper);
        int i = (Integer.valueOf(page) - 1) * Integer.valueOf(pageSize);
        employeesQueryWrapper.last("limit " + i + ", " + pageSize);
        List<Employees> list = employeesService.list(employeesQueryWrapper);
        JSONObject alldata = new JSONObject();
        JSONObject pagejsondata = new JSONObject();
        pagejsondata.put("total", count);
        alldata.put("data", list);
        alldata.put("pagedata", pagejsondata);
        return Message.success(alldata);
    }
    @ResponseBody
    @GetMapping("/readexcel")
    public Message readexcel() throws IOException {
        String fileName = filepath + "demo.xlsx";
        ArrayList<EmployeesCompany> employeeComList = new ArrayList<>();
        ArrayList<Employees> employeeList = new ArrayList<>();
        ArrayList<EmployeesEdu> employeeEduList = new ArrayList<>();
        EasyExcel.read(fileName, EmployeesCompany.class, new PageReadListener<EmployeesCompany>(dataList -> {
            for (EmployeesCompany demoData : dataList) {
                employeeComList.add(demoData);
            }
        })).sheet().headRowNumber(1).sheetNo(1).doRead();
        EasyExcel.read(fileName, EmployeesEdu.class, new PageReadListener<EmployeesEdu>(dataList -> {
            for (EmployeesEdu demoData : dataList) {
                employeeEduList.add(demoData);
            }
        })).sheet().headRowNumber(1).sheetNo(1).doRead();

        //将文件中的图片全部提取出来放在map里
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        Map<String, XSSFPictureData> picmap = getPictures(xssfWorkbook.getSheetAt(1));

        Integer[] count = {0};
        String[] lastuid1 = {null};
        EasyExcel.read(fileName, Employees.class, new PageReadListener<Employees>(dataList -> {
            String lastuid = lastuid1[0];
            for (int i = 0; i < dataList.size(); i++) {
                Employees employee = dataList.get(i);
                Integer row = i + count[0]+1;
                if (employee.getName() != null) {
                    String uid = UUID.randomUUID().toString().replace("-", "");
                    lastuid = uid;
                    employee.setUid(uid);
                    if (picmap.get(row + "-0") != null) {
                        employee.setPhoto("photo" + uid + ".jpg");
                        XSSFPictureData xssfPictureData = picmap.get(row + "-0");
                        byte[] data = xssfPictureData.getData();
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(filepath + "photo" + uid + ".jpg");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    employeeList.add(employee);
                    employeeComList.get(i + count[0]).setUid(uid);
                    employeeEduList.get(i + count[0]).setUid(uid);
                } else {
                    employeeComList.get(i + count[0]).setUid(lastuid);
                    employeeEduList.get(i + count[0]).setUid(lastuid);
                }
                //公司logo
                if (picmap.get(row + "-11") != null) {
                    System.out.println(row+"-"+lastuid);
                    employeeComList.get(i + count[0]).setCompanylogo("Companylogo" + lastuid + ".jpg");
                    XSSFPictureData xssfPictureData = picmap.get(row + "-11");
                    byte[] data = xssfPictureData.getData();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filepath + "Companylogo" + lastuid + ".jpg");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //学校logo
                if (picmap.get(row + "-17") != null) {
                    employeeEduList.get(i + count[0]).setUniversitylogo("Universitylogo" + lastuid + ".jpg");
                    XSSFPictureData xssfPictureData = picmap.get(row + "-17");
                    byte[] data = xssfPictureData.getData();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filepath + "Universitylogo" + lastuid + ".jpg");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            count[0] += dataList.size();
            lastuid1[0]=lastuid;
        })).sheet().headRowNumber(1).sheetNo(1).doRead();
        for (int i = 0; i < employeeComList.size(); i++) {
            if (employeeComList.get(i).getCompany() == null) {
                employeeComList.remove(i);
                i--;
            }
        }
        //删除学校列表中的无效数据
        for (int i = 0; i < employeeEduList.size(); i++) {
            if (employeeEduList.get(i).getUniversity() == null) {
                employeeEduList.remove(i);
                i--;
            }
        }
        employeesService.saveBatch(employeeList);
        employeesCompanyService.saveBatch(employeeComList);
        employeesEduService.saveBatch(employeeEduList);
        return Message.success(employeeEduList);
    }

    public Map<String, XSSFPictureData> getPictures(XSSFSheet xssfSheet) {

        Map<String, XSSFPictureData> map = new HashMap<>();
        List<XSSFShape> list = xssfSheet.getDrawingPatriarch().getShapes();
        for (XSSFShape shape : list) {

            XSSFPicture picture = (XSSFPicture) shape;
            XSSFClientAnchor xssfClientAnchor = (XSSFClientAnchor) picture.getAnchor();
            XSSFPictureData pdata = picture.getPictureData(); // 行号-列号
            String key = xssfClientAnchor.getRow1() + "-" + xssfClientAnchor.getCol1();
            map.put(key, pdata);
        }
        return map;
    }
    @GetMapping("/test")
    @ResponseBody
    @Test
    public Message test() throws IOException {
        String url="https://mmbiz.qpic.cn/mmbiz_jpg/reaJ3QMbz3tBrFjaVdWOonym31JxicSJebdNpM1QZib0Bsk0FR2M2Lk4IgaVbI0YIAqc8kpAqRG5AJQK5jp0jwxA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1";
        RestTemplate restTemplate = new RestTemplate();
        byte[] forObject = restTemplate.getForObject(url, byte[].class);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(filepath + "001.jpg"));
        fileOutputStream.write(forObject);
        fileOutputStream.close();
        return Message.success("success");
    }
}
