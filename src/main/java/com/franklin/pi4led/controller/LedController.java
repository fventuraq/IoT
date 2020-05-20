package com.franklin.pi4led.controller;

import com.pi4j.io.gpio.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //ADD SERVIS REST FOR CONTROLLER
public class LedController {

    private static GpioPinDigitalOutput pin;

    @RequestMapping("/") //PUERTO SIN APLICATION
    public String greeting(){
        return "Welcome Franklin";
    }

    @RequestMapping("/light")
    public String light(){
        if(pin == null){
            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
        }

        pin.toggle();

        return "Ok";
    }
}
