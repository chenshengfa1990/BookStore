
package com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.android;

import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.objectpool.PoolableManager;

public class DrawingCachePoolManager implements PoolableManager<DrawingCache> {

    @Override
    public DrawingCache newInstance() {
        return null;
    }

    @Override
    public void onAcquired(DrawingCache element) {

    }

    @Override
    public void onReleased(DrawingCache element) {

    }

}
