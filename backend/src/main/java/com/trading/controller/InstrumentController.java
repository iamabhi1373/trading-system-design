package com.trading.controller;

import com.trading.model.Instrument;
import com.trading.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InstrumentController {

    @Autowired
    private TradingService tradingService;

    @GetMapping("/instruments")
    public ResponseEntity<List<Instrument>> getInstruments() {
        return ResponseEntity.ok(tradingService.getAllInstruments());
    }
}
