package com.example.gettour_api;

import com.example.gettour_api.utils.RequestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
class GetTourApiApplicationTests {

    @Test
    public void requestDeadlineCalcTest(){
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("09:21"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:21"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("15:56"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"13:56"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("20:15"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"17:00"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("05:35"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:00"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("11:35"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"09:35"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("00:00"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:00"));
    }

    @Test
    void contextLoads() {
    }

}