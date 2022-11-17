package com.ccwme.employeemanager.Controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ccwme.employeemanager.Bean.*;
import com.ccwme.employeemanager.Service.Employees.EmployeesService;
import com.ccwme.employeemanager.Service.EmployeesComments.EmployeesCommentsService;
import com.ccwme.employeemanager.Service.EmployeesCompany.EmployeesCompanyService;
import com.ccwme.employeemanager.Service.EmployeesEdu.EmployeesEduService;
import com.ccwme.employeemanager.Service.EmployeesResumes.EmployeesResumesService;
import com.ccwme.employeemanager.Util.Message;
import lombok.extern.log4j.Log4j2;

import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    EmployeesCommentsService employeesCommentsService;
    @Autowired
    EmployeesResumesService employeesResumesService;

    @ResponseBody
    @CrossOrigin
    @PostMapping("/CandidateList")
    public Message candidateList(@RequestBody Map reqbody) {
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
                //如果key是id筛选方式不是like而是eq
                if(searchkeystr.equals("id")){
                    employeesQueryWrapper.eq(searchkeystr, valsstring);
                }else{
                    employeesQueryWrapper.like(searchkeystr, valsstring);
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
                    employeesQueryWrapper.in((String) filterkey, filtervals);
                }
            }
        }
        employeesQueryWrapper.orderByDesc("id");
        long count = employeesService.count(employeesQueryWrapper);
        int i = (Integer.valueOf(page) - 1) * Integer.valueOf(pageSize);
        employeesQueryWrapper.last("limit " + i + ", " + pageSize);
        List<Employees> emplist = employeesService.list(employeesQueryWrapper);
        ArrayList<String> uids = new ArrayList<>();
        for (Employees employees : emplist) {
            uids.add(employees.getUid());
        }
        //获取每个人的历史就职公司数据
        QueryWrapper<EmployeesCompany> employeesCompanyQueryWrapper = new QueryWrapper<>();
        if (uids.size() != 0) {
            employeesCompanyQueryWrapper.in("uid", uids);
            employeesCompanyQueryWrapper.orderByDesc("starttime");
        }
        List<EmployeesCompany> empcompanylist = employeesCompanyService.list(employeesCompanyQueryWrapper);
        //获取每个人的comments数据
        QueryWrapper<EmployeesComments> employeesCommentsQueryWrapper = new QueryWrapper<>();
        if (uids.size() != 0) {
            employeesCommentsQueryWrapper.in("uid", uids);
            employeesCommentsQueryWrapper.orderByDesc("time");
        }
        List<EmployeesComments> employeesCommentsList = employeesCommentsService.list(employeesCommentsQueryWrapper);
        //获取每个人的Edu数据
        QueryWrapper<EmployeesEdu> employeesEduQueryWrappery = new QueryWrapper<>();
        if (uids.size() != 0) {
            employeesEduQueryWrappery.in("uid", uids);
            employeesEduQueryWrappery.orderByDesc("id");
        }
        List<EmployeesEdu> employeesEduList = employeesEduService.list(employeesEduQueryWrappery);

        JSONObject alldata = new JSONObject();
        JSONObject pagejsondata = new JSONObject();
        pagejsondata.put("total", count);
        alldata.put("data", emplist);
        alldata.put("empcompanylist", empcompanylist);
        alldata.put("employeesCommentsList", employeesCommentsList);
        alldata.put("employeesEduList", employeesEduList);
        alldata.put("pagedata", pagejsondata);
        return Message.success(alldata);
    }

    @PostMapping("/updateColData")
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

    @PostMapping("/reciveheadimg")
    @ResponseBody
    @CrossOrigin
    public Message reciveheadimg(@RequestPart MultipartFile file, @RequestPart String filename) throws IOException {
        File file1 = new File(filepath + "uploadtempfiles" + File.separator + filename + ".jpg");
        file.transferTo(file1);
        return Message.success("recivedimg");
    }

    @PostMapping("/removeheadimg")
    @ResponseBody
    @CrossOrigin
    public Message removeheadimg(@RequestBody Map param) throws IOException {
        File file = new File(filepath + "uploadtempfiles" + File.separator + param.get("filename") + ".jpg");
        if (file.exists()) {
            if (file.delete()) {
                return Message.success("deleted");
            }
            return Message.fail("file delete error");
        }
        return Message.fail("file not exist");
    }

    @ResponseBody
    @CrossOrigin
    @PostMapping("/addEmployeeOne")
    public Message addEmployeeOne(@RequestBody Map param) {
        String emp = (String) param.get("emp");
        String commentsList = (String) param.get("CommentsList");
        String companylist = (String) param.get("Companylist");
        String resumelist = (String) param.get("resume");
        String universitylist = (String) param.get("Universitylist");
        Employees emp1 = JSON.parseObject(emp, Employees.class);
        List<EmployeesComments> employeesCommentsList = JSON.parseArray(commentsList, EmployeesComments.class);
        List<EmployeesCompany> employeesCompanies = JSON.parseArray(companylist, EmployeesCompany.class);
        List<EmployeesResume> employeesResumes = JSON.parseArray(resumelist, EmployeesResume.class);
        List<EmployeesEdu> employeesEdus = JSON.parseArray(universitylist, EmployeesEdu.class);
        String uid = UUID.randomUUID().toString().replace("-", "");
        for (EmployeesComments employeesComments : employeesCommentsList) {
            employeesComments.setUid(uid);
        }
        for (EmployeesCompany employeesCompany : employeesCompanies) {
            employeesCompany.setUid(uid);
        }
        for (EmployeesResume employeesResume : employeesResumes) {
            employeesResume.setUid(uid);
            File file1 = new File(filepath + "uploadtempfiles" + File.separator + employeesResume.getFilename());
            if (file1.exists()) {
                file1.renameTo(new File(filepath + "ResumeFiles" + File.separator + employeesResume.getFilename()));
            } else {
                return Message.fail("missing resumeFiles");
            }
        }
        for (EmployeesEdu edus : employeesEdus) {
            edus.setUid(uid);
        }
        emp1.setUid(uid);
        String photo = emp1.getPhoto();
        for (Object o : JSON.parseArray(photo)) {
            String pic = (String) o;
            File file1 = new File(filepath + "uploadtempfiles" + File.separator + pic);
            if (file1.exists()) {
                file1.renameTo(new File(filepath + "EmployeesImages" + File.separator + pic));
            } else {
                return Message.fail("missing photos");
            }
        }
        employeesService.save(emp1);
        employeesResumesService.saveBatch(employeesResumes);
        employeesCommentsService.saveBatch(employeesCommentsList);
        employeesEduService.saveBatch(employeesEdus);
        employeesCompanyService.saveBatch(employeesCompanies);
        return Message.success(emp1);
    }

    @PostMapping("/addUploadData")
    @ResponseBody
    @CrossOrigin
    public Message addUploadData(HttpServletRequest request, @RequestPart MultipartFile file, @RequestPart String filename) throws IOException {
        String filetitle = filepath + "uploaddatafile" + File.separator + filename + ".xlsx";
        File file1 = new File(filetitle);
        file.transferTo(file1);
        return readexcel(filetitle);
    }

    public Message readexcel(String fileName) throws IOException {
        ArrayList<EmployeesCompany> employeeComList = new ArrayList<>();
        ArrayList<Employees> employeeList = new ArrayList<>();
        ArrayList<EmployeesEdu> employeeEduList = new ArrayList<>();
        ArrayList<EmployeesComments> employeesCommentsList = new ArrayList<>();
        EasyExcel.read(fileName, EmployeesCompany.class, new PageReadListener<EmployeesCompany>(dataList -> {
            for (EmployeesCompany demoData : dataList) {
                employeeComList.add(demoData);
            }
        })).sheet().headRowNumber(1).sheetNo(0).doRead();
        EasyExcel.read(fileName, EmployeesEdu.class, new PageReadListener<EmployeesEdu>(dataList -> {
            for (EmployeesEdu demoData : dataList) {
                employeeEduList.add(demoData);
            }
        })).sheet().headRowNumber(1).sheetNo(0).doRead();
        EasyExcel.read(fileName, EmployeesComments.class, new PageReadListener<EmployeesComments>(dataList -> {
            for (EmployeesComments demoData : dataList) {
                employeesCommentsList.add(demoData);
            }
        })).sheet().headRowNumber(1).sheetNo(0).doRead();

        //将文件中的图片全部提取出来放在map里
        File file = new File(fileName);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        Map<String, XSSFPictureData> picmap = getPictures(xssfWorkbook.getSheetAt(0));

        Integer[] count = {0};
        String[] lastuid1 = {null};
        EasyExcel.read(fileName, Employees.class, new PageReadListener<Employees>(dataList -> {
            String lastuid = lastuid1[0];
            for (int i = 0; i < dataList.size(); i++) {
                Employees employee = dataList.get(i);
                Integer row = i + count[0] + 1;
                if (employee.getName() != null) {
                    String uid = UUID.randomUUID().toString().replace("-", "");
                    lastuid = uid;
                    employee.setUid(uid);
                    if (picmap.get(row + "-0") != null) {
                        employee.setPhoto("[\"photo" + uid + ".jpg\"]");
                        XSSFPictureData xssfPictureData = picmap.get(row + "-0");
                        byte[] data = xssfPictureData.getData();
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(filepath + "EmployeesImages" + File.separator + "photo" + uid + ".jpg");
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
                    employeesCommentsList.get(i + count[0]).setUid(uid);
                } else {
                    employeeComList.get(i + count[0]).setUid(lastuid);
                    employeeEduList.get(i + count[0]).setUid(lastuid);
                    employeesCommentsList.get(i + count[0]).setUid(lastuid);
                }
                //公司logo
                if (picmap.get(row + "-11") != null) {
                    employeeComList.get(i + count[0]).setCompanylogo("[\"Companylogo" + lastuid + ".jpg\"]");
                    XSSFPictureData xssfPictureData = picmap.get(row + "-11");
                    byte[] data = xssfPictureData.getData();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filepath + "EmployeesImages" + File.separator + "Companylogo" + lastuid + ".jpg");
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
                    employeeEduList.get(i + count[0]).setUniversitylogo("[\"Universitylogo" + lastuid + ".jpg\"]");
                    XSSFPictureData xssfPictureData = picmap.get(row + "-17");
                    byte[] data = xssfPictureData.getData();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filepath + "EmployeesImages" + File.separator + "Universitylogo" + lastuid + ".jpg");
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
            lastuid1[0] = lastuid;
        })).sheet().headRowNumber(1).sheetNo(0).doRead();
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
        //删除comments列表中的无效数据
        for (int i = 0; i < employeesCommentsList.size(); i++) {
            if (employeesCommentsList.get(i).getComment() == null) {
                employeesCommentsList.remove(i);
                i--;
            }
        }
        employeesService.saveBatch(employeeList);
        employeesCompanyService.saveBatch(employeeComList);
        employeesEduService.saveBatch(employeeEduList);
        employeesCommentsService.saveBatch(employeesCommentsList);
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
}
