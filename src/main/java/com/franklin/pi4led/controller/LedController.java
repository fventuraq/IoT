package com.franklin.pi4led.controller;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController //ADD SERVIS REST FOR CONTROLLER
public class LedController {

    private static GpioPinDigitalOutput pin;
    private static I2CBus SemTemp;

    @RequestMapping("/") //PUERTO SIN APLICATION
    public String greeting(){
        return "Welcome Franklin";
    }

    @RequestMapping("/light")
    public String light(){

        if(pin == null){
            System.out.println("Entro aca ...");
            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
        }

        System.out.println("ESTADO DEL PIN: "+pin.getState().toString());

        pin.toggle();

        return "Ok";
    }

    @RequestMapping("/ledoffon")
    public String offon() throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        GpioController gpio2 = GpioFactory.getInstance();
        pin = gpio2.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED",PinState.LOW);

        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        I2CDevice device = bus.getDevice(0x48);

        device.write(0x01, (byte) 0x60);
        Thread.sleep(1000);

        byte[] data = new byte[2];
        device.read(0x00, data, 0, 2);

        //covertimos a temperatura a 12-bits
        int temp = ((((data[0] & 0xFF)*256) +(data[1]&0xF0))/16);
        if(temp>2047){
            temp-=4096;
        }

        double temCel = temp*0.0625;

        System.out.println("TEMPERATURA E: "+temCel);

        //Si a temperatura e maior a 30 o pin se activa

        if(temCel>30){
            pin.setState(PinState.HIGH);
        }else pin.setState(PinState.LOW);

        return pin.getState().toString();
    }
}
