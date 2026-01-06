package com.chubb.billing.service;

import org.springframework.test.util.ReflectionTestUtils;

import com.chubb.billing.client.ConsumerClient;
import com.chubb.billing.client.MeterClient;
import com.chubb.billing.client.UtilityClient;
import com.chubb.billing.dto.*;
import com.chubb.billing.exception.BillingException;
import com.chubb.billing.exception.DependencyUnavailableException;
import com.chubb.billing.models.*;
import com.chubb.billing.repository.BillRepository;

import feign.FeignException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private MeterClient meterClient;

    @Mock
    private ConsumerClient consumerClient;

    @Mock
    private UtilityClient utilityClient;

    @Mock
    private RabbitTemplate rabbitTemplate;  

    @InjectMocks
    private BillingService service;    
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                service,
                "rabbitTemplate",
                rabbitTemplate
        );
    }

    // ---------------- TESTS ----------------

    @Test
    void markBillAsPaid_success() {

        Bill bill = Bill.builder()
                .id("b1")
                .status(BillStatus.GENERATED)
                .build();

        when(billRepository.findById("b1"))
                .thenReturn(Optional.of(bill));

        service.markBillAsPaid("b1");

        assertThat(bill.getStatus()).isEqualTo(BillStatus.PAID);
        verify(billRepository).save(bill);
    }

    @Test
    void getAllBills_success() {

        when(billRepository.findAll())
                .thenReturn(List.of(new Bill()));

        assertThat(service.getAllBills()).hasSize(1);
    }

    @Test
    void getConnectionsWithoutReadings_success() {

        ConnectionResponse connection =
                new ConnectionResponse(
                        "conn1", "c1", "u1",
                        TariffType.RESIDENTIAL_FLAT,
                        "ACTIVE"
                );

        when(consumerClient.getAllConnections())
                .thenReturn(List.of(connection));

        when(meterClient.getConnectionsWithReadings())
                .thenReturn(List.of()); 

        ConsumerResponse consumer =
                ConsumerResponse.builder()
                        .id("c1")
                        .fullName("John")
                        .email("john@mail.com")
                        .build();

        when(consumerClient.getConsumer("c1"))
                .thenReturn(consumer);

        UtilityResponse utility =
                UtilityResponse.builder()
                        .id("u1")
                        .name("Electricity")
                        .build();

        when(utilityClient.getUtility("u1"))
                .thenReturn(utility);

        List<ConnectionBillingViewDTO> result =
                service.getConnectionsWithoutReadings();

        assertThat(result).hasSize(1);
    }

    @Test
    void sendOverdueReminder_success() {

        Bill bill = Bill.builder()
                .id("b1")
                .consumerEmail("a@test.com")
                .amount(500.0)
                .build();

        when(billRepository.findById("b1"))
                .thenReturn(Optional.of(bill));

        service.sendOverdueReminder("b1");

        verify(rabbitTemplate).convertAndSend(
                eq("billing.exchange"),
                eq("billing.overdue.reminder"),
                any(Object.class)
        );
    }
    @Test
    void getBillsByConsumer_success() {

        when(billRepository.findByConsumerId("c1"))
                .thenReturn(List.of(new Bill()));

        List<Bill> bills = service.getBillsByConsumer("c1");

        assertThat(bills).hasSize(1);
    }

    @Test
    void markBillAsPaid_billNotFound() {

        when(billRepository.findById("b1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markBillAsPaid("b1"))
                .isInstanceOf(BillingException.class);
    }

    @Test
    void generateBill_success() {

        GenerateBillRequest request = GenerateBillRequest.builder()
                .connectionId("conn1")
                .billingCycle(java.time.LocalDate.now())
                .build();

        MeterReadingResponse latest = MeterReadingResponse.builder()
                .connectionId("conn1")
                .consumerId("c1")
                .utilityId("u1")
                .readingValue(150)
                .build();

        when(meterClient.getLatest("conn1"))
                .thenReturn(latest);

        FeignException feign204 = mock(FeignException.class);
        when(feign204.status()).thenReturn(204);

        when(meterClient.getPrevious("conn1"))
                .thenThrow(feign204);


        ConnectionResponse connection =
                new ConnectionResponse(
                        "conn1", "c1", "u1",
                        TariffType.RESIDENTIAL_FLAT,
                        "ACTIVE"
                );

        when(consumerClient.getConnection("conn1"))
                .thenReturn(connection);

        ConsumerResponse consumer =
                ConsumerResponse.builder()
                        .id("c1")
                        .fullName("John")
                        .email("john@mail.com")
                        .build();

        when(consumerClient.getConsumer("c1"))
                .thenReturn(consumer);

        UtilityResponse utility =
                UtilityResponse.builder()
                        .id("u1")
                        .name("Electricity")
                        .build();

        when(utilityClient.getUtility("u1"))
                .thenReturn(utility);

        when(billRepository.save(any()))
                .thenAnswer(i -> {
                    Bill b = i.getArgument(0);
                    b.setId("b1");
                    return b;
                });

        BillResponse response = service.generateBill(request);

        assertThat(response.getBillId()).isEqualTo("b1");
        assertThat(response.getAmount()).isGreaterThan(0);
    }

    @Test
    void generateBill_noMeterReading() {

        GenerateBillRequest request = GenerateBillRequest.builder()
                .connectionId("conn1")
                .billingCycle(java.time.LocalDate.now())
                .build();

        when(meterClient.getLatest("conn1"))
                .thenThrow(mock(feign.FeignException.NotFound.class));

        assertThatThrownBy(() -> service.generateBill(request))
                .isInstanceOf(BillingException.class);
    }

    
    @Test
    void meterFallback_throwsException() {

        assertThatThrownBy(() ->
                service.meterFallback("conn1", new RuntimeException("down"))
        ).isInstanceOf(DependencyUnavailableException.class);
    }

    @Test
    void sendOverdueReminder_billNotFound() {

        when(billRepository.findById("b1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendOverdueReminder("b1"))
                .isInstanceOf(RuntimeException.class);
    }


}
