package ru.yandex.payment.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BalanceService {

    private final Map<Long, Float> balances = new HashMap<>();


    public BalanceService(){
        Random random = new Random();
        float min = 100.0F;
        float max = 1000.0F;
        balances.put(1L, min + random.nextFloat() * (max - min));
        balances.put(2L, 0.0F);
    }

    public double getBalance(Long id){
        return balances.get(id);
    }

    public void changeBalance(Long id, float value){
        balances.put(id, balances.get(id) - value);
    }
}
