package com.example.videoweb.base.config;

import com.example.videoweb.base.listener.OrderStateListenerImpl;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.enums.OrderEvent;
import com.example.videoweb.domain.enums.OrderState;
import com.example.videoweb.mapper.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.EnumSet;

/**
 * @Author: chailei
 * @Date: 2024/10/16 12:35
 */
@Slf4j
@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Resource
    private OrderMapper orderMapper;

    /**
     * 配置状态机状态
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates().initial(OrderState.WAITING_CONFIRM).states(EnumSet.allOf(OrderState.class));
    }

    /**
     * 配置状态转换
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        log.info("状态机配置状态转换-开始");
        transitions.withExternal()
                .source(OrderState.WAITING_CONFIRM).target(OrderState.WAITING_COMPLETED).event(OrderEvent.CONFIRM_ORDER)
                .and().withExternal()
                .source(OrderState.WAITING_COMPLETED).target(OrderState.COMPLETED).event(OrderEvent.DONE_ORDER)
                .and().withExternal()
                .source(OrderState.WAITING_COMPLETED).target(OrderState.FAILED).event(OrderEvent.CANCEL_ORDER)
                .and().withExternal()
                .source(OrderState.WAITING_COMPLETED).target(OrderState.WAITING_CONFIRM).event(OrderEvent.BACK_ORDER);
    }

    /**
     * 配置状态机监听
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        config.withConfiguration().listener(new StateMachineListenerAdapter<OrderState, OrderEvent>() {
            @Override//1、第一次状态机状态改变监听， 3、第一次状态机状态改变监听
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                log.info("状态机 from： {} to： {}", from, to);
            }

            @Override//4、状态机发送消息失败失败时
            public void eventNotAccepted(Message<OrderEvent> event) {
                log.warn("事件 {} 不被允许", event.getHeaders().get("payload"));
            }
        });
    }

    /**
     * 持久化配置
     * 实际使用中，可以配合redis等，进行持久化操作
     * @return
     */
    @Bean
    public DefaultStateMachinePersister persister(){
        return new DefaultStateMachinePersister<>(new StateMachinePersist<Object, Object, Order>() {
            @Override
            public void write(StateMachineContext<Object, Object> context, Order order) throws Exception {
                orderMapper.updateById(order); //4.持久化改变状态机状态
            }

            @Override
            public StateMachineContext<Object, Object> read(Order appOrder) throws Exception {
                //2、状态机持久化恢复状态 此处直接获取DB中order的状态
                Order dbOrder = orderMapper.selectById(appOrder.getOrderId());
                return new DefaultStateMachineContext(dbOrder.getOrderStatus(), null, null, null);
            }
        });
    }
}
