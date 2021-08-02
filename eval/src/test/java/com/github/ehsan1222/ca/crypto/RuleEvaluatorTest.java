package com.github.ehsan1222.ca.crypto;

import com.binance.api.client.domain.market.Candlestick;
import com.github.ehsan1222.ca.dao.Pattern;
import com.github.ehsan1222.ca.dao.PatternCheck;
import com.github.ehsan1222.ca.dao.PatternType;
import com.github.ehsan1222.ca.services.AlertService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class RuleEvaluatorTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private RuleEvaluator ruleEvaluator;

    @Test
    public void evaluate_GivenFirstGraterLastAndGreaterCheck() {
        Pattern pattern = Mockito.mock(Pattern.class);
        List<Candlestick> candlesticks = Mockito.mock(List.class);
        ruleEvaluator = Mockito.spy(ruleEvaluator);
        BDDMockito.given(pattern.getType()).willReturn(PatternType.LOW);
        BDDMockito.given(pattern.getCheck()).willReturn(PatternCheck.GREATER_THAN);
        Mockito.doReturn(true).when(ruleEvaluator).haveEnoughCandlesticksItems(Mockito.any(), Mockito.any());
        BDDMockito.given(pattern.getFirstInterval()).willReturn(1);
        BDDMockito.given(pattern.getLastInterval()).willReturn(2);
        Mockito.doReturn(1000.0).when(ruleEvaluator).getMeanValue(eq(candlesticks), eq(1), any(PatternType.class));
        Mockito.doReturn(1001.0).when(ruleEvaluator).getMeanValue(eq(candlesticks), eq(2), any(PatternType.class));
        Mockito.doReturn(false).when(ruleEvaluator).isEvaluate(eq(1000.0), eq(1001.0), any());

        ruleEvaluator.evaluate(candlesticks, pattern);

        Mockito.verify(candlesticks, Mockito.times(1)).size();
    }

    @Test
    public void evaluate_GivenNotEnoughCandlestickItems() {
        Pattern pattern = Mockito.mock(Pattern.class);
        List<Candlestick> candlesticks = Mockito.mock(List.class);
        ruleEvaluator = Mockito.spy(ruleEvaluator);
        Mockito.doReturn(false).when(ruleEvaluator).haveEnoughCandlesticksItems(Mockito.any(), Mockito.any());

        ruleEvaluator.evaluate(candlesticks, pattern);

        Mockito.verify(ruleEvaluator, Mockito.never()).getMeanValue(any(List.class), anyInt(), any(PatternType.class));
        Mockito.verify(ruleEvaluator, Mockito.never()).isEvaluate(anyDouble(), anyDouble(), any());
    }

    @Test
    public void evaluate_GivenNullPattern() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Pattern pattern = null;
        List<Candlestick> candlesticks = Mockito.mock(List.class);
        ruleEvaluator = Mockito.spy(ruleEvaluator);

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("evaluate", List.class, Pattern.class);
        getValueMethod.setAccessible(true);
        getValueMethod.invoke(ruleEvaluator, candlesticks, pattern);
        getValueMethod.setAccessible(false);

        Mockito.verify(ruleEvaluator, Mockito.never()).haveEnoughCandlesticksItems(any(), any());
    }

    @Test
    public void getMeanValue_GivenIntervalTwoShouldReturnLastTwoCandlesticksItems() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Candlestick c1 = new Candlestick();
        c1.setLow("1001");
        Candlestick c2 = new Candlestick();
        c2.setLow("1002");
        Candlestick c3 = new Candlestick();
        c3.setLow("1003");

        List<Candlestick> candlesticks = List.of(c1, c2, c3);

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getMeanValue",
                List.class, int.class, PatternType.class);
        getValueMethod.setAccessible(true);
        Object actual = getValueMethod.invoke(ruleEvaluator, candlesticks, 2, PatternType.LOW);
        getValueMethod.setAccessible(false);

        Assertions.assertEquals(1002.5, actual);
    }

    @Test
    public void getMeanValue_GivenBiggerIntervalRangeOfCandlesticksShouldThrowException() {
        List<Candlestick> candlesticks = Mockito.mock(ArrayList.class);
        BDDMockito.given(candlesticks.size()).willReturn(1);

        Assertions.assertThrows(Exception.class,
                () -> {
                    Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getMeanValue",
                            List.class, int.class, PatternType.class);
                    getValueMethod.setAccessible(true);
                    getValueMethod.invoke(ruleEvaluator, candlesticks, 2, PatternType.LOW);
                    getValueMethod.setAccessible(false);
                });
        Mockito.verify(candlesticks, Mockito.times(1)).size();
    }

    @Test
    public void getMeanValue_GivenZeroIntervalShouldThrowException() {
        List<Candlestick> candlesticks = Mockito.mock(ArrayList.class);

        Assertions.assertThrows(Exception.class,
                () -> {
                    Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getMeanValue",
                            List.class, int.class, PatternType.class);
                    getValueMethod.setAccessible(true);
                    getValueMethod.invoke(ruleEvaluator, candlesticks, 0, PatternType.LOW);
                    getValueMethod.setAccessible(false);
                });
        Mockito.verify(candlesticks, Mockito.never()).size();
    }


    @Test
    public void getValue_GivenLowPatternTypeShouldReturnLowValue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Candlestick candlestick = Mockito.mock(Candlestick.class);
        BDDMockito.given(candlestick.getLow()).willReturn("1000");

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getValue", Candlestick.class, PatternType.class);
        getValueMethod.setAccessible(true);
        Object actual = getValueMethod.invoke(ruleEvaluator, candlestick, PatternType.LOW);
        getValueMethod.setAccessible(false);

        Assertions.assertEquals(actual, 1000D);
        Mockito.verify(candlestick, Mockito.times(1)).getLow();
        Mockito.verify(candlestick, Mockito.never()).getHigh();
        Mockito.verify(candlestick, Mockito.never()).getOpen();
        Mockito.verify(candlestick, Mockito.never()).getClose();
    }

    @Test
    public void getValue_GivenHighPatternTypeShouldReturnHighValue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Candlestick candlestick = Mockito.mock(Candlestick.class);
        BDDMockito.given(candlestick.getHigh()).willReturn("1000");

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getValue", Candlestick.class, PatternType.class);
        getValueMethod.setAccessible(true);
        Object actual = getValueMethod.invoke(ruleEvaluator, candlestick, PatternType.HIGH);
        getValueMethod.setAccessible(false);

        Assertions.assertEquals(actual, 1000D);
        Mockito.verify(candlestick, Mockito.times(1)).getHigh();
        Mockito.verify(candlestick, Mockito.never()).getLow();
        Mockito.verify(candlestick, Mockito.never()).getOpen();
        Mockito.verify(candlestick, Mockito.never()).getClose();
    }

    @Test
    public void getValue_GivenOpenPatternTypeShouldReturnOpenValue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Candlestick candlestick = Mockito.mock(Candlestick.class);
        BDDMockito.given(candlestick.getOpen()).willReturn("1000");

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getValue", Candlestick.class, PatternType.class);
        getValueMethod.setAccessible(true);
        Object actual = getValueMethod.invoke(ruleEvaluator, candlestick, PatternType.OPEN);
        getValueMethod.setAccessible(false);

        Assertions.assertEquals(actual, 1000D);
        Mockito.verify(candlestick, Mockito.times(1)).getOpen();
        Mockito.verify(candlestick, Mockito.never()).getHigh();
        Mockito.verify(candlestick, Mockito.never()).getLow();
        Mockito.verify(candlestick, Mockito.never()).getClose();
    }

    @Test
    public void getValue_GivenClosePatternTypeShouldReturnOpenClose() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Candlestick candlestick = Mockito.mock(Candlestick.class);
        BDDMockito.given(candlestick.getClose()).willReturn("1000");

        Method getValueMethod = RuleEvaluator.class.getDeclaredMethod("getValue", Candlestick.class, PatternType.class);
        getValueMethod.setAccessible(true);
        Object actual = getValueMethod.invoke(ruleEvaluator, candlestick, PatternType.CLOSE);
        getValueMethod.setAccessible(false);

        Assertions.assertEquals(actual, 1000D);
        Mockito.verify(candlestick, Mockito.times(1)).getClose();
        Mockito.verify(candlestick, Mockito.never()).getHigh();
        Mockito.verify(candlestick, Mockito.never()).getLow();
        Mockito.verify(candlestick, Mockito.never()).getOpen();
    }
}