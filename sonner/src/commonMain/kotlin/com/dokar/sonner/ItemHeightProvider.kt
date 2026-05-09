package com.dokar.sonner

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

@Stable
internal class ItemHeightProvider {
    /**
     * 当前 measure 阶段产生的高度，使用普通 Map 而非 mutableStateOf，
     * 避免在 LazyLayout.measure 中写入 Compose state 触发 "pending composition has not
     * been applied" 崩溃（KMMCompose Bugly Android #1259232）。
     */
    private var rawHeights: Map<Int, Int> = emptyMap()

    /**
     * 状态镜像，仅供外部监听者使用；只有数据真正变化才更新，并放在独立的快照里提交，
     * 防止与正在进行的 measure pass 互相干扰。
     */
    private val snapshotHeights = mutableStateOf<Map<Int, Int>>(emptyMap())

    fun updateItemHeights(heights: Map<Int, Int>) {
        if (rawHeights == heights) return
        val snapshotCopy = heights.toMap()
        rawHeights = snapshotCopy
        if (snapshotHeights.value == snapshotCopy) return
        // 在独立的可写快照里提交，避免在 LazyLayout.measure 进行中污染 Compose 全局快照，
        // 触发 SubcomposeLayout 在 setContent 阶段抛 ComposeRuntimeError。
        Snapshot.withMutableSnapshot {
            snapshotHeights.value = snapshotCopy
        }
    }

    fun get(layoutIndex: Int): Int {
        return rawHeights[layoutIndex] ?: snapshotHeights.value[layoutIndex] ?: 0
    }

    fun listen(layoutIndex: Int): Flow<Int> {
        return snapshotFlow { snapshotHeights.value[layoutIndex] }
            .mapNotNull { it }
            .distinctUntilChanged()
    }
}