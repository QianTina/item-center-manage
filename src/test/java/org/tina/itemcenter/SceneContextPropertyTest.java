package org.tina.itemcenter;

import net.jqwik.api.*;
import net.jqwik.api.constraints.LongRange;
import org.tina.itemcenter.common.context.SceneContext;
import org.tina.itemcenter.common.exception.SceneContextException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneContext 的属性测试
 * 
 * 使用 jqwik 进行基于属性的测试，验证 SceneContext 的正确性属性
 */
public class SceneContextPropertyTest {

    /**
     * 属性 25：ThreadLocal 清理
     * 
     * 验证需求：2.5
     * 
     * 对于任意 HTTP 请求，在请求处理结束后，SceneContext 中的场景 ID 必须被清理，
     * 避免内存泄漏和线程污染。
     * 
     * 测试策略：
     * 1. 在多个线程中设置不同的场景 ID
     * 2. 验证每个线程只能获取到自己设置的场景 ID
     * 3. 清理后验证无法获取场景 ID（抛出异常）
     * 4. 验证清理不影响其他线程
     * 
     * Feature: item-management-system, Property 25: ThreadLocal 清理
     */
    @Property(tries = 100)
    void threadLocalCleanup_MustClearSceneIdAfterRequest(
            @ForAll @LongRange(min = 1, max = 1000) Long sceneId1,
            @ForAll @LongRange(min = 1001, max = 2000) Long sceneId2,
            @ForAll @LongRange(min = 2001, max = 3000) Long sceneId3
    ) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            // 创建三个并发任务，每个任务在不同线程中操作 SceneContext
            List<Future<Boolean>> futures = new ArrayList<>();
            
            // 线程 1: 设置 sceneId1，验证，清理，验证清理成功
            futures.add(executor.submit(() -> {
                try {
                    // 设置场景 ID
                    SceneContext.setSceneId(sceneId1);
                    
                    // 验证可以获取到正确的场景 ID
                    Long retrievedId = SceneContext.getSceneId();
                    if (!sceneId1.equals(retrievedId)) {
                        return false;
                    }
                    
                    // 清理场景 ID
                    SceneContext.clear();
                    
                    // 验证清理后无法获取场景 ID（应该抛出异常）
                    try {
                        SceneContext.getSceneId();
                        return false; // 如果没有抛出异常，测试失败
                    } catch (SceneContextException e) {
                        return true; // 抛出异常是预期行为
                    }
                } catch (Exception e) {
                    return false;
                }
            }));
            
            // 线程 2: 设置 sceneId2，验证，清理，验证清理成功
            futures.add(executor.submit(() -> {
                try {
                    SceneContext.setSceneId(sceneId2);
                    Long retrievedId = SceneContext.getSceneId();
                    if (!sceneId2.equals(retrievedId)) {
                        return false;
                    }
                    
                    SceneContext.clear();
                    
                    try {
                        SceneContext.getSceneId();
                        return false;
                    } catch (SceneContextException e) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }));
            
            // 线程 3: 设置 sceneId3，验证，清理，验证清理成功
            futures.add(executor.submit(() -> {
                try {
                    SceneContext.setSceneId(sceneId3);
                    Long retrievedId = SceneContext.getSceneId();
                    if (!sceneId3.equals(retrievedId)) {
                        return false;
                    }
                    
                    SceneContext.clear();
                    
                    try {
                        SceneContext.getSceneId();
                        return false;
                    } catch (SceneContextException e) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }));
            
            // 等待所有任务完成并验证结果
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(), "ThreadLocal cleanup must work correctly in all threads");
            }
            
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
    
