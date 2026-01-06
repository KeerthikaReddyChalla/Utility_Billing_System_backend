package com.chubb.billing.service;

import com.chubb.billing.client.ConsumerClient;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.chubb.billing.client.MeterClient;
import com.chubb.billing.client.UtilityClient;
import com.chubb.billing.dto.*;
import com.chubb.billing.exception.BillingException;
import com.chubb.billing.exception.DependencyUnavailableException;
import com.chubb.billing.models.*;
import com.chubb.billing.repository.BillRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.chubb.billing.dto.ConsumerResponse;
import com.chubb.billing.dto.UtilityResponse;
import com.chubb.billing.event.OverdueBillReminderEvent;
import com.chubb.billing.dto.ConnectionResponse;



import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final MeterClient meterClient;
    private final ConsumerClient consumerClient;
    private final UtilityClient utilityClient;
    private final RabbitTemplate rabbitTemplate;

    
    public BillResponse generateBill(GenerateBillRequest request) {



        String connectionId = request.getConnectionId();
    

     
        MeterReadingResponse latest;
        try {
            latest = getLatestReading(connectionId);
        } catch (FeignException.NotFound e) {
            throw new BillingException("No meter reading exists for this connection");
        }


        MeterReadingResponse previous = null;
        try {
            previous = meterClient.getPrevious(connectionId);
        } catch (FeignException ex) {
            if (ex.status() != 204) {
                throw new DependencyUnavailableException(
                        "Meter service unavailable. Cannot fetch previous reading."
                );
            }
        }

        double unitsConsumed =
                (previous == null)
                        ? latest.getReadingValue()
                        : latest.getReadingValue() - previous.getReadingValue();

    

        
        ConnectionResponse connection;
        try {
            connection = consumerClient.getConnection(latest.getConnectionId());
        } catch (FeignException ex) {
            throw new DependencyUnavailableException(
                    "Consumer service unavailable. Cannot fetch connection."
            );
        }

        TariffType tariffType = connection.getTariffType();
      

 
        double amount = calculateAmount(tariffType, unitsConsumed);
    

        ConsumerResponse consumer;
        try {
            consumer = consumerClient.getConsumer(latest.getConsumerId());
        } catch (FeignException ex) {
            throw new DependencyUnavailableException(
                    "Consumer service unavailable."
            );
        }


        UtilityResponse utility;
        try {
            utility = utilityClient.getUtility(latest.getUtilityId());
        } catch (FeignException ex) {
            throw new DependencyUnavailableException(
                    "Utility service unavailable."
            );
        }


        Bill bill = Bill.builder()
                .connectionId(latest.getConnectionId())
                .consumerId(latest.getConsumerId())
                .utilityId(latest.getUtilityId())
                .consumerName(consumer.getFullName())
                .consumerEmail(consumer.getEmail())
                .utilityName(utility.getName())
                .billingCycle(request.getBillingCycle())
                .unitsConsumed(unitsConsumed)
                .amount(amount)
                .status(BillStatus.GENERATED)
                .build();

        Bill saved = billRepository.save(bill);


        return BillResponse.builder()
                .billId(saved.getId())
                .connectionId(saved.getConnectionId())
                .consumerId(saved.getConsumerId())
                .consumerName(saved.getConsumerName())
                .consumerEmail(saved.getConsumerEmail())
                .utilityName(saved.getUtilityName())
                .unitsConsumed(saved.getUnitsConsumed())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .billingCycle(saved.getBillingCycle())
                .build();
    }

    public List<Bill> getBillsByConsumer(String consumerId) {
        return billRepository.findByConsumerId(consumerId);
    }

    public void markBillAsPaid(String billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillingException("Bill not found"));

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }


    @CircuitBreaker(
            name = "meterService",
            fallbackMethod = "meterFallback"
    )
    public MeterReadingResponse getLatestReading(String connectionId) {
        return meterClient.getLatest(connectionId);
    }

    public MeterReadingResponse meterFallback(String connectionId, Throwable ex) {
        System.out.println("⚡ CIRCUIT BREAKER OPEN — meter-service DOWN");
        throw new DependencyUnavailableException(
                "Meter service unavailable. Cannot generate bill right now."
        );
    }

 
    private double calculateAmount(TariffType tariffType, double units) {
        return switch (tariffType) {

            case RESIDENTIAL_FLAT ->
                    units * 5.0 + 50;

            case RESIDENTIAL_STANDARD ->
                    units * 6.0 + 75;

            case COMMERCIAL_STANDARD ->
                    units * 8.0 + 150;

            case GOVT_SUBSIDIZED ->
                    units * 2.0;
        };
    }
   
    public List<ConnectionBillingViewDTO> getConnectionsWithoutReadings() {

        
        List<ConnectionResponse> connections =
                consumerClient.getAllConnections();

   
        Set<String> connectionsWithReadings =
                meterClient.getConnectionsWithReadings()
                        .stream()
                        .collect(Collectors.toSet());

      
        return connections.stream()
                .filter(c -> !connectionsWithReadings.contains(c.getId()))
                .map(c -> {

                    ConsumerResponse consumer;
                    UtilityResponse utility;

                    try {
                        consumer = consumerClient.getConsumer(c.getConsumerId());
                        utility = utilityClient.getUtility(c.getUtilityId());
                    } catch (Exception ex) {
                       
                        return null;
                    }
                    

                                	
                    return ConnectionBillingViewDTO.builder()
                            .connectionId(c.getId())
                            .consumerId(consumer.getId())
                            .consumerName(consumer.getFullName())
                            .utilityId(utility.getId())
                            .utilityName(utility.getName())
                            .tariffType(c.getTariffType())
                            .build();
                            
                            
                })
                .filter(Objects::nonNull) // remove failed rows
                .toList();
    }
    public void sendOverdueReminder(String billId) {

        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));

        OverdueBillReminderEvent event =
            new OverdueBillReminderEvent(
                bill.getId(),
                bill.getConsumerEmail(),
                bill.getAmount()
            );

        rabbitTemplate.convertAndSend(
            "billing.exchange",
            "billing.overdue.reminder",
            event
        );
    }
    

}
