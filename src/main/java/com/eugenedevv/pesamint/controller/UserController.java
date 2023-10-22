package com.eugenedevv.pesamint.controller;

import com.eugenedevv.pesamint.dto.BankResponse;
import com.eugenedevv.pesamint.dto.CreditDebitRequest;
import com.eugenedevv.pesamint.dto.EnquiryRequest;
import com.eugenedevv.pesamint.dto.UserRequest;
import com.eugenedevv.pesamint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public  ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest) {


        final BankResponse response =  userService.createAccount(userRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/detailsEnquiry")
    public ResponseEntity<BankResponse> detailsEnquiry(@RequestBody EnquiryRequest request){

        final BankResponse response =  userService.detailsEnquiry(request);
        return new ResponseEntity<>(response, response.getHttpStatus());

    }

    @PostMapping("/credit")
    @ResponseBody
    public ResponseEntity<BankResponse> creditAccount(@RequestBody CreditDebitRequest request){
        final BankResponse response =  userService.creditAccount(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
    @PostMapping("/debit")
    @ResponseBody
    public ResponseEntity<BankResponse> debitAccount(@RequestBody CreditDebitRequest request){
        final BankResponse response = userService.debitAccount(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
