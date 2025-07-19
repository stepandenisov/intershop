package ru.yandex.payment.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BalanceService {

    private float balance;


    public BalanceService(){
        Random random = new Random();
        float min = 100.0F;
        float max = 1000.0F;
        this.balance = min + random.nextFloat() * (max - min);
    }

    public double getBalance(){
        return this.balance;
    }

    public void changeBalance(float value){
        this.balance = this.balance - value;
    }
}
