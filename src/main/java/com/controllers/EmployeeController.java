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
        receipt.setDelivered(true);
        receiptService.save(receipt);
        return "redirect:/employees";
    }
    @GetMapping("/history")
    public String getHistory(Model model){
        model.addAttribute("receipts",receiptService.getOrderedToday());
        return "employee/history";
    }
}