    /**
     * 属性 25：ThreadLocal 隔离性
     * 
     * 验证需求：2.5
     * 
     * 验证不同线程的 SceneContext 是完全隔离的，一个线程的操作不会影响其他线程。
     * 
     * 测试策略：
     * 1. 在多个线程中同时设置不同的场景 ID
     * 2. 验证每个线程只能获取到自己设置的场景 ID
     * 3. 验证一个线程的清理操作不影响其他线程
     */
    @Property(tries = 100)
    void threadLocalIsolation_EachThreadHasItsOwnSceneId(
            @ForAll @LongRange(min = 1, max = 10000) Long sceneId1,
            @ForAll @LongRange(min = 10001, max = 20000) Long sceneId2
    ) throws InterruptedException, ExecutionException {
        // 确保两个场景 ID 不同
        if (sceneId1.equals(sceneId2)) {
            return;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        try {
            // 线程 1: 设置 sceneId1 并保持
            Future<Boolean> future1 = executor.submit(() -> {
                try {
                    SceneContext.setSceneId(sceneId1);
                    latch.countDown();
                    
                    // 等待线程 2 也设置完成
                    latch.await(5, TimeUnit.SECONDS);
                    
                    // 验证仍然是自己的场景 ID
                    Long retrievedId = SceneContext.getSceneId();
                    boolean result = sceneId1.equals(retrievedId);
                    
                    SceneContext.clear();
                    return result;
                } catch (Exception e) {
                    return false;
                }
            });
            
            // 线程 2: 设置 sceneId2 并保持
            Future<Boolean> future2 = executor.submit(() -> {
                try {
                    SceneContext.setSceneId(sceneId2);
                    latch.countDown();
                    
                    // 等待线程 1 也设置完成
                    latch.await(5, TimeUnit.SECONDS);
                    
                    // 验证仍然是自己的场景 ID
                    Long retrievedId = SceneContext.getSceneId();
                    boolean result = sceneId2.equals(retrievedId);
                    
                    SceneContext.clear();
                    return result;
                } catch (Exception e) {
                    return false;
                }
            });
            
            // 验证两个线程都成功获取到各自的场景 ID
            assertTrue(future1.get(), "Thread 1 must get its own scene ID");
            assertTrue(future2.get(), "Thread 2 must get its own scene ID");
            
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
    
    /**
     * 属性 25：清理后无法获取场景 ID
     * 
     * 验证需求：2.5
     * 
     * 验证清理后尝试获取场景 ID 会抛出 SceneContextException。
     */
    @Property(tries = 100)
    void clearSceneId_GetAfterClearMustThrowException(
            @ForAll @LongRange(min = 1, max = 100000) Long sceneId
    ) {
        // 设置场景 ID
        SceneContext.setSceneId(sceneId);
        
        // 验证可以获取
        assertEquals(sceneId, SceneContext.getSceneId());
        
        // 清理
        SceneContext.clear();
        
        // 验证清理后无法获取（抛出异常）
        assertThrows(SceneContextException.class, () -> {
            SceneContext.getSceneId();
        }, "Getting scene ID after clear must throw SceneContextException");
    }
    
    /**
     * 属性 25：多次清理是安全的
     * 
     * 验证需求：2.5
     * 
     * 验证多次调用 clear() 不会导致错误。
     */
    @Property(tries = 100)
    void clearSceneId_MultipleClearIsSafe(
            @ForAll @LongRange(min = 1, max = 100000) Long sceneId
    ) {
        // 设置场景 ID
        SceneContext.setSceneId(sceneId);
        
        // 多次清理
        SceneContext.clear();
        SceneContext.clear();
        SceneContext.clear();
        
        // 验证清理后无法获取
        assertThrows(SceneContextException.class, () -> {
            SceneContext.getSceneId();
        });
    }
    
    /**
     * 属性 25：未设置场景 ID 时获取会抛出异常
     * 
     * 验证需求：2.5
     * 
     * 验证在未设置场景 ID 的情况下尝试获取会抛出 SceneContextException。
     */
    @Property(tries = 100)
    void getSceneId_WithoutSetMustThrowException() {
        // 确保清理状态
        SceneContext.clear();
        
        // 验证未设置时获取会抛出异常
        assertThrows(SceneContextException.class, () -> {
            SceneContext.getSceneId();
        }, "Getting scene ID without setting must throw SceneContextException");
    }
}
