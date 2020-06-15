/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sharding.strategy.algorithm.sharding.range;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.apache.shardingsphere.infra.config.properties.ConfigurationProperties;
import org.apache.shardingsphere.sharding.strategy.route.standard.StandardShardingStrategy;
import org.apache.shardingsphere.sharding.strategy.route.value.ListRouteValue;
import org.apache.shardingsphere.sharding.strategy.route.value.RangeRouteValue;
import org.apache.shardingsphere.sharding.strategy.route.value.RouteValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class StandardRangeShardingAlgorithmTest {
    
    private StandardShardingStrategy shardingStrategy;
    
    @Before
    public void setUp() {
        StandardRangeShardingAlgorithm shardingAlgorithm = new StandardRangeShardingAlgorithm();
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.RANGE_LOWER_KEY, "10");
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.RANGE_UPPER_KEY, "45");
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.SHARDING_VOLUME_KEY, "10");
        shardingAlgorithm.init();
        shardingStrategy = new StandardShardingStrategy("order_id", shardingAlgorithm);
    }
    
    @Test
    public void assertPreciseDoSharding() {
        List<String> availableTargetNames = Lists.newArrayList("t_order_0", "t_order_1", "t_order_2", "t_order_3", "t_order_4", "t_order_5");
        List<RouteValue> shardingValues = Lists.newArrayList(new ListRouteValue<>("order_id", "t_order", Lists.newArrayList(0L, 1L, 2L, 4L, 17L, 25L, 45L)));
        Collection<String> actual = shardingStrategy.doSharding(availableTargetNames, shardingValues, new ConfigurationProperties(new Properties()));
        assertThat(actual.size(), is(4));
        assertTrue(actual.contains("t_order_0"));
        assertTrue(actual.contains("t_order_1"));
        assertTrue(actual.contains("t_order_2"));
        assertTrue(actual.contains("t_order_5"));
    }
    
    @Test
    public void assertRangeDoShardingWithoutLowerBound() {
        List<String> availableTargetNames = Lists.newArrayList("t_order_0", "t_order_1", "t_order_2", "t_order_3", "t_order_4", "t_order_5");
        List<RouteValue> shardingValues = Lists.newArrayList(new RangeRouteValue<>("order_id", "t_order", Range.lessThan(12L)));
        Collection<String> actual = shardingStrategy.doSharding(availableTargetNames, shardingValues, new ConfigurationProperties(new Properties()));
        assertThat(actual.size(), is(2));
        assertTrue(actual.contains("t_order_0"));
        assertTrue(actual.contains("t_order_1"));
    }
    
    @Test
    public void assertRangeDoShardingWithoutUpperBound() {
        List<String> availableTargetNames = Lists.newArrayList("t_order_0", "t_order_1", "t_order_2", "t_order_3", "t_order_4", "t_order_5");
        List<RouteValue> shardingValues = Lists.newArrayList(new RangeRouteValue<>("order_id", "t_order", Range.greaterThan(40L)));
        Collection<String> actual = shardingStrategy.doSharding(availableTargetNames, shardingValues, new ConfigurationProperties(new Properties()));
        assertThat(actual.size(), is(2));
        assertTrue(actual.contains("t_order_4"));
        assertTrue(actual.contains("t_order_5"));
    }
    
    @Test
    public void assertRangeDoSharding() {
        List<String> availableTargetNames = Lists.newArrayList("t_order_0", "t_order_1", "t_order_2", "t_order_3", "t_order_4", "t_order_5");
        List<RouteValue> shardingValues = Lists.newArrayList(new RangeRouteValue<>("order_id", "t_order", Range.closed(12L, 55L)));
        Collection<String> actual = shardingStrategy.doSharding(availableTargetNames, shardingValues, new ConfigurationProperties(new Properties()));
        assertThat(actual.size(), is(5));
        assertTrue(actual.contains("t_order_1"));
        assertTrue(actual.contains("t_order_2"));
        assertTrue(actual.contains("t_order_3"));
        assertTrue(actual.contains("t_order_4"));
        assertTrue(actual.contains("t_order_5"));
    }
    
    @Test
    public void assertGetAutoTablesAmount() {
        StandardRangeShardingAlgorithm shardingAlgorithm = new StandardRangeShardingAlgorithm();
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.RANGE_LOWER_KEY, "10");
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.RANGE_UPPER_KEY, "45");
        shardingAlgorithm.getProps().setProperty(StandardRangeShardingAlgorithm.SHARDING_VOLUME_KEY, "10");
        shardingAlgorithm.init();
        assertThat(shardingAlgorithm.getAutoTablesAmount(), is(6));
    }
}