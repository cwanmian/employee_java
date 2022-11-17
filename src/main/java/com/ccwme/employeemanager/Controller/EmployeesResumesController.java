package com.ccwme.employeemanager.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccwme.employeemanager.Bean.Employees;
import com.ccwme.employeemanager.Bean.EmployeesCompany;
import com.ccwme.employeemanager.Bean.EmployeesEdu;
import com.ccwme.employeemanager.Service.Employees.EmployeesService;
import com.ccwme.employeemanager.Service.EmployeesComments.EmployeesCommentsService;
import com.ccwme.employeemanager.Service.EmployeesCompany.EmployeesCompanyService;
import com.ccwme.employeemanager.Service.EmployeesEdu.EmployeesEduService;
import com.ccwme.employeemanager.Util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeesResumesController {
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
    @ResponseBody
    @PostMapping("/reciveResumes")
    @CrossOrigin
    public Message reciveResumes(@RequestPart MultipartFile file, @RequestPart String filename) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String[] split = originalFilename.split("\\.");
        String ext = split[split.length-1];
        File file1 = new File(filepath + "uploadtempfiles" + File.separator + filename + "."+ext);
        file.transferTo(file1);
        return Message.success("recivedfile");
    }
    @PostMapping("/removeResumes")
    @ResponseBody
    @CrossOrigin
    public Message removeResumes(@RequestBody Map param) throws IOException {
        File file = new File(filepath + "uploadtempfiles" + File.separator + param.get("filename"));
        if (file.exists()) {
            if (file.delete()) {
                return Message.success("deleted");
            }
            return Message.fail("file delete error");
        }
        return Message.fail("file not exist");
    }
}
