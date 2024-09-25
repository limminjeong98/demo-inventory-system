package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalculatorTest {

    @Mock
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        // given
        given(calculator.add(anyInt(), anyInt()))
                .willAnswer(
                        invocation -> {
                            Integer a = invocation.getArgument(0);
                            Integer b = invocation.getArgument(1);
                            return a + b;
                        }
                );
    }

    @Test
    void onePlusTwoShouldReturnThree() {
        // when
        Integer a = 1;
        Integer b = 2;
        Integer result = calculator.add(a, b);

        // then
        assertEquals(result, Integer.valueOf(3));
        verify(calculator, only()).add(a, b);
    }

}