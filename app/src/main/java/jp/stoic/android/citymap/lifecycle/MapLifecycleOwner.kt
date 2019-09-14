package jp.stoic.android.citymap.lifecycle

import androidx.lifecycle.*

class MapLifecycleOwner : LifecycleOwner, LifecycleObserver {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private var activityState = Lifecycle.State.INITIALIZED
    private var mapState = Lifecycle.State.INITIALIZED

    override fun getLifecycle() = lifecycleRegistry

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        activityState = Lifecycle.State.CREATED
        mapState = Lifecycle.State.CREATED
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        activityState = Lifecycle.State.STARTED
        if (mapState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        activityState = Lifecycle.State.RESUMED
        if (mapState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        }
    }

    fun onStartStyleLoad() {
        if (mapState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapState = Lifecycle.State.CREATED
            if (activityState.isAtLeast(Lifecycle.State.CREATED)) {
                lifecycleRegistry.currentState = Lifecycle.State.CREATED
            }
        }
    }

    fun onStyleLoaded() {
        mapState = Lifecycle.State.RESUMED
        if (activityState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        activityState = Lifecycle.State.STARTED
        if (mapState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        activityState = Lifecycle.State.CREATED
        if (mapState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner) {
        activityState = Lifecycle.State.DESTROYED
        if (mapState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            mapState = Lifecycle.State.DESTROYED
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
        source.lifecycle.removeObserver(this)
    }
}