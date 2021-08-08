package com.example.gettour_api;

import com.example.gettour_api.enums.RequestStatus;
import com.example.gettour_api.exceptions.CompanyExistsException;
import com.example.gettour_api.exceptions.EmailExistsException;
import com.example.gettour_api.exceptions.RequestNotFoundException;
import com.example.gettour_api.exceptions.UserNotFoundException;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.models.Request;
import com.example.gettour_api.repositories.AppUserRepository;
import com.example.gettour_api.repositories.RequestRepository;
import com.example.gettour_api.services.AccountServiceImpl;
import com.example.gettour_api.services.AppUserService;
import com.example.gettour_api.services.JwtUserDetailsService;
import com.example.gettour_api.services.interfaces.AccountService;
import com.example.gettour_api.utils.EmailValidator;
import com.example.gettour_api.utils.HttpRequestUtil;
import com.example.gettour_api.utils.RequestUtil;
import com.example.gettour_api.utils.jwt.JwtTokenUtil;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.Rollback;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

@DataJpaTest
class GetTourApiApplicationTests {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void getUserMailFromHeaderTest(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Assertions.assertEquals(HttpRequestUtil.getUserMailFromHeader(request,"Authorization"), "shafigtahmasib@gmail.com");
    }

    @Test
    void registeringWithExistingTourCompanyTest(){
        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        try {
            new AppUserService(appUserRepository, null, null).signUpUser(AppUser.builder().email(email).build());
        }catch (EmailExistsException emailExistsException){
            Assertions.assertEquals("Email is already taken", emailExistsException.getMessage());
        }
    }

    @Test
    void registeringWithExistingEmailTest(){
        String email = "test@gmail.com";
        String companyName = "MyTour";

        AppUser user = AppUser.builder().email(email).companyName(companyName).build();
        appUserRepository.save(user);

        try {
            new AppUserService(appUserRepository, null, null).signUpUser(AppUser.builder().email("test1@gmail.com").companyName(companyName).build());
        }catch (CompanyExistsException companyExistsException){
            Assertions.assertEquals("Company is already registered", companyExistsException.getMessage());
        }
    }

    @Test
    void emailValidatorTest(){
        Assertions.assertEquals(false, new EmailValidator().test("asd"));
        Assertions.assertEquals(true, new EmailValidator().test("shafigtahmasib@gmail.com"));
        Assertions.assertEquals(false, new EmailValidator().test("123"));
    }

    @Test
    void requestDeadlineCalcTest(){
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("09:21"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:21"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("15:56"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"13:56"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("20:15"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"17:00"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("05:35"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:00"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("11:35"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now().plusDays(1)+"T"+"09:35"));
        Assertions.assertEquals(RequestUtil.getDeadLine(LocalTime.parse("00:00"),"09:00", "19:00", 8), LocalDateTime.parse(LocalDate.now()+"T"+"17:00"));
    }

    @Test
    void findByEmailTest() {

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        AppUser expectedUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " +email));
        AssertionsForInterfaceTypes.assertThat(expectedUser).isEqualTo(user);
    }

    @Test
    void findAppUserByIdTest() {

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        AppUser expectedUser = appUserRepository.findAppUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        AssertionsForInterfaceTypes.assertThat(expectedUser).isEqualTo(user);
    }

    @Test
    void getAppUserByCompanyNameTest() {

        String companyName = "MyTour";

        AppUser user = AppUser.builder().email("test@gmail.com").companyName(companyName).build();
        appUserRepository.save(user);

        AppUser expectedUser = appUserRepository.getAppUserByCompanyName(companyName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        AssertionsForInterfaceTypes.assertThat(expectedUser).isEqualTo(user);
    }

    @Test
    void getRequestByIdAndAgent_IdTest(){

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        appUserRepository.save(user);

        Request request = Request.builder().status(RequestStatus.ACCEPTED).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();

        requestRepository.save(request);

        Request expectedRequest = requestRepository.getRequestByIdAndAgent_Id(request.getId(), request.getAgent().getId())
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        AssertionsForInterfaceTypes.assertThat(expectedRequest).isEqualTo(request);
    }

    @Test
    void getRequestByAgent_IdAndDataContainsTest(){

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        appUserRepository.save(user);

        Request request = Request.builder()
                .data("{\"travelEndDate\":\"2021-05-08\",\"telegramIdentifier\":\"3e155365-b40c-43aa-8524-80e4b3b2f1fc\",\"tourType\":\"İstirahət-gəzinti\",\"travelStartDate\":\"2021-05-05\",\"language\":\"AZ\",\"travellerCount\":\"4\",\"addressFrom\":\"Baku\",\"addressTo\":\"GetTour təklif etsin\",\"budget\":\"64\"}")
                .isArchived(true).status(RequestStatus.NEW_REQUEST)
                .agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request);

        Request expectedRequest = requestRepository.getRequestByAgent_IdAndDataContains(request.getAgent().getId(), "3e155365-b40c-43aa-8524-80e4b3b2f1fc")
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        AssertionsForInterfaceTypes.assertThat(expectedRequest).isEqualTo(request);
    }

    @Test
    void getRequestByAgent_IdAndIsArchivedIsTrueTest(){

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        Request request1 = Request.builder().isArchived(true).status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request1);


        Request request2 = Request.builder().isArchived(true).status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request2);

        Request request3 = Request.builder().isArchived(false).status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request3);

        List<Request> requestList = requestRepository.getRequestByAgent_IdAndIsArchivedIsTrue(user.getId());
        AssertionsForInterfaceTypes.assertThat(requestList).isEqualTo(Arrays.asList(request1, request2));

    }

    @Test
    void getRequestByAgent_IdAndStatusTest(){

        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        Request request1 = Request.builder().status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request1);


        Request request2 = Request.builder().status(RequestStatus.ACCEPTED).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request2);

        Request request3 = Request.builder().status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request3);

        List<Request> requestList = requestRepository.getRequestByAgent_IdAndStatus(user.getId(), RequestStatus.NEW_REQUEST);
        AssertionsForInterfaceTypes.assertThat(requestList).isEqualTo(Arrays.asList(request1, request3));
    }

    @Test
    void getAllByAgent_IdTest(){
        String email = "test@gmail.com";

        AppUser user = AppUser.builder().email(email).companyName("MyTour").build();
        appUserRepository.save(user);

        Request request1 = Request.builder().status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request1);


        Request request2 = Request.builder().status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request2);

        Request request3 = Request.builder().status(RequestStatus.NEW_REQUEST).agent(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"))).build();
        requestRepository.save(request3);

        List<Request> requestList = requestRepository.getAllByAgent_Id(user.getId());
        AssertionsForInterfaceTypes.assertThat(requestList).isEqualTo(Arrays.asList(request1, request2, request3));

    }
}