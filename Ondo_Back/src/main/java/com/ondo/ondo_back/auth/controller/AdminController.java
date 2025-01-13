package com.ondo.ondo_back.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class AdminController {

    @GetMapping("/admin")
    public String admin() {

        return "admin controller";
    }
}
