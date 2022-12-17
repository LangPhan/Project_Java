package com.controllers;

import com.models.Receipt;
import com.services.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

@Controller
@RequestMapping("employees")
public class EmployeeController {
    @Autowired
    private ReceiptService receiptService;
    @GetMapping("")
    public String getIndex(Model model){
        model.addAttribute("receipts",receiptService.getOrder());
        return "employee/index";
    }
    @PostMapping("/delivered/{id}")
    public String postDelivered(@PathVariable Long id){
        Receipt receipt = receiptService.findById(id).get();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        receipt.setUpdateAt(dateFormat.format(date));
        receipt.setDelivered(true);
        receiptService.save(receipt);
        return "redirect:/employees";
    }
    @GetMapping("/history")
    public String getHistory(Model model){
        model.addAttribute("receipts",receiptService.getOrderedToday()
                .stream().sorted(Comparator.comparing(Receipt::getUpdateAt).reversed()).collect(Collectors.toList()));
        return "employee/history";
    }
}
