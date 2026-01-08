package com.trading.controller;

import com.trading.model.PortfolioHolding;
import com.trading.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PortfolioController {

    @Autowired
    private TradingService tradingService;

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioHolding>> getPortfolio() {
        return ResponseEntity.ok(tradingService.getPortfolio());
    }
}
