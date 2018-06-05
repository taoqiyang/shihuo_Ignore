//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mousepaint.taoqiyang.com.ignore.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

public class LDThreadM {
    public static final String DEFAULT_SINGLE_POOL_NAME = "DEFAULT_SINGLE_POOL_NAME";
    private static LDThreadM.ThreadPoolProxy mLongPool = null;
    private static Object mLongLock = new Object();
    private static LDThreadM.ThreadPoolProxy mShortPool = null;
    private static Object mShortLock = new Object();
    private static LDThreadM.ThreadPoolProxy mDownloadPool = null;
    private static Object mDownloadLock = new Object();
    private static Map<String, LDThreadM.ThreadPoolProxy> mMap = new HashMap();
    private static Object mSingleLock = new Object();

    public LDThreadM() {
    }

    public static LDThreadM.ThreadPoolProxy getDownloadPool() {
        if(mDownloadPool == null) {
            Object var0 = mDownloadLock;
            synchronized(mDownloadLock) {
                if(mDownloadPool == null) {
                    mDownloadPool = new LDThreadM.ThreadPoolProxy(3, 3, 5L);
                }
            }
        }

        return mDownloadPool;
    }

    public static LDThreadM.ThreadPoolProxy getLongPool() {
        if(mLongPool == null) {
            Object var0 = mLongLock;
            synchronized(mLongLock) {
                if(mLongPool == null) {
                    mLongPool = new LDThreadM.ThreadPoolProxy(5, 5, 5L);
                }
            }
        }

        return mLongPool;
    }

    public static LDThreadM.ThreadPoolProxy getShortPool() {
        if(mShortPool == null) {
            Object var0 = mShortLock;
            synchronized(mShortLock) {
                if(mShortPool == null) {
                    mShortPool = new LDThreadM.ThreadPoolProxy(2, 2, 5L);
                }
            }
        }

        return mShortPool;
    }

    public static LDThreadM.ThreadPoolProxy getSinglePool() {
        return getSinglePool("DEFAULT_SINGLE_POOL_NAME");
    }

    public static LDThreadM.ThreadPoolProxy getSinglePool(String name) {
        LDThreadM.ThreadPoolProxy singlePool = (LDThreadM.ThreadPoolProxy)mMap.get(name);
        if(singlePool == null) {
            Object var2 = mSingleLock;
            synchronized(mSingleLock) {
                singlePool = (LDThreadM.ThreadPoolProxy)mMap.get(name);
                if(singlePool == null) {
                    singlePool = new LDThreadM.ThreadPoolProxy(1, 1, 5L);
                    mMap.put(name, singlePool);
                }
            }
        }

        return singlePool;
    }

    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mPool;
        private int mCorePoolSize;
        private int mMaximumPoolSize;
        private long mKeepAliveTime;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.mCorePoolSize = corePoolSize;
            this.mMaximumPoolSize = maximumPoolSize;
            this.mKeepAliveTime = keepAliveTime;
        }

        public synchronized void execute(Runnable run) {
            if(run != null) {
                if(this.mPool == null || this.mPool.isShutdown()) {
                    this.mPool = new ThreadPoolExecutor(this.mCorePoolSize, this.mMaximumPoolSize, this.mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), Executors.defaultThreadFactory(), new AbortPolicy());
                }

                this.mPool.execute(run);
            }
        }

        public synchronized void cancel(Runnable run) {
            if(this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.getQueue().remove(run);
            }

        }

        public synchronized boolean contains(Runnable run) {
            return this.mPool == null || this.mPool.isShutdown() && !this.mPool.isTerminating()?false:this.mPool.getQueue().contains(run);
        }

        public void stop() {
            if(this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.shutdownNow();
            }

        }

        public synchronized void shutdown() {
            if(this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.shutdownNow();
            }

        }
    }
}
