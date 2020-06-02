package com.bfwg.rest;

import com.bfwg.dto.OrderCarDto;
import com.bfwg.dto.OrderTourDto;
import com.bfwg.model.OrderCar;
import com.bfwg.model.OrderTour;
import com.bfwg.repository.OrderCarRepository;
import com.bfwg.repository.OrderTourRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(value = "/paypal")
public class PayPalController {
    @Autowired
    private OrderTourRepository orderTourRepository;
    @Autowired
    private OrderCarRepository orderCarRepository;

    String clientId = "AdV4zJPzyBY0p84YDB0hIUk-ENOlKk6iRLzhP50X0SiyNF5CMbIjdB_WqMUxlYulxn0bh8RyTMTDwwwp";
    String clientSecret = "ELtdAu7zw5LTS9YpK82M4UYPp-7SpILx5phFQsuPglEeTL7WvZAhBOYxmgpc3W5N9gBJ6MfY8tFMISZR";

    @PreAuthorize("hasRole('USER')")
    public Map<String, Object> createPayment(String sum){
        Map<String, Object> response = new HashMap<String, Object>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("https://wlink.netlify.com/cancel");
        redirectUrls.setReturnUrl("https://wlink.netlify.com/");
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;
        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            createdPayment = payment.create(context);
            if(createdPayment!=null){
                List<Links> links = createdPayment.getLinks();
                for (Links link:links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error happened during payment creation!");
        }
        return response;
    }
    public Payment completePaymentSuccess(String paymentId, String payerId){
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
            return createdPayment;
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/make/payment")
    public Map<String, Object> makePayment(@RequestParam("sum") String sum,Principal user) {
        return createPayment(sum);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/complete/payment")
    public ResponseEntity<Object> completePayment(@RequestParam("paymentId") String paymentId , @RequestParam("PayerID") String payerId , @RequestParam("token") String token){
        Payment payment = this.completePaymentSuccess(paymentId,payerId);
        if (payment == null){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.FORBIDDEN.value())
                    .setMessage("Fails!")
                    .setData(null)
                    .build(), HttpStatus.FORBIDDEN);
        }
        OrderTour orderTour = orderTourRepository.findByToken(token);

        if (orderTour == null){
            OrderCar orderCar = orderCarRepository.findByToken(token);
            DateTime date = new DateTime();
            orderCar.setStatus(2);
            orderCar.setDate(date.getMillis());

            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.OK.value())
                    .setMessage("Success!")
                    .setData(
                            new OrderCarDto(orderCarRepository.save(orderCar))
                    )
                    .build(), HttpStatus.OK);
        }
        DateTime date = new DateTime();

        orderTour.setStatus(2);
        orderTour.setDate(date.getMillis());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(
                        new OrderTourDto(orderTourRepository.save(orderTour))
                )
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/complete/payment/car")
    public ResponseEntity<Object> completePaymentCar(@RequestParam("paymentId") String paymentId , @RequestParam("PayerID") String payerId , @RequestParam("token") String token){
        Payment payment = this.completePaymentSuccess(paymentId,payerId);
        if (payment == null){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.FORBIDDEN.value())
                    .setMessage("Fails!")
                    .setData(null)
                    .build(), HttpStatus.FORBIDDEN);
        }
        OrderCar orderCar = orderCarRepository.findByToken(token);
        if (orderCar == null){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("ORDER CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        DateTime date = new DateTime();
        orderCar.setStatus(2);
        orderCar.setDate(date.getMillis());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(
                        new OrderCarDto(orderCarRepository.save(orderCar))
                )
                .build(), HttpStatus.OK);
    }



}

