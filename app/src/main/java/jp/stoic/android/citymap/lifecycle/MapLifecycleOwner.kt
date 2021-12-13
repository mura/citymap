package jp.stoic.android.citymap.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class MapLifecycleOwner : LifecycleOwner, DefaultLifecycleObserver {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private var activityState = Lifecycle.State.INITIALIZED
    private var mapState = Lifecycle.State.INITIALIZED

    override fun getLifecycle() = lifecycleRegistry

    override fun onCreate(owner: LifecycleOwner) {
        activityState = Lifecycle.State.CREATED
        mapState = Lifecycle.State.CREATED
    }

    override fun onStart(owner: LifecycleOwner) {
        activityState = Lifecycle.State.STARTED
        if (mapState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    override fun onResume(owner: LifecycleOwner) {
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

    override fun onPause(owner: LifecycleOwner) {
        activityState = Lifecycle.State.STARTED
        if (mapState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        activityState = Lifecycle.State.CREATED
        if (mapState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityState = Lifecycle.State.DESTROYED
        if (mapState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            mapState = Lifecycle.State.DESTROYED
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
        owner.lifecycle.removeObserver(this)
    }
}