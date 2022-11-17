package com.ccwme.employeemanager.Controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ccwme.employeemanager.Bean.*;
import com.ccwme.employeemanager.Service.CompanyList.CompanyListService;
import com.ccwme.employeemanager.Service.Employees.EmployeesService;
import com.ccwme.employeemanager.Service.EmployeesComments.EmployeesCommentsService;
import com.ccwme.employeemanager.Service.EmployeesCompany.EmployeesCompanyService;
import com.ccwme.employeemanager.Service.EmployeesEdu.EmployeesEduService;
import com.ccwme.employeemanager.Service.EmployeesResumes.EmployeesResumesService;
import com.ccwme.employeemanager.Util.Message;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;


@Controller
@Log4j2
public class CompanyListController {
    @Value("${filespath}")
    String filepath;
    @Autowired
    EmployeesService employeesService;
    @Autowired
    EmployeesEduService employeesEduService;
    @Autowired
    EmployeesCompanyService employeesCompanyService;
    @Autowired
    EmployeesCommentsService employeesCommentsService;
    @Autowired
    EmployeesResumesService employeesResumesService;
    @Autowired
    CompanyListService companyListService;

    @ResponseBody
    @CrossOrigin
    @PostMapping("/CompanyList")
    public Message CompanyList(@RequestBody Map reqbody) {
        Integer page = (Integer) reqbody.get("page");
        Integer pageSize = (Integer) reqbody.get("pageSize");
        String filter = (String) reqbody.get("filter");
        String search = (String) reqbody.get("search");
        JSONObject filterObject = JSON.parseObject(filter);
        JSONObject searchObject = JSON.parseObject(search);
        QueryWrapper<Companylist> companylistQueryWrapper = new QueryWrapper();

        Iterator<String> searchiterator = searchObject.keySet().iterator();
        ArrayList searchkeys = new ArrayList();
        while (searchiterator.hasNext()) {
            searchkeys.add(searchiterator.next());
        }
        for (Object searchkey : searchkeys) {
            String searchkeystr = (String) searchkey;
            String valsstring = searchObject.getString(searchkeystr);
            if (valsstring != null) {
                //如果key是id筛选方式不是like而是eq
                if (searchkeystr.equals("id")) {
                    companylistQueryWrapper.eq(searchkeystr, valsstring);
                } else {
                    companylistQueryWrapper.like(searchkeystr, valsstring);
                }
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
                    companylistQueryWrapper.in((String) filterkey, filtervals);
                }
            }
        }
        companylistQueryWrapper.orderByDesc("id");
        long count = companyListService.count(companylistQueryWrapper);
        int i = (Integer.valueOf(page) - 1) * Integer.valueOf(pageSize);
        companylistQueryWrapper.last("limit " + i + ", " + pageSize);
        List<Companylist> companylist = companyListService.list(companylistQueryWrapper);
        JSONObject alldata = new JSONObject();
        JSONObject pagejsondata = new JSONObject();
        pagejsondata.put("total", count);
        alldata.put("data", companylist);
        alldata.put("pagedata", pagejsondata);
        return Message.success(alldata);
    }

    @PostMapping("/updateColData1")
    @ResponseBody
    @CrossOrigin
    public Message editTextArea(String id, String col, String data) {
        UpdateWrapper<Employees> bugUpdateWrapper = new UpdateWrapper<>();
        bugUpdateWrapper.eq("id", id);
        bugUpdateWrapper.set(col, data);
        boolean update = employeesService.update(bugUpdateWrapper);
        if (update) {
            return Message.success("update success");
        }
        return Message.fail("update fail");
    }

    @PostMapping("/recivelogoimg")
    @ResponseBody
    @CrossOrigin
    public Message reciveheadimg(@RequestPart MultipartFile file, @RequestPart String filename) throws IOException {
        File file1 = new File(filepath + "uploadtempfiles" + File.separator + filename + ".jpg");
        file.transferTo(file1);
        return Message.success("recivedimg");
    }

    @PostMapping("/removelogoimg")
    @ResponseBody
    @CrossOrigin
    public Message removelogoimg(@RequestBody Map param) throws IOException {
        File file = new File(filepath + "uploadtempfiles" + File.separator + param.get("filename") + ".jpg");
        if (file.exists()) {
            if (file.delete()) {
                return Message.success("deleted");
            }
            return Message.fail("file delete error");
        }
        return Message.fail("file not exist");
    }

    @PostMapping("/checkCompanynumber")
    @ResponseBody
    @CrossOrigin
    public Message checkCompanynumber(@RequestBody Map param) throws IOException {
        Object number = param.get("number");
        QueryWrapper<Companylist> companylistQueryWrapper = new QueryWrapper<>();
        companylistQueryWrapper.eq("number", number);
        List<Companylist> list = companyListService.list(companylistQueryWrapper);
        if (list.size() > 0) {
            return Message.fail("重复");
        }
        return Message.success("不重复");
    }

    @PostMapping("/deleteCompany")
    @ResponseBody
    @CrossOrigin
    public Message deleteCompany(@RequestBody Map param) throws IOException {
        Object id = param.get("id");
        Companylist comp = companyListService.getById((Serializable) id);
        String logo = comp.getLogo();
        JSONArray jsonArray = JSON.parseArray(logo);
        for (Object logoname : jsonArray) {
            File file = new File(filepath + "CompanyLogo" + File.separator + logoname);
            if (file.exists()) {
                file.delete();
                System.out.println("删除logo");
            }
        }
        boolean remove = companyListService.removeById((Serializable) id);
        if (remove) {
            return Message.success("deleted");
        }
        return Message.fail("delete fail");
    }

    @PostMapping("/editCompanyOne")
    @ResponseBody
    @CrossOrigin
    public Message editCompanyOne(@RequestBody Companylist company) throws IOException {
        String logo1 = company.getLogo();
        Companylist comp2 = companyListService.getById(company.getId());
        String logo2 = comp2.getLogo();
        JSONArray jsonArray1 = JSON.parseArray(logo1);
        JSONArray jsonArray2 = JSON.parseArray(logo2);
        ArrayList<String> haslogos = new ArrayList<>();
        for (Object log1 : jsonArray1) {
            boolean tem=true;
            for (Object log2 : jsonArray2) {
                System.out.println(log1);
                System.out.println(log2);
                if (log1.equals(log2)) {
                    haslogos.add((String) log1);
                    tem=false;
                }
            }
            if(tem){
                File file1 = new File(filepath + "uploadtempfiles" + File.separator + log1);
                if (file1.exists()) {
                    file1.renameTo(new File(filepath + "CompanyLogo" + File.separator + log1));
                } else {
                    return Message.fail("missing logoFiles");
                }
            }
        }
        //删除edit后没有的logo
        for (Object log2 : jsonArray2) {
            boolean tem=true;
            for (Object log1 : jsonArray1) {
                if (log2.equals(log1)) {
                    tem=false;
                }
            }
            if(tem){
                File file1 = new File(filepath + "CompanyLogo" + File.separator + log2);
                if(file1.exists()){
                    file1.delete();
                }
            }
        }
        boolean b = companyListService.updateById(company);
        if (b) {
            return Message.success("update success");
        }
        return Message.fail("update fail");
    }

    @ResponseBody
    @CrossOrigin
    @PostMapping("/addCompanyOne")
    public Message addCompanyOne(@RequestBody Companylist company) {
        String logostr = company.getLogo();
        JSONArray jsonArray = JSON.parseArray(logostr);
        QueryWrapper<Companylist> companylistQueryWrapper = new QueryWrapper<>();
        companylistQueryWrapper.orderByDesc("id");
        companylistQueryWrapper.last("limit 0,1");
        List<Companylist> list = companyListService.list(companylistQueryWrapper);
        String number;
        if(list.size()==0){
            number = "20220101";
        }else{
            Companylist companylist = list.get(0);
            number = companylist.getNumber();
        }
        long newNumber = Long.parseLong(number)+1;
        for (Object logo : jsonArray) {
            File file1 = new File(filepath + "uploadtempfiles" + File.separator + logo);
            if (file1.exists()) {
                file1.renameTo(new File(filepath + "CompanyLogo" + File.separator + logo));
            } else {
                return Message.fail("missing logoFiles");
            }
        }
        company.setNumber(String.valueOf(newNumber));
        companyListService.save(company);
        return Message.success("add success");
    }

    @PostMapping("/addUploadCompanyData")
    @ResponseBody
    @CrossOrigin
    public Message addUploadCompanyData(HttpServletRequest request, @RequestPart MultipartFile file, @RequestPart String filename) throws IOException {
        String filetitle = filepath + "uploaddatafile" + File.separator + filename + ".xlsx";
        File file1 = new File(filetitle);
        file.transferTo(file1);
        return readexcel(filetitle);
    }

    public Message readexcel(String fileName) throws IOException {
        ArrayList<Companylist> companylists = new ArrayList<>();
        //将文件中的图片全部提取出来放在map里
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        Map<String, XSSFPictureData> picmap = getPictures(xssfWorkbook.getSheetAt(0));
        Integer[] count = {0};

        //获取数据库中最后一个公司的ID
        QueryWrapper<Companylist> companylistQueryWrapper = new QueryWrapper<>();
        companylistQueryWrapper.orderByDesc("id");
        companylistQueryWrapper.last("limit 0,1");
        List<Companylist> list = companyListService.list(companylistQueryWrapper);
        String number;
        if(list.size()==0){
            number = "20220101";
        }else{
            Companylist companylist = list.get(0);
            number = companylist.getNumber();
        }
        long[] newNumber=new long[]{Long.parseLong(number)+1};
        EasyExcel.read(fileName, Companylist.class, new PageReadListener<Companylist>(dataList -> {
            for (int i = 0; i < dataList.size(); i++) {
                Companylist companylist = dataList.get(i);
                companylist.setNumber(String.valueOf(newNumber[0]));
                newNumber[0]++;
                Integer row = i + count[0] + 1;
                String uid = UUID.randomUUID().toString().replace("-", "");
                if (picmap.get(row + "-0") != null) {
                    companylist.setLogo("[\"logo" + uid + ".jpg\"]");
                    XSSFPictureData xssfPictureData = picmap.get(row + "-0");
                    byte[] data = xssfPictureData.getData();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filepath + "CompanyLogo" + File.separator + "logo" + uid + ".jpg");
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
                companylists.add(companylist);
            }
            count[0] += dataList.size();
        })).sheet().headRowNumber(1).sheetNo(0).doRead();
        companyListService.saveBatch(companylists);
        return Message.success("read over");
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
}
