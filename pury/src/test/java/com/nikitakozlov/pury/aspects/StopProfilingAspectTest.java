package com.nikitakozlov.pury.aspects;

import com.nikitakozlov.pury.annotations.StopProfiling;
import com.nikitakozlov.pury.internal.profile.Profiler;
import com.nikitakozlov.pury.internal.profile.ProfilingManager;
import com.nikitakozlov.pury.internal.profile.ProfilerId;
import com.nikitakozlov.pury.internal.profile.ProfilingManagerSetter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StopProfilingAspectTest {

    private static final int RUNS_COUNTER_5 = 5;
    private static final String PROFILE_NAME = "profileName";

    @Test
    public void weaveJoinPoint_TakesParametersFromStartProfilingAnnotationAndStartAsyncProfiler() throws Throwable {
        ProfilerId profilerId = new ProfilerId(PROFILE_NAME, RUNS_COUNTER_5);
        Profiler profiler = mock(Profiler.class);
        ProfilingManager asyncProfilingManager = mock(ProfilingManager.class);
        when(asyncProfilingManager.getProfiler(eq(profilerId)))
                .thenReturn(profiler);
        ProfilingManagerSetter.setInstance(asyncProfilingManager);

        JoinPoint joinPoint = mockJoinPoint("methodWithStopProfilingAnnotation");
        StopProfilingAspect aspect = new StopProfilingAspect();
        aspect.weaveJoinPoint(joinPoint);
        verify(asyncProfilingManager).getProfiler(eq(profilerId));
        //verify(profiler).startStage();
    }

    private JoinPoint mockJoinPoint(String methodName) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        when(methodSignature.getMethod()).thenReturn(this.getClass().getDeclaredMethod(methodName));
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        return joinPoint;
    }

    public void methodWithoutAnnotations() {}

    @StopProfiling(runsCounter = RUNS_COUNTER_5, profilerName = PROFILE_NAME)
    private void methodWithStopProfilingAnnotation() {}

}