package com.github.ehsan122.ca.services;

import com.github.ehsan122.ca.dto.AlertOut;
import com.github.ehsan122.ca.entities.Alert;
import com.github.ehsan122.ca.repositories.AlertRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;



    @Test
    public void get_WhenHaveNullIdShouldThrowException() {
        BDDMockito.given(alertRepository.findById(null)).willThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> alertService.get(null));
    }

    @Test
    public void getAll_WhenHaveNoAlertShouldReturnEmptyList() {
        BDDMockito.given(alertRepository.findAll()).willReturn(List.of());

        List<AlertOut> actualAlertOuts = alertService.getAll();

        assertNotNull(actualAlertOuts);
        assertEquals(actualAlertOuts.size(), 0);
    }

    @Test
    public void getAll_ShouldReturnList() {
        Alert alert1 = new Alert("ROLE_1", "BTC/USDT", 12.01, LocalDateTime.now());
        alert1.setId(1L);
        Alert alert2 = new Alert("ROLE_2", "ETH/USDT", 15.17, LocalDateTime.now());
        alert2.setId(2L);
        List<Alert> mockAlerts = List.of(alert1, alert2);
        BDDMockito.given(alertRepository.findAll()).willReturn(mockAlerts);
        List<AlertOut> expectedAlertOuts = List.of(
                new AlertOut(alert1.getId(), alert1.getRule(), alert1.getMarket(), alert1.getPrice(), alert1.getDateCalculated()),
                new AlertOut(alert2.getId(), alert2.getRule(), alert2.getMarket(), alert2.getPrice(), alert2.getDateCalculated())
        );

        List<AlertOut> actualAlertOuts = alertService.getAll();

        assertNotNull(actualAlertOuts);
        assertEquals(actualAlertOuts.size(), 2);
        assertEquals(actualAlertOuts.get(0), expectedAlertOuts.get(0));
        assertEquals(actualAlertOuts.get(1), expectedAlertOuts.get(1));
    }

    @Test
    public void convertMillisToDateTime_ShouldReturnLocalDateTime() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LocalDateTime expected = LocalDateTime.now();
        Long millis = expected.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Method convertMillisToDateTimeMethod = AlertService.class.getDeclaredMethod("convertMillisToDateTime", Long.class);
        convertMillisToDateTimeMethod.setAccessible(true);
        Object actual = convertMillisToDateTimeMethod.invoke(alertService, millis);
        convertMillisToDateTimeMethod.setAccessible(false);

        assertNotNull(actual);
        assertEquals(expected.getClass().getName(), actual.getClass().getName());
        assertEquals(expected.getYear(), ((LocalDateTime)actual).getYear());
        assertEquals(expected.getMonth(), ((LocalDateTime)actual).getMonth());
        assertEquals(expected.getDayOfMonth(), ((LocalDateTime)actual).getDayOfMonth());
        assertEquals(expected.getHour(), ((LocalDateTime)actual).getHour());
        assertEquals(expected.getMinute(), ((LocalDateTime)actual).getMinute());
        assertEquals(expected.getSecond(), ((LocalDateTime)actual).getSecond());
    }


    @Test
    public void convertToDto_ShouldReturnDto() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        LocalDateTime openDateTime = LocalDateTime.now();
        Alert alert = new Alert("ROLE_1", "BTC/USDT", 100.024, openDateTime);
        alert.setId(1L);
        AlertOut expected = new AlertOut(1, "ROLE_1", "BTC/USDT", 100.024 , openDateTime);

        Method convertToDtoMethod = AlertService.class.getDeclaredMethod("convertToDto", Alert.class);
        convertToDtoMethod.setAccessible(true);
        Object actual = convertToDtoMethod.invoke(alertService, alert);
        convertToDtoMethod.setAccessible(false);

        assertEquals(expected, actual);
    }

}