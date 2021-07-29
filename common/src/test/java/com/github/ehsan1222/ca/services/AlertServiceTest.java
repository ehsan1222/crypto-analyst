package com.github.ehsan1222.ca.services;

import com.github.ehsan1222.ca.dto.AlertOut;
import com.github.ehsan1222.ca.entities.Alert;
import com.github.ehsan1222.ca.exceptions.AlertNotFoundException;
import com.github.ehsan1222.ca.repositories.AlertRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    public void save_ShouldReturnSavedAlert() {

        Alert alert = new Alert("ROLE_1", "BTC/USDT", 12.45, LocalDateTime.now());
        alert.setId(2L);
        BDDMockito.given(alertRepository.save(any(Alert.class))).willReturn(alert);

        AlertOut result = alertService.save("ROLE_1", "BTC/USDT", 12.45, System.currentTimeMillis());

        assertNotNull(result);
        assertEquals(result.getId(), alert.getId());
        assertEquals(result.getRule(), alert.getRule());
        assertEquals(result.getMarket(), alert.getMarket());
        assertEquals(result.getOpenDateTime(), alert.getDateCalculated());
        Mockito.verify(alertRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void save_GivenBlankMarketShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> alertService.save("ROLE_1", "  ", 10.1, System.currentTimeMillis()));
    }

    @Test
    public void save_GivenBlankRuleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> alertService.save("  ", null, 10.1, System.currentTimeMillis()));
    }

    @Test
    public void save_GivenNullMarketShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> alertService.save("ROLE_1", null, 10.1, System.currentTimeMillis()));
    }

    @Test
    public void save_GivenNullRuleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> alertService.save(null, "BTC/USDT", 10.1, System.currentTimeMillis()));
    }

    @Test
    public void get_GivenNotExistedAlertShouldThrowException() {
        BDDMockito.given(alertRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

        assertThrows(AlertNotFoundException.class, () -> alertService.get(1L));
    }


    @Test
    public void get_GivenExistedAlertShouldReturnAlertOut() {
        Alert alert = new Alert("ROLE_1", "BTC/USDT", 12.01, LocalDateTime.now());
        alert.setId(1L);
        BDDMockito.given(alertRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(alert));

        AlertOut actual = alertService.get(1L);

        assertNotNull(actual);
        assertEquals(alert.getId(), actual.getId());
        assertEquals(alert.getRule(), actual.getRule());
        assertEquals(alert.getMarket(), actual.getMarket());
        assertEquals(alert.getPrice(), actual.getPrice());
        assertEquals(alert.getDateCalculated(), actual.getOpenDateTime());
    }

    @Test
    public void get_GivenNullIdShouldThrowException() {
        BDDMockito.given(alertRepository.findById(null)).willThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> alertService.get(null));
    }

    @Test
    public void getAll_ShouldReturnEmptyList() {
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
        LocalDateTime expected = LocalDateTime.of(2020, 12, 15, 16, 32, 13);
        Long millis = expected.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Method convertMillisToDateTimeMethod = AlertService.class.getDeclaredMethod("convertMillisToDateTime", Long.class);
        convertMillisToDateTimeMethod.setAccessible(true);
        Object actual = convertMillisToDateTimeMethod.invoke(alertService, millis);
        convertMillisToDateTimeMethod.setAccessible(false);

        assertNotNull(actual);
        assertEquals(expected.getClass().getName(), actual.getClass().getName());
        assertEquals(2020, ((LocalDateTime)actual).getYear());
        assertEquals(12, ((LocalDateTime)actual).getMonth().getValue());
        assertEquals(15, ((LocalDateTime)actual).getDayOfMonth());
        assertEquals(16, ((LocalDateTime)actual).getHour());
        assertEquals(32, ((LocalDateTime)actual).getMinute());
        assertEquals(13, ((LocalDateTime)actual).getSecond());
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