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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeesCompanyController {
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
    @PostMapping("/likeEmployess")
    @CrossOrigin
    public Message likeEmployess(@RequestBody Map param){
        String id = (String)param.get("id");

        //年龄相似
        QueryWrapper<Employees> employeesQueryWrapper = new QueryWrapper<>();
        employeesQueryWrapper.eq("id",id);
        Employees emp = employeesService.list(employeesQueryWrapper).get(0);
        String birth = emp.getBirth();
        String year = birth.substring(0, 4);
        int yearnum = Integer.parseInt(year);
        QueryWrapper<Employees> employeesQueryWrapper1 = new QueryWrapper<>();
        for (int i = yearnum-5; i < yearnum+5; i++) {
            employeesQueryWrapper1.like("birth",Integer.toString(i)).or();
        }
        List<Employees> employeesList = employeesService.list(employeesQueryWrapper1);
        ArrayList<String> uids = new ArrayList<>();
        for (Employees employees : employeesList) {
            uids.add(employees.getUid());
        }

        //学历相同
        QueryWrapper<EmployeesEdu> employeesEduQueryWrapper = new QueryWrapper<>();
        employeesEduQueryWrapper.eq("id", id);
        EmployeesEdu employeesEdu = employeesEduService.list(employeesEduQueryWrapper).get(0);
        QueryWrapper<EmployeesEdu> employeesEduQueryWrapper1 = new QueryWrapper<>();
        employeesEduQueryWrapper1.eq("degree", employeesEdu.getDegree());
        ArrayList<String> fuids = new ArrayList<>();
        for (EmployeesEdu edu : employeesEduService.list(employeesEduQueryWrapper1)) {
            for (String uid : uids) {
                if (uid.equals(edu.getUid())) {
                    fuids.add(uid);
                }
            }
        }

        //重新获取每个人数据
        QueryWrapper<Employees> employeesQueryWrapper2 = new QueryWrapper<>();
        if (fuids.size() != 0) {
            employeesQueryWrapper2.in("uid", fuids);
        }
        List<Employees> emplist = employeesService.list(employeesQueryWrapper2);

        //获取每个人的历史就职公司数据
        QueryWrapper<EmployeesCompany> employeesCompanyQueryWrapper = new QueryWrapper<>();
        if (fuids.size() != 0) {
            employeesCompanyQueryWrapper.in("uid", fuids);
        }
        List<EmployeesCompany> empcompanylist = employeesCompanyService.list(employeesCompanyQueryWrapper);

        //获取每个人的Edu数据
        QueryWrapper<EmployeesEdu> employeesEduQueryWrappery = new QueryWrapper<>();
        if (fuids.size() != 0) {
            employeesEduQueryWrappery.in("uid", fuids);
            employeesEduQueryWrappery.orderByDesc("id");
        }
        List<EmployeesEdu> employeesEduList = employeesEduService.list(employeesEduQueryWrappery);

        JSONObject alldata = new JSONObject();
        alldata.put("data", emplist);
        alldata.put("empcompanylist", empcompanylist);
        alldata.put("employeesEduList", employeesEduList);
        return Message.success(alldata);

    }
}
