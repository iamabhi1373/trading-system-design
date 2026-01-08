package com.trading.controller;

import com.trading.model.Trade;
import com.trading.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TradeController {

    @Autowired
    private TradingService tradingService;

    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getTrades() {
        return ResponseEntity.ok(tradingService.getAllTrades());
    }
}
